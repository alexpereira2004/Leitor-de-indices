package br.com.lunacom.leitordeindices.service;

import javassist.tools.rmi.ObjectNotFoundException;

import java.util.Date;

public interface Scraping {
    void executar(String codigoAtivo, Date dataInicioPesquisa) throws ObjectNotFoundException;
    String getOrigem();
}
