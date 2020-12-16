package br.com.lunacom.leitordeindices.service;

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

@Slf4j
public class ScrapingAbstract {
    @Value("${webdriver.gecko.driver}")
    private String webdriverGeckoDriver;

    @Autowired
    protected AtivoService ativoService;

    @Autowired
    protected CotacaoService cotacaoService;

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

    protected Optional<Cotacao> getCotacaoSeExistir(Cotacao cotacao, List<Cotacao> cotacoesExistentes) {
        return cotacoesExistentes
                .stream()
                .filter(c -> c.getReferencia().equals(cotacao.getReferencia()))
                .findFirst();
    }
}
