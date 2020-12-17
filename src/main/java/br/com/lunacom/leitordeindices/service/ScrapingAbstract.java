package br.com.lunacom.leitordeindices.service;

import br.com.lunacom.leitordeindices.converter.CotacaoAtivoDtoToCotacaoAtivoConverter;
import br.com.lunacom.leitordeindices.domain.Ativo;
import br.com.lunacom.leitordeindices.domain.Cotacao;
import br.com.lunacom.leitordeindices.domain.dto.CotacaoAtivoDto;
import br.com.lunacom.leitordeindices.util.DataUtil;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.sql.Timestamp;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
public class ScrapingAbstract {
    @Value("${webdriver.gecko.driver}")
    private String webdriverGeckoDriver;

    @Autowired
    protected AtivoService ativoService;

    @Autowired
    protected CotacaoService cotacaoService;

    @Autowired
    CotacaoAtivoDtoToCotacaoAtivoConverter converter;

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

    protected void salvarCotacoesPorAtivo(Ativo ativo, Date dataInicioPesquisa, List<CotacaoAtivoDto> dtoList) {
        final List<Cotacao> cotacoes = converter.encode(dtoList);
        Collections.sort(cotacoes, Collections.reverseOrder());

        final List<Cotacao> cotacoesExistentes = cotacaoService.findAllByAtivoAndReferenciaGreaterThanEqual(ativo, dataInicioPesquisa);
        Collections.sort(cotacoesExistentes, Collections.reverseOrder());

        cotacoes.stream()
                .map(c -> setCotacaoIdSeExistir(c, cotacoesExistentes))
                .map(c -> setAtivoToList(ativo, c))
                .collect(Collectors.toList());

        log.info(String.format("Total de cotações encontradas para %s: %s",ativo.getCodigo(), cotacoes.size()));
        cotacaoService.insertAll(cotacoes);
    }

    protected void salvarCotacoesPorAtivo(Ativo ativo, Date dataInicioPesquisa, CotacaoAtivoDto dto) {
        NumberFormat nf = NumberFormat.getInstance(Locale.GERMANY);

        List<Cotacao> cotacoes = new ArrayList<>();

        final List<Cotacao> cotacoesExistentes = cotacaoService.findAllByAtivoAndReferenciaGreaterThanEqual(ativo, dataInicioPesquisa);



//        c.setAtivo(ativo);
//        final Date date = DataUtil.parseDayMonthYearDot(tdElements.get(0).getText());
//        Timestamp ts = new Timestamp(date.getTime());
//        c.setReferencia(ts);
//        c.setPreco(nf.parse(tdElements.get(1).getText()).doubleValue());
//        c.setAbertura(nf.parse(tdElements.get(2).getText()).doubleValue());
//        c.setMaxima(nf.parse(tdElements.get(3).getText()).doubleValue());
//        c.setMinima(nf.parse(tdElements.get(4).getText()).doubleValue());
//        c.setVolume(tdElements.get(5).getText());
//        c.setOrigem("historico-ativo");
//        c.setImportacao(new Date());
//        final String variacao = tdElements.get(6).getText().replace("%","");
//        c.setVariacao(nf.parse(variacao).doubleValue());
//        final Optional<Cotacao> cotacaoSeExistir = getCotacaoSeExistir(c, cotacoesExistentes);
//        if (cotacaoSeExistir.isPresent()) {
//            c.setId(cotacaoSeExistir.get().getId());
//        }
//        cotacoes.add(c);



        log.info(String.format("Total de cotações encontradas para %s: %s",ativo.getCodigo(), cotacoes.size()));
        cotacaoService.insertAll(cotacoes);
    }

    protected Cotacao setCotacaoIdSeExistir(Cotacao cotacao, List<Cotacao> cotacoesExistentes) {
        final Optional<Cotacao> first = cotacoesExistentes
                .stream()
                .filter(c -> cotacao.getReferencia().equals(new Date(c.getReferencia().getTime())))
                .findFirst();
        if (first.isPresent()) {
            cotacao.setId(first.get().getId());
        }
        return cotacao;
    }

    protected Cotacao setAtivoToList(Ativo a, Cotacao c) {
        c.setAtivo(a);
        return c;
    }
}
