package br.com.lunacom.leitordeindices.service;

import javassist.tools.rmi.ObjectNotFoundException;

import java.util.Date;

public class ScrapingCotacaoAtual implements Scraping {
    @Override
    public void executar(String codigoAtivo, Date dataInicioPesquisa) throws ObjectNotFoundException {

    }

    @Override
    public String getOrigem() {
        return "atual";
    }
}
