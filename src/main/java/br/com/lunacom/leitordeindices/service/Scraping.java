package br.com.lunacom.leitordeindices.service;

import javassist.tools.rmi.ObjectNotFoundException;
import org.openqa.selenium.ElementClickInterceptedException;
import org.openqa.selenium.ElementNotInteractableException;
import org.springframework.retry.annotation.Retryable;

import java.util.Date;
import java.util.List;

public interface Scraping {

    void executar(String codigoAtivo, Date dataReferencia) throws ObjectNotFoundException;

    @Retryable(
            value = { ElementNotInteractableException.class, ElementClickInterceptedException.class },
            maxAttempts = 10
    )
    void executar(List<String> listaAtivos, Date dataReferencia, Boolean invisivel) throws ObjectNotFoundException;

    String getOrigem();
}
