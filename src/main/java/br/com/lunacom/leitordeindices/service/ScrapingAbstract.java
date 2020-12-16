package br.com.lunacom.leitordeindices.service;

import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Value;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Slf4j
public class ScrapingAbstract {
    @Value("${webdriver.gecko.driver}")
    private String webdriverGeckoDriver;

    protected void loop(List<String> listaAtivos, Date dataReferencia, Boolean invisivel) {
        List<String> ativosPendentes = new ArrayList<>(listaAtivos);
        FirefoxOptions options = new FirefoxOptions();
        options.setHeadless(invisivel);

        WebDriver driver = new FirefoxDriver(options);
        WebDriverWait wait = new WebDriverWait(driver, 10);
        driver.get(getUrlBase());
        try {
            ativosPendentes.forEach(a -> {
                scrapingAtivo(a, dataReferencia, driver, wait);
                log.info(String.format("<<<<< Scraping finalizado para %s >>>>>", a));
                listaAtivos.remove(a);
            });

        } finally {
            driver.quit();
        }
    }

    protected String getUrlBase() {
        return "" ;
    }

    protected String scrapingAtivo(String a, Date dataReferencia, WebDriver driver, WebDriverWait wait) {
        return a;
    }

}
