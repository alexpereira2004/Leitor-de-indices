package br.com.lunacom.leitordeindices.service;

import br.com.lunacom.leitordeindices.converter.Converter;
import br.com.lunacom.leitordeindices.converter.TabelaTrSiteInvestingComToCotacaoAtivoDtoConverter;
import br.com.lunacom.leitordeindices.domain.Ativo;
import br.com.lunacom.leitordeindices.domain.Cotacao;
import br.com.lunacom.leitordeindices.domain.dto.CotacaoAtivoDto;
import br.com.lunacom.leitordeindices.util.DataUtil;
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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.persistence.NoResultException;
import java.sql.Timestamp;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ScrapingInvestingComService extends ScrapingInvestingComAbstract implements Scraping {

    @Autowired
    TabelaTrSiteInvestingComToCotacaoAtivoDtoConverter converter;

    private final String origem = "investing";

    @Value("${webdriver.gecko.driver}")
    private String webdriverGeckoDriver;

    @Override
    public void executar(String referenciaCodigoAtivo, Date dataInicioPesquisa)  {

    }

    @Override
    public void executar(List<String> listaAtivos, Date dataReferencia, Boolean invisivel)  {
        loop(listaAtivos, dataReferencia, invisivel);
    }

    @Override
    protected String scrapingAtivo(String codigoAtivo, Date dataInicioPesquisa, WebDriver driver, WebDriverWait wait) {
        try {
            this.fecharAvisoPrivacidade(driver);
            final Ativo ativo = ativoService.searchAtivoByCodigo(codigoAtivo);
            this.acessarHistorico(ativo, driver, wait);
            this.filtrar(dataInicioPesquisa, driver);
            List<WebElement> trElements = bucarResultados(driver, wait);

            final List<CotacaoAtivoDto> cotacaoAtivoDtoList = converter.encode(trElements);
            salvarCotacoesPorAtivo(ativo, dataInicioPesquisa, cotacaoAtivoDtoList);
        } catch (NoResultException e) {
            log.warn(e.getMessage(), codigoAtivo);
        } catch (ObjectNotFoundException e) {
            log.error(String.format("Código do ativo não existe: $s", codigoAtivo));
            e.printStackTrace();
        }
        return codigoAtivo;
    }

    private void acessarHistorico(Ativo ativo, WebDriver driver, WebDriverWait wait) {
        if (Objects.isNull(ativo.getCaminho())) {
            pesquisarCaminhoDoAtivo(ativo, driver, wait);
        }
        driver.get(ativo.getCaminho()+"-historical-data");
    }

    private void filtrar(Date dataInicioPesquisa, WebDriver driver) {
        driver.findElement(By.cssSelector(".historicDate")).click();
        final WebElement startDate = driver.findElement(By.id("startDate"));
        startDate.clear();
        startDate.sendKeys(DataUtil.formatAsDayMonthYearSlash(dataInicioPesquisa));
        driver.findElement(By.id("applyBtn")).click();
    }

    private List<WebElement> bucarResultados(WebDriver driver, WebDriverWait wait) {
        wait.until(ExpectedConditions.presenceOfElementLocated(By.id("curr_table")));
        log.info("Carregou a tabela");
        final WebElement table = driver.findElement(By.id("curr_table"));
        final WebElement tableBody = table.findElement(By.tagName("tbody"));
        log.info("Aguardando corpo da tabela");
        wait.until(ExpectedConditions.presenceOfElementLocated(By.className("first")));
        log.info("Corpo da tabela carregado");
        List<WebElement> trs = tableBody.findElements(By.tagName("tr"));
        if (trs.size() == 0) {
            throw new NoResultException();
        }
        return trs;
    }

    @Override
    public String getOrigem() {
        return origem;
    }

    private boolean compararSeExiste(Cotacao cotacao, List<Cotacao> cotacoesExistentes) {
        final List<Cotacao> collect = cotacoesExistentes
                .stream()
                .filter(c -> c.equals(cotacao))
                .collect(Collectors.toList());
        return collect.isEmpty();
    }
}


