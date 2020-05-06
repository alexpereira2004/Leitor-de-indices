package br.com.lunacom.leitordeindices.service;

import br.com.lunacom.leitordeindices.domain.Ativo;
import br.com.lunacom.leitordeindices.domain.Cotacao;
import br.com.lunacom.leitordeindices.util.DataUtil;
import javassist.tools.rmi.ObjectNotFoundException;
import lombok.extern.java.Log;
import lombok.extern.log4j.Log4j;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.remote.RemoteWebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.xml.bind.ValidationException;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


@Log
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

    public void pesquisarHistoricoAtivo(String codigoAtivo) throws ObjectNotFoundException {
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
            startDate.sendKeys("01/01/2020");

            driver.findElement(By.id("applyBtn")).click();
            NumberFormat nf = NumberFormat.getInstance(Locale.GERMANY);

            final Ativo ativo = ativoService.searchAtivoByCodigo(codigoAtivo);

            List<Cotacao> cotacoes = new ArrayList<>();
            final WebElement results_box = driver.findElement(By.id("results_box"));
            final List<WebElement> trElements = results_box.findElements(By.tagName("tr"));
            trElements.stream().skip(1).forEach(tr -> {
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

            cotacaoService.insertAll(cotacoes);
        } finally {
            driver.quit();
        }

    }
}
