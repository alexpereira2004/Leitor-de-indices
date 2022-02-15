package br.com.lunacom.leitordeindices.util;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;

import java.util.Objects;

public class WebDriverSingleton {
    private static WebDriver webDriver;

    private WebDriverSingleton() {}

    public static WebDriver getInstance(Boolean invisivel) {
        if (verificarNovaInstancia()) {
            FirefoxOptions options = new FirefoxOptions();
            options.setHeadless(invisivel);
            webDriver = new FirefoxDriver(options);
        }
        return webDriver;
    }

    private static boolean verificarNovaInstancia() {
        boolean response = false;
        if (Objects.isNull(webDriver)) {
            response = true;
        } else {
            try {
                webDriver.getCurrentUrl();
            } catch (Exception e) {
//                webDriver.quit();
                response = true;
            }
        }
        return response;
    }

}
