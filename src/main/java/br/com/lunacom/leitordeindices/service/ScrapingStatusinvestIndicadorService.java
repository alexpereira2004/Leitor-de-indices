package br.com.lunacom.leitordeindices.service;

import br.com.lunacom.leitordeindices.domain.Ativo;
import br.com.lunacom.leitordeindices.domain.Indicador;
import br.com.lunacom.leitordeindices.domain.IndicadorResultado;
import br.com.lunacom.leitordeindices.repositories.IndicadorRepository;
import br.com.lunacom.leitordeindices.repositories.IndicadorResultadoRepository;
import javassist.tools.rmi.ObjectNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static br.com.lunacom.leitordeindices.util.Mensagens.ATIVO_NAO_EXISTE;

@Slf4j
@Service
@Transactional
public class ScrapingStatusinvestIndicadorService implements ScrapingIndicador {

    @Autowired
    protected AtivoService ativoService;

    @Autowired
    IndicadorRepository indicadorRepository;

    @Autowired
    IndicadorResultadoRepository indicadorResultadoRepository;

    private final String URL_BASE = "https://statusinvest.com.br";
    private final By xpathFilhoDoNodo = By.xpath("./child::*");
    private List<Indicador> indicadores;

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
        WebDriverWait wait = new WebDriverWait(driver, 10);
//        driver.get(URL_BASE);
        try {
            indicadores = indicadorRepository.findAll();
            ativosPendentes.forEach(a -> {
                scrapingIndicador(a, driver, wait);
                log.info(String.format("<<<<< Scraping finalizado para %s >>>>>", a));
                listaAtivos.remove(a);
            });
        } finally {
            driver.quit();
        }
    }

    private void scrapingIndicador(String codigoAtivo, WebDriver driver, WebDriverWait wait) {
        try {
            final Ativo ativo = ativoService.searchAtivoByCodigo(codigoAtivo);
            String path = String.format("%s/%s/%s", URL_BASE, "acoes", ativo.getCodigo());
            driver.get(path);
            fechaPopupAnuncios(driver, wait);
            clicaNoBotaoParaListarHistorico(driver);
            final List<WebElement> resultadosTableHtml = buscarDadosHistoricoIndicadores(driver);
            indicadorResultadoRepository.deleteIndicadorResultadoByAtivo(ativo);
            resultadosTableHtml
                    .stream()
                    .map(e -> lerTabelaHtmlESalvarDados(e, ativo))
                    .collect(Collectors.toList());
        } catch (ObjectNotFoundException e) {
            log.error(String.format(ATIVO_NAO_EXISTE, codigoAtivo));
            e.printStackTrace();
        }
    }

    private void fechaPopupAnuncios(WebDriver driver, WebDriverWait wait) {
        final List<WebElement> popupAdvertising = driver.findElements(By.className("popup-fixed"));
        if (!popupAdvertising.isEmpty()) {
            final WebElement elementoAlvo = popupAdvertising.get(0);
            wait.until(ExpectedConditions.visibilityOf(elementoAlvo));
            final List<WebElement> advertisingElement = elementoAlvo.findElements(By.className("advertising"));
            final List<WebElement> div = advertisingElement.get(0).findElements(xpathFilhoDoNodo);
            final List<WebElement> buttonClose = div.get(0).findElements(xpathFilhoDoNodo);
            wait.until(ExpectedConditions.elementToBeClickable(buttonClose.get(0)));
            buttonClose.get(0).click();
            wait.until(ExpectedConditions.invisibilityOf(elementoAlvo));
            log.info("Fechou um popup de anúncio");
        }
    }

    private boolean lerTabelaHtmlESalvarDados(WebElement webElement, Ativo ativo) {
        final WebElement div = webElement.findElement(xpathFilhoDoNodo);
        final List<WebElement> elements = div.findElements(xpathFilhoDoNodo);

        // Indicadores
        final WebElement nomeIndicadorLido = elements.get(0);
        final List<WebElement> nomeIndicadorLidoElements = nomeIndicadorLido.findElements(xpathFilhoDoNodo);
        final List<String> listaNomeIndicadoresLidos = nomeIndicadorLidoElements.stream().map(e -> limparNome(e.getText())).collect(Collectors.toList());

        // Anos
        final WebElement valorIndicador = elements.get(1);
        final WebElement divContainerValores = valorIndicador.findElement(By.xpath("./child::*"));
        final List<WebElement> listaValorPorAno = divContainerValores.findElements(By.xpath("./child::*"));

        List<IndicadorResultado> salvar = new ArrayList<>();
        for (int i = 1; i < listaValorPorAno.size(); i++) {
            final Indicador indicador = pesquisaIndicadorDaListaUsando(listaNomeIndicadoresLidos.get(i));

            String s = (listaValorPorAno.get(i).getText());
            final List<String> valores = Arrays.asList(s.split("\n"));

            String w = listaValorPorAno.get(0).getText();
            final List<String> anos = Arrays.asList(w.split("\n"));

            for (int y = 1; y < valores.size(); y++) {
                salvar.add(
                        IndicadorResultado
                                .builder()
                                .valor(limparValor(valores.get(y)))
                                .indicador(indicador)
                                .ano(Integer.valueOf(anos.get(y)))
                                .ativo(ativo)
                                .build());
            }
        }
        final String tipoIndicadorDoLoop = limparNome(webElement.getText());
        log.info("Leu todos os indicadores do tipo "+tipoIndicadorDoLoop+" do ativo "+ativo.getCodigo());
        indicadorResultadoRepository.saveAll(salvar);
        return true;
    }

    private String limparNome(String text) {
        return text.split("\\n")[0].trim();
    }

    private void clicaNoBotaoParaListarHistorico(WebDriver driver) {
        final WebElement element = driver.findElement(By.className("btn-indicator-view-type"));
        final List<WebElement> button = element.findElements(By.tagName("button"));
        button.get(1).click();
    }

    private List<WebElement> buscarDadosHistoricoIndicadores(WebDriver driver) {
        final WebElement container = driver.findElement(By.className("indicator-historical-container"));
        return container.findElements(By.className("indicators"));
    }

    private double limparValor(String s) {
        s = s.replace("-%", "0");
        s = s.replace("%", "0");
        s = s.replaceAll("-$","0");
        s = s.replace(".", "");
        s = s.replace(",", ".");
        return Double.parseDouble(s);
    }

    private Indicador pesquisaIndicadorDaListaUsando(String nome) {
        return indicadores
                .stream()
                .filter(i -> nome.equals(i.getNome()))
                .findFirst()
                .get();
    }
}
