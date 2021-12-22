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
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.stereotype.Service;

import javax.persistence.NoResultException;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

@Slf4j
@Service
public class ScrapingCotacaoAtualService extends ScrapingInvestingComAbstract implements Scraping {

    @Override
    public void executar(String referenciaCodigoAtivo, Date diaAtual) throws ObjectNotFoundException {
        System.setProperty("webdriver.gecko.driver", "D:/Java/Infra/geckodriver-v0.30.0-win64/geckodriver.exe");
        FirefoxOptions options = new FirefoxOptions();
        options.setHeadless(true);
        WebDriver driver = new FirefoxDriver(options);
        WebDriverWait wait = new WebDriverWait(driver, 10);
        try {
            List<String> ativos = parseAtivos(referenciaCodigoAtivo);
            ativos.forEach(a -> {
                scrapingAtivo(a, driver, wait);
                log.info("Scraping finalizado para "+a);
            });
        } finally {
            driver.quit();
        }
    }

    @Override
    public void executar(List<String> listaAtivos, Date dataReferencia, Boolean v) throws ObjectNotFoundException {

    }

    @Override
    public String getOrigem() {
        return "atual";
    }

    private void scrapingAtivo(String codigoAtivo, WebDriver driver, WebDriverWait wait) {
        try {
            final Ativo ativo = ativoService.searchAtivoByCodigo(codigoAtivo);
            this.pesquisarCotacao(ativo, driver, wait);
            salvarCotacao(ativo);
        } catch (NoResultException e) {
            log.warn(e.getMessage(), codigoAtivo);
        } catch (ObjectNotFoundException e) {
            log.error(String.format("Código do ativo não existe: $s", codigoAtivo));
            e.printStackTrace();
        }
    }

    private void pesquisarCotacao(Ativo ativo, WebDriver driver, WebDriverWait wait) {
        if (Objects.isNull(ativo.getCaminho())) {
            pesquisarCaminhoDoAtivo(ativo, driver, wait);
        }
        NumberFormat nf = NumberFormat.getInstance(Locale.GERMANY);
        driver.get(ativo.getCaminho());
        wait.until(ExpectedConditions.presenceOfElementLocated(By.id("quotes_summary_current_data")));
        final WebElement quotes_summary_current_data = driver.findElement(By.id("quotes_summary_current_data"));
        final List<WebElement> spans = quotes_summary_current_data.findElements(By.tagName("span"));
        Cotacao cotacao = new Cotacao();

        final Date dataAtual = DataUtil.setEndTime(new Date(), 0, 0, 0, 0);

        try {
            cotacao.setPreco(nf.parse(spans.get(0).getText()).doubleValue());
            cotacao.setOrigem(getOrigem());
            final String variacao = spans.get(3).getText()
                    .replace("+","")
                    .replace("%","");
            cotacao.setVariacao(nf.parse(variacao).doubleValue());
            cotacao.setAtivo(ativo);
            cotacao.setReferencia(dataAtual);
        } catch (ParseException e) {
            e.printStackTrace();
        }


        final List<Cotacao> cotacoesDoDia = cotacaoService.findAllByAtivoAndReferencia(ativo, dataAtual);
        if (cotacoesDoDia.size() > 0) {
            cotacao.setId(cotacoesDoDia.get(0).getId());
        }
        cotacaoService.insert(cotacao);

    }

    private void salvarCotacao(Ativo ativo) {
    }
}
