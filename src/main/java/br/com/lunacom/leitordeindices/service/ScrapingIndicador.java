package br.com.lunacom.leitordeindices.service;

import javassist.tools.rmi.ObjectNotFoundException;

import java.util.List;

public interface ScrapingIndicador {
    void executar(List<String> listaAtivos, Boolean visivel) throws ObjectNotFoundException;
    void executar(List<String> listaAtivos, Integer anoReferencia, Boolean visivel) throws ObjectNotFoundException;
}
