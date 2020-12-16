package br.com.lunacom.leitordeindices.service;

import br.com.lunacom.leitordeindices.converter.TabelaTrSiteAdvfnToCotacaoAtivoDtoConverter;
import br.com.lunacom.leitordeindices.converter.TabelaTrSiteInvestingComToCotacaoAtivoDtoConverter;
import br.com.lunacom.leitordeindices.domain.Ativo;
import br.com.lunacom.leitordeindices.domain.dto.CotacaoAtivoDto;
import javassist.tools.rmi.ObjectNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import static br.com.lunacom.leitordeindices.util.Mensagens.ATIVO_NAO_EXISTE;
import static br.com.lunacom.leitordeindices.util.Mensagens.ELEMENTO_ESTRUTURA_NAO_ENCONTRADO;

@Slf4j
@Service
public class ScrapingSiteAdvfnService extends ScrapingAbstract implements Scraping {

    @Autowired
    TabelaTrSiteAdvfnToCotacaoAtivoDtoConverter converter;

    private final String URL_BASE = "https://br.advfn.com/bolsa-de-valores/busca";

    @Override
    public void executar(String codigoAtivo, Date dataReferencia) throws ObjectNotFoundException {

    }

    @Override
    public void executar(List<String> listaAtivos, Date dataReferencia, Boolean invisivel) throws ObjectNotFoundException {
       loop(listaAtivos, dataReferencia, invisivel);
    }

    @Override
    public String getOrigem() {
        return "advfn";
    }

    @Override
    protected String scrapingAtivo(String codigoAtivo, Date dataInicioPesquisa, WebDriver driver, WebDriverWait wait) {
        try {
            final Ativo ativo = ativoService.searchAtivoByCodigo(codigoAtivo);

            //Pesquisar
            driver.findElement(By.id("headerQuickQuoteSearch")).sendKeys(ativo.getCodigo());
            driver.findElement(By.id("header-search-btn")).click();

            final WebElement element = By.id("quote-menu").findElement(driver);
            final WebElement ul = element.findElement(By.tagName("ul"));
            final List<WebElement> li = ul.findElements(By.tagName("li"));
            final WebElement historico = li.stream()
                    .filter(l -> l.getText().equals("Histórico"))
                    .findFirst()
                    .orElseThrow(() -> new ObjectNotFoundException(ELEMENTO_ESTRUTURA_NAO_ENCONTRADO));
            historico.click();

            //Buscar Resultados
            final List<WebElement> tableElement = driver.findElements(By.className("TableElement"));
            final WebElement div = tableElement.stream().filter(t -> t.getText().contains("Data Fechamento Variação")).findFirst().orElseThrow(() -> new ObjectNotFoundException(ELEMENTO_ESTRUTURA_NAO_ENCONTRADO));
            final WebElement table = div.findElement(By.tagName("table"));
            final List<WebElement> trs = table.findElements(By.tagName("tr"));
            trs.remove(0);
            final List<CotacaoAtivoDto> cotacaoAtivoDtoList = converter.encode(trs);

            salvarCotacoesPorAtivo(ativo, dataInicioPesquisa, cotacaoAtivoDtoList);



        } catch (ObjectNotFoundException e) {
            log.error(String.format(ATIVO_NAO_EXISTE, codigoAtivo));
            e.printStackTrace();
        }
        return codigoAtivo;
    }

    @Override
    protected String getUrlBase() {
        return URL_BASE;
    }
}
