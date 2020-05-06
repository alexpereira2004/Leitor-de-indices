package br.com.lunacom.leitordeindices.resources;

import br.com.lunacom.leitordeindices.service.ScrapingIbovespaService;
import javassist.tools.rmi.ObjectNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value="/scraping")
public class ScrapingResource {

    @Autowired
    ScrapingIbovespaService scrapingService;

    @RequestMapping(value = "ibovespa/carregar-indices-diarios/", method = RequestMethod.GET)
    public ResponseEntity<Void> pesquisar() {
        scrapingService.executar();
        return ResponseEntity.ok().build();
    }


    @RequestMapping(value = "historico/ativos/{ativo}/", method = RequestMethod.GET)
    public ResponseEntity<Void> pesquisarHistoricoAtivo(@PathVariable String ativo) throws ObjectNotFoundException {
        scrapingService.pesquisarHistoricoAtivo(ativo);
        return ResponseEntity.ok().build();
    }
}
