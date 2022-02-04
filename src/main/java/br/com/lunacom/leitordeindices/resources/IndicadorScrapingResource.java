package br.com.lunacom.leitordeindices.resources;

import br.com.lunacom.leitordeindices.service.ScrapingIndicador;
import javassist.tools.rmi.ObjectNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.text.ParseException;
import java.util.List;

@Slf4j
@RestController
@RequestMapping(value="/indicador-scraping")
public class IndicadorScrapingResource {

    @Autowired
    ScrapingIndicador service;

    @RequestMapping(method= RequestMethod.GET)
    public ResponseEntity<Void> pesquisarIndicadores (
            @RequestParam("ativos") List<String> listaAtivos,
            @RequestParam("invisivel") Boolean invisivel
    ) throws ParseException, ObjectNotFoundException {
        service.executar(listaAtivos, invisivel);
        log.info("Pesquisa de indicador");
        return ResponseEntity.ok().build();
    }
}
