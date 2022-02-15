package br.com.lunacom.leitordeindices.util;

import org.openqa.selenium.WebDriver;

import javax.annotation.PreDestroy;

public class TerminateBean {
    @PreDestroy
    public void onDestroy() throws Exception {
        System.out.println("Spring Container is destroyed!");
        WebDriver driver = WebDriverSingleton.getInstance(true);
        driver.quit();
    }
}
