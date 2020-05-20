package br.com.lunacom.leitordeindices.service;

import javassist.tools.rmi.ObjectNotFoundException;

import java.util.Date;

public interface Scraping {
    void executar(String codigoAtivo, Date dataReferencia) throws ObjectNotFoundException;
    String getOrigem();
}
