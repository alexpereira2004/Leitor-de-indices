package br.com.lunacom.leitordeindices.service;

import br.com.lunacom.leitordeindices.domain.Ativo;
import br.com.lunacom.leitordeindices.domain.Cotacao;
import br.com.lunacom.leitordeindices.util.DataUtil;
import javassist.tools.rmi.ObjectNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.remote.RemoteWebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.NoResultException;
import java.sql.Timestamp;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ScrapingHistoricoAtivosService implements Scraping {
    @Autowired
    private AtivoService ativoService;

    @Autowired
    private CotacaoService cotacaoService;

    private final String origem = "historico-ativo";

    @Override
    public void executar(String referenciaCodigoAtivo, Date dataInicioPesquisa)  {
        WebDriver driver = new FirefoxDriver();
        WebDriverWait wait = new WebDriverWait(driver, 10);
        driver.get("https://br.investing.com/equities/brazil");

        try {

            List<String> ativos = new ArrayList<>();

            ativos.add("EZTC3");

            ativos.forEach(a -> {
                scrapingAtivo(a, dataInicioPesquisa, driver, wait);
            });

        } finally {
            driver.quit();
        }
    }

    private void scrapingAtivo(String codigoAtivo, Date dataInicioPesquisa, WebDriver driver, WebDriverWait wait) {
        try {
            final Ativo ativo = ativoService.searchAtivoByCodigo(codigoAtivo);
            this.pesquisar(ativo, driver, wait);
            this.filtrar(dataInicioPesquisa, driver);

            List<WebElement> trElements = bucarResultados(driver, wait);
            salvarCotacoesPorAtivo(ativo, dataInicioPesquisa, trElements);
        } catch (NoResultException e) {
            log.warn(e.getMessage(), codigoAtivo);
        } catch (ObjectNotFoundException e) {
            log.error(String.format("Código do ativo não existe: $s", codigoAtivo));
            e.printStackTrace();
        }
    }

    private void pesquisar(Ativo ativo, WebDriver driver, WebDriverWait wait) {
        driver.findElement(By.cssSelector(".searchText")).clear();
        driver.findElement(By.cssSelector(".searchText")).sendKeys(ativo.getCodigo());
        driver.findElement(By.cssSelector(".searchGlassIcon")).click();
        final WebElement indices = driver.findElement(By.cssSelector(".quatesTable"));
        final List<WebElement> linkResultados = ((RemoteWebElement) indices).findElements(By.tagName("a"));
        wait.until(ExpectedConditions.visibilityOf(linkResultados.get(0)));
        final String href = linkResultados.get(0).getAttribute("href");
        driver.get(href+"-historical-data");
    }

    private void filtrar(Date dataInicioPesquisa, WebDriver driver) {
        driver.findElement(By.cssSelector(".historicDate")).click();
        final WebElement startDate = driver.findElement(By.id("startDate"));
        startDate.clear();
        startDate.sendKeys(DataUtil.formatAsDayMonthYearSlash(dataInicioPesquisa));
        driver.findElement(By.id("applyBtn")).click();
    }

    private List<WebElement> bucarResultados(WebDriver driver, WebDriverWait wait) {
        log.info("Carregou a tabela");
        final WebElement table = driver.findElement(By.id("curr_table"));
        final WebElement tableBody = table.findElement(By.tagName("tbody"));
        log.info("Aguardando corpo da tabela");
        wait.until(ExpectedConditions.presenceOfElementLocated(By.className("first")));
        log.info("Corpo da tabela carregado");
        List<WebElement> trs = tableBody.findElements(By.tagName("tr"));
        if (trs.size() == 0) {
            throw new NoResultException();
        }
        return trs;
    }

    private void salvarCotacoesPorAtivo(Ativo ativo, Date dataInicioPesquisa, List<WebElement> trElements) {
        NumberFormat nf = NumberFormat.getInstance(Locale.GERMANY);

        List<Cotacao> cotacoes = new ArrayList<>();

        final List<Cotacao> cotacoesExistentes = cotacaoService.findAllByAtivoAndReferenciaAfter(ativo, dataInicioPesquisa);

        trElements.stream().forEach(tr -> {
            Cotacao c = new Cotacao();
            final List<WebElement> tdElements = tr.findElements(By.tagName("td"));
            try {
                c.setAtivo(ativo);
                final Date date = DataUtil.parseDayMonthYearDot(tdElements.get(0).getText());
                Timestamp ts = new Timestamp(date.getTime());
                c.setReferencia(ts);
                c.setPreco(nf.parse(tdElements.get(1).getText()).doubleValue());
                c.setAbertura(nf.parse(tdElements.get(2).getText()).doubleValue());
                c.setMaxima(nf.parse(tdElements.get(3).getText()).doubleValue());
                c.setMinima(nf.parse(tdElements.get(4).getText()).doubleValue());
                c.setVolume(tdElements.get(5).getText());
                c.setOrigem("historico-ativo");
                c.setImportacao(new Date());
                final String variacao = tdElements.get(6).getText().replace("%","");
                c.setVariacao(nf.parse(variacao).doubleValue());
                if (compararSeExiste(c, cotacoesExistentes)) {
                    cotacoes.add(c);
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        });

        log.info(String.format("Total de cotações encontradas para %s: %s",ativo.getCodigo(), cotacoes.size()));
        cotacaoService.insertAll(cotacoes);
    }

    @Override
    public String getOrigem() {
        return origem;
    }

    private boolean compararSeExiste(Cotacao cotacao, List<Cotacao> cotacoesExistentes) {
        final List<Cotacao> collect = cotacoesExistentes
                .stream()
                .filter(c -> c.equals(cotacao))
                .collect(Collectors.toList());
        return collect.isEmpty();
    }
}


