package br.com.lunacom.leitordeindices.service;

import javassist.tools.rmi.ObjectNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Slf4j
@Service
public class ScrapingSiteAdvfnService extends ScrapingAbstract implements Scraping {

    private final String URL_BASE = "https://br.advfn.com/bolsa-de-valores/bovespa";

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

        return codigoAtivo;
    }

    @Override
    protected String getUrlBase() {
        return URL_BASE;
    }
}
