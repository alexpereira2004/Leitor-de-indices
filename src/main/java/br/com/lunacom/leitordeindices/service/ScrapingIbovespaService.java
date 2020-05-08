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

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;


@Slf4j
@Service
public class ScrapingIbovespaService {

    @Autowired
    private AtivoService ativoService;

    @Autowired
    private CotacaoService cotacaoService;

    private final String origem = "ibovespa-diario";

    public void executar() {
        System.setProperty("webdriver.gecko.driver", "C:/WebDriver/bin/geckodriver.exe");

        WebDriver driver = new FirefoxDriver();
        WebDriverWait wait = new WebDriverWait(driver, 10);
        try {
            driver.get("https://br.investing.com/equities/brazil");
            final WebElement indices = driver.findElement(By.id("cross_rate_markets_stocks_1"));
            driver.findElement(By.id("cross_rate_markets_stocks_1"));

            final List<WebElement> trs = ((RemoteWebElement) indices).findElements(By.tagName("tr"));

            trs
                .stream()
                .skip(1)
                .forEach(tr -> {
                    String cod_referencia = tr.getAttribute("id").replace("pair_", "");
                    final String nomeDaEmpresa = tr.findElement(By.className("plusIconTd")).getText();
                    final String preco_cotacao = tr.findElement(By.className("pid-" + cod_referencia + "-last")).getText();
                    final String volume = tr.findElement(By.className("pid-" + cod_referencia + "-turnover")).getText();
                try {
                    final Ativo ativo = ativoService.searchAtivo(nomeDaEmpresa);
                    Cotacao cotacao = new Cotacao();
                    NumberFormat nf = NumberFormat.getInstance(Locale.GERMANY);
                    cotacao.setPreco(nf.parse(preco_cotacao).doubleValue());
                    cotacao.setAtivo(ativo);
                    cotacao.setVolume(volume);
                    cotacao.setOrigem(this.origem);
                    cotacaoService.insert(cotacao);
                } catch (ObjectNotFoundException e) {
                    e.printStackTrace();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                System.out.println(tr.getText());
            });

        } finally {
            driver.quit();
        }
    }

    public void pesquisarHistoricoAtivo(String codigoAtivo, Date dataInicioPesquisa) throws ObjectNotFoundException {
        WebDriver driver = new FirefoxDriver();
        WebDriverWait wait = new WebDriverWait(driver, 10);
        try {
            driver.get("https://br.investing.com/equities/brazil");

            driver.findElement(By.cssSelector(".searchText")).clear();
            driver.findElement(By.cssSelector(".searchText")).sendKeys(codigoAtivo);
            driver.findElement(By.cssSelector(".searchGlassIcon")).click();
            final WebElement indices = driver.findElement(By.cssSelector(".quatesTable"));
            final List<WebElement> linkResultados = ((RemoteWebElement) indices).findElements(By.tagName("a"));
            wait.until(ExpectedConditions.visibilityOf(linkResultados.get(0)));
            final String href = linkResultados.get(0).getAttribute("href");
            driver.get(href+"-historical-data");
            driver.findElement(By.cssSelector(".historicDate")).click();
            final WebElement startDate = driver.findElement(By.id("startDate"));
            startDate.clear();
            startDate.sendKeys(DataUtil.formatAsDayMonthYearSlash(dataInicioPesquisa));

            driver.findElement(By.id("applyBtn")).click();
            NumberFormat nf = NumberFormat.getInstance(Locale.GERMANY);

            final Ativo ativo = ativoService.searchAtivoByCodigo(codigoAtivo);

            List<Cotacao> cotacoes = new ArrayList<>();
            log.info("Carregou a tabela");
            final WebElement table = driver.findElement(By.id("curr_table"));
            final WebElement tableBody = table.findElement(By.tagName("tbody"));
            log.info("Aguardando corpo da tabela");
            wait.until(ExpectedConditions.presenceOfElementLocated(By.className("first")));
            log.info("Corpo da tabela carregado");
            final List<WebElement> trElements = tableBody.findElements(By.tagName("tr"));

            trElements.stream().forEach(tr -> {
                Cotacao c = new Cotacao();
                final List<WebElement> tdElements = tr.findElements(By.tagName("td"));
                try {
                    c.setAtivo(ativo);
                    c.setReferencia(DataUtil.parseDayMonthYearDot(tdElements.get(0).getText()));
                    c.setPreco(nf.parse(tdElements.get(1).getText()).doubleValue());
                    c.setAbertura(nf.parse(tdElements.get(2).getText()).doubleValue());
                    c.setMaxima(nf.parse(tdElements.get(3).getText()).doubleValue());
                    c.setMinima(nf.parse(tdElements.get(4).getText()).doubleValue());
                    c.setVolume(tdElements.get(5).getText());
                    c.setOrigem("historico-ativo");
                    c.setImportacao(new Date());
                    final String variacao = tdElements.get(6).getText().replace("%","");
                    c.setVariacao(nf.parse(variacao).doubleValue());
                    cotacoes.add(c);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            });
            log.info(String.format("Total de cotações encontradas para %s: %s",codigoAtivo, cotacoes.size()));
            cotacaoService.insertAll(cotacoes);
        } finally {
            driver.quit();
        }

    }
}
