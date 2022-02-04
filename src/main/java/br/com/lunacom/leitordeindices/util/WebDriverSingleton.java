package br.com.lunacom.leitordeindices.util;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;

public class WebDriverSingleton {
    private static WebDriver webDriver;

    private WebDriverSingleton() {}

    public static WebDriver getInstance(Boolean invisivel) {
        if(webDriver == null) {
            if (webDriver == null) {
                FirefoxOptions options = new FirefoxOptions();
                options.setHeadless(invisivel);
                webDriver = new FirefoxDriver(options);
            }
        }
        return webDriver;
    }

}
