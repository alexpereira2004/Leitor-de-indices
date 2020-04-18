package br.com.lunacom.leitordeindices.resources;

import br.com.lunacom.leitordeindices.service.ScrapingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(value="/scraping")
public class ScrapingResource {

    @Autowired
    ScrapingService scrapingService;

    @RequestMapping(value = "/pesquisar/", method = RequestMethod.GET)
    public ResponseEntity<Void> pesquisar() {
        scrapingService.executar();
        return ResponseEntity.ok().build();
    }
}
