package br.com.lunacom.leitordeindices.service;

import br.com.lunacom.leitordeindices.domain.Ativo;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.RemoteWebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class ScrapingAbstract {

    @Autowired
    protected AtivoService ativoService;

    @Autowired
    protected CotacaoService cotacaoService;

    protected void pesquisarCaminhoDoAtivo(Ativo ativo, WebDriver driver, WebDriverWait wait) {
        driver.findElement(By.cssSelector(".searchText")).clear();
        driver.findElement(By.cssSelector(".searchText")).sendKeys(ativo.getCodigo());
        driver.findElement(By.cssSelector(".searchGlassIcon")).click();
        wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(".quatesTable")));
        final WebElement indices = driver.findElement(By.cssSelector(".quatesTable"));
        final List<WebElement> linkResultados = ((RemoteWebElement) indices).findElements(By.tagName("a"));
        wait.until(ExpectedConditions.visibilityOf(linkResultados.get(0)));
        ativo.setCaminho(linkResultados.get(0).getAttribute("href"));
        ativoService.update(ativo);
    }

    protected List<String> parseAtivos(String referenciaCodigoAtivo) {
        List<String> ativos = new ArrayList<>(Arrays.asList(referenciaCodigoAtivo.split(",")));
        return ativos;
    }


    protected void fecharAvisoPrivacidade(WebDriver driver) {
        final List<WebElement> elements = driver.findElements(By.cssSelector("#onetrust-accept-btn-handler"));
        if (elements.isEmpty()) {
            return;
        }
        elements.get(0).click();
    };
}
