package br.com.lunacom.leitordeindices.service;

import br.com.lunacom.leitordeindices.converter.CotacaoAtivoDtoToCotacaoAtivoConverter;
import br.com.lunacom.leitordeindices.domain.Ativo;
import br.com.lunacom.leitordeindices.domain.Cotacao;
import br.com.lunacom.leitordeindices.domain.dto.CotacaoAtivoDto;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.text.NumberFormat;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
public class ScrapingAbstract {
//    @Value("${webdriver.gecko.driver}")
//    private String webdriverGeckoDriver;

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

    protected Cotacao setCotacaoIdSeExistir(Cotacao cotacao, List<Cotacao> cotacoesExistentes) {
        final Optional<Cotacao> first = cotacoesExistentes
                .stream()
                .filter(c -> cotacao.getReferencia().equals(c.getReferencia()))
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
