package br.com.lunacom.leitordeindices.service;

import br.com.lunacom.leitordeindices.domain.Ativo;
import br.com.lunacom.leitordeindices.domain.Indicador;
import br.com.lunacom.leitordeindices.domain.IndicadorAno;
import br.com.lunacom.leitordeindices.domain.IndicadorResultado;
import br.com.lunacom.leitordeindices.repositories.IndicadorRepository;
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

    @Autowired
    IndicadorRepository indicadorRepository;

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
//        WebDriverWait wait = new WebDriverWait(driver, 10);
//        driver.get(URL_BASE);
        try {
            indicadores = indicadorRepository.findAll();
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

            clicaNoBotaoParaListarHistorico(driver);

            final List<WebElement> indicators = buscarDadosHistoricoIndicadores(driver);


// loop para cada tipo de indicador

            final WebElement webElement = indicators.get(0);

            final WebElement div = webElement.findElement(xpathFilhoDoNodo);
            final List<WebElement> elements = div.findElements(xpathFilhoDoNodo);

            // Indicadores
            final WebElement nomeIndicadorLido = elements.get(0);
            final List<WebElement> nomeIndicadorLidoElements = nomeIndicadorLido.findElements(xpathFilhoDoNodo);
            final List<String> listaNomeIndicadoresLidos = nomeIndicadorLidoElements.stream().map(e -> limparNome(e.getText())).collect(Collectors.toList());
            int quantidadeLinhas = nomeIndicadorLidoElements.size();

            // Anos
            final WebElement valorIndicador = elements.get(1);
            final WebElement divContainerValores = valorIndicador.findElement(By.xpath("./child::*"));
            final List<WebElement> listaValorPorAno = divContainerValores.findElements(By.xpath("./child::*"));
            final String strAnos = listaValorPorAno.get(0).getText();
            final List<String> listaAnos = Arrays.asList(strAnos.split("\n"));
            int quantidadeColunas = listaAnos.size() - 1;

            final String text = webElement.getText();
            final String all = text.replaceAll("\nhelp_outline", "").replaceAll("\nshow_chart", "").replaceAll("\nformat_quote", "");
            Arrays.asList(all.split("\n"));
            final LinkedList<String> resultados = new LinkedList<>(Arrays.asList(all.split("\n")));


            log.info("Até aqui");


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

            log.info("Dois !!");

//            final WebElement div = webElement.findElement(xpathFilhoDoNodo);
//            final List<WebElement> elements = div.findElements(xpathFilhoDoNodo);
//
//            //Buscar nome dos indicadores (Por tipo : VALUATION, ENDIVIDAMENTO, EFICIÊNCIA, RENTABILIDADE, CRESCIMENTO)
//            final WebElement nomeIndicadorLido = elements.get(0);
//            final List<WebElement> nomeIndicadorLidoElements = nomeIndicadorLido.findElements(xpathFilhoDoNodo);
//            final List<String> listaNomeIndicadoresLidos = nomeIndicadorLidoElements.stream().map(e -> limparNome(e.getText())).collect(Collectors.toList());
//
//            // Buscar a matriz de resultados x ano
//            final WebElement valorIndicador = elements.get(1);
//            final WebElement divContainerValores = valorIndicador.findElement(By.xpath("./child::*"));
//            final List<WebElement> listaValorPorAno = divContainerValores.findElements(By.xpath("./child::*"));
//            final Map<String, IndicadorAno> indicadorAnoMap = criarListaDeAnosDosDados(listaValorPorAno.get(0).getText(), ativo);
//
//            // Salvar
//            salvarListaDeAnos(indicadorAnoMap);
//
//            int index = 0;
//            listaNomeIndicadoresLidos.stream()
//                    .skip(1)
//                    .map(obj -> percorrerResultados(listaNomeIndicadoresLidos.indexOf(obj), obj, indicadorAnoMap, listaValorPorAno))
//                    .collect(Collectors.toList());

//            indicadores
//                    .stream()
//                    .filter(i -> "P/VP".equals(i.getNome()))
//                    .findAny();




        } catch (ObjectNotFoundException e) {
            log.error(String.format(ATIVO_NAO_EXISTE, codigoAtivo));
            e.printStackTrace();
        }

    }

    private double limparValor(String s) {
        s = s.replace("-%", "0");
        s = s.replace("%", "");
        s = s.replace(",", ".");
        return Double.parseDouble(s);
    }

    private boolean percorrerResultados(int index, String nomeIndicador,
                                        Map<String, IndicadorAno> anosMap, List<WebElement> resultadosWebElement) {
        final String linha = resultadosWebElement.get(index).getText();
        final List<String> resultados = Arrays.asList(linha.split("\n"));

        final Indicador indicador = pesquisaIndicadorDaListaUsando(nomeIndicador);

        anosMap.values()
                .stream()
                .forEach( ano -> {
//                    final String s = resultados.get(0);
//                    resultados.remove(0);
                    double resultado = 0;
//                    if (!s.equals("-")) {
//                        resultado = Double.parseDouble(s);
//                    }
                    salvarResultado(resultado, ano, indicador);
                });

        return true;
    }

    private void salvarResultado(double resultadoValor, IndicadorAno indicadorAno, Indicador indicador) {
        final IndicadorResultado resultado = IndicadorResultado.builder()
                .valor(resultadoValor)
                .indicador(indicador)
                .build();
    }

    private void salvarListaDeAnos(Map<String, IndicadorAno> stringIndicadorAnoMap) {
//        indicadorAnoRepository.saveAll(stringIndicadorAnoMap.values());
    }

    private Map<String, IndicadorAno> criarListaDeAnosDosDados(String strAnos, Ativo ativo) {
        Map<String,IndicadorAno> listaAnosHashMap = new LinkedHashMap<String,IndicadorAno>();
        final List<String> listaAnos = Arrays.asList(strAnos.split("\n"));
        listaAnos
                .stream()
                .skip(1)
                .map(str -> listaAnosHashMap.put(str, new IndicadorAno(Integer.valueOf(str), ativo)))
                .collect(Collectors.toList());

        return listaAnosHashMap;
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

    private Indicador pesquisaIndicadorDaListaUsando(String nome) {
        return indicadores
                .stream()
                .filter(i -> nome.equals(i.getNome()))
                .findFirst()
                .get();
    }
}
