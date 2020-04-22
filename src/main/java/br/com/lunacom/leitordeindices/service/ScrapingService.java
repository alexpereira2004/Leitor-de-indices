package br.com.lunacom.leitordeindices.service;

import br.com.lunacom.leitordeindices.domain.Ativo;
import br.com.lunacom.leitordeindices.domain.Cotacao;
import javassist.tools.rmi.ObjectNotFoundException;
import lombok.extern.java.Log;
import lombok.extern.log4j.Log4j;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.remote.RemoteWebElement;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.xml.bind.ValidationException;
import java.text.NumberFormat;
import java.text.ParseException;
import java.time.Duration;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


@Log
@Service
public class ScrapingService {

    @Autowired
    private AtivoService ativoService;

    @Autowired
    private CotacaoService cotacaoService;

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
                    final Ativo ativoPesquisado = ativoService.getNomeETipo(nomeDaEmpresa);
                    final Ativo ativo = ativoService.searchAtivo(ativoPesquisado.getNome(), ativoPesquisado.getTipo());
                    Cotacao cotacao = new Cotacao();
                    NumberFormat nf = NumberFormat.getInstance(Locale.GERMANY);
                    cotacao.setPreco(nf.parse(preco_cotacao).doubleValue());
                    cotacao.setAtivo(ativo);
                    cotacaoService.insert(cotacao);
                } catch (ObjectNotFoundException e) {
                    e.printStackTrace();
                } catch (ValidationException e) {
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
}
