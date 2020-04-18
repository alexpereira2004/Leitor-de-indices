package br.com.lunacom.leitordeindices.service;

import lombok.extern.java.Log;
import lombok.extern.log4j.Log4j;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.remote.RemoteWebElement;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;



@Log
@Service
public class ScrapingService {
    public void executar() {
        System.setProperty("webdriver.gecko.driver", "C:/WebDriver/bin/geckodriver.exe");

        WebDriver driver = new FirefoxDriver();
        WebDriverWait wait = new WebDriverWait(driver, 10);
        try {
            driver.get("https://br.investing.com/equities/brazil");
            final WebElement indices = driver.findElement(By.id("cross_rate_markets_stocks_1"));
            driver.findElement(By.id("cross_rate_markets_stocks_1"));

            final List<WebElement> trs = ((RemoteWebElement) indices).findElements(By.tagName("tr"));

            trs.stream().forEach(tr -> {
                System.out.println(tr.getText());
            });

        } finally {
            driver.quit();
        }
    }
}
