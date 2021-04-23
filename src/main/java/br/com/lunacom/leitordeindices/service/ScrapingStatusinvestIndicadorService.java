package br.com.lunacom.leitordeindices.service;

import br.com.lunacom.leitordeindices.domain.Ativo;
import br.com.lunacom.leitordeindices.domain.Indicador;
import br.com.lunacom.leitordeindices.domain.IndicadorAno;
import br.com.lunacom.leitordeindices.domain.IndicadorResultado;
import javassist.tools.rmi.ObjectNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

import static br.com.lunacom.leitordeindices.util.Mensagens.ATIVO_NAO_EXISTE;

@Slf4j
@Service
public class ScrapingStatusinvestIndicadorService implements ScrapingIndicador {

    @Autowired
    protected AtivoService ativoService;

    private final String URL_BASE = "https://statusinvest.com.br";
    private final By xpathFilhoDoNodo = By.xpath("./child::*");

    @Override
    public void executar(List<String> listaAtivos, Boolean visivel) throws ObjectNotFoundException {
        loop(listaAtivos, 0, visivel);
    }

    @Override
    public void executar(List<String> listaAtivos, Integer anoReferencia, Boolean visivel) throws ObjectNotFoundException {
        loop(listaAtivos, anoReferencia, visivel);
    }

    protected void loop(List<String> listaAtivos, Integer anoReferencia, Boolean invisivel) {
        List<String> ativosPendentes = new ArrayList<>(listaAtivos);
        FirefoxOptions options = new FirefoxOptions();
        options.setHeadless(invisivel);

        WebDriver driver = new FirefoxDriver(options);
//        WebDriverWait wait = new WebDriverWait(driver, 10);
//        driver.get(URL_BASE);
        try {
            ativosPendentes.forEach(a -> {
                scrapingIndicador(a, driver);
                log.info(String.format("<<<<< Scraping finalizado para %s >>>>>", a));
                listaAtivos.remove(a);
            });

        } finally {
            driver.quit();
        }
    }

    private void scrapingIndicador(String codigoAtivo, WebDriver driver) {
        try {
            final Ativo ativo = ativoService.searchAtivoByCodigo(codigoAtivo);

            String path = String.format("%s/%s/%s", URL_BASE, "acoes", ativo.getCodigo());
            driver.get(path);

            clicaNoBotãoParaListarHistorico(driver);

            final List<WebElement> indicators = buscarDadosHistoricoIndicadores(driver);


// loop para cada tipo de indicador

            final WebElement webElement = indicators.get(0);
            final WebElement div = webElement.findElement(xpathFilhoDoNodo);
            final List<WebElement> elements = div.findElements(xpathFilhoDoNodo);

            final WebElement nomeIndicador = elements.get(0);
            final List<WebElement> nomeIndicadorElements = nomeIndicador.findElements(xpathFilhoDoNodo);

            final List<String> listaNomeIndicadores = nomeIndicadorElements.stream().map(e -> limparNome(e.getText())).collect(Collectors.toList());

            final WebElement valorIndicador = elements.get(1);
            final WebElement divContainerValores = valorIndicador.findElement(By.xpath("./child::*"));
            final List<WebElement> listaValorPorAno = divContainerValores.findElements(By.xpath("./child::*"));

//            IndicadorAno ano = new IndicadorAno();
//            final String strAnos = listaValorPorAno.get(0).getText();
//            final List<String> listaAnos = Arrays.asList(strAnos.split("\n"));

            final String strAnos = listaValorPorAno.get(0).getText();
            final List<String> listaAnos = Arrays.asList(strAnos.split("\n"));
            Map<String,IndicadorAno> listaAnosHashMap = new HashMap<String,IndicadorAno>();
//            listaAnos.stream().map(str -> listaAnosHashMap.put(str, new IndicadorAno(Integer.valueOf(str), ativo))).collect(Collectors.toList());

            listaAnos
                    .stream()
                    .skip(1)
                    .map(str -> listaAnosHashMap.put(str, new IndicadorAno(Integer.valueOf(str), ativo)))
                    .collect(Collectors.toList());

            IndicadorResultado resultado = new IndicadorResultado();
            Indicador indicador = new Indicador();

            //
//listaValorPorAno.get(3).getText();

        } catch (ObjectNotFoundException e) {
            log.error(String.format(ATIVO_NAO_EXISTE, codigoAtivo));
            e.printStackTrace();
        }

    }

    private String limparNome(String text) {
        return text.split("\\n")[0].trim();
    }

    private void clicaNoBotãoParaListarHistorico(WebDriver driver) {
        final WebElement element = driver.findElement(By.className("btn-indicator-type"));
        final List<WebElement> button = element.findElements(By.tagName("button"));
        button.get(1).click();
    }

    private List<WebElement> buscarDadosHistoricoIndicadores(WebDriver driver) {
        final WebElement container = driver.findElement(By.className("indicator-historical-container"));
        return container.findElements(By.className("indicators"));
    }
}
