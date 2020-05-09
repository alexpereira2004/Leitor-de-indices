package br.com.lunacom.leitordeindices.resources;

import br.com.lunacom.leitordeindices.service.ScrapingHistoricoAtivosService;
import br.com.lunacom.leitordeindices.service.ScrapingIbovespaService;
import br.com.lunacom.leitordeindices.util.DataUtil;
import javassist.tools.rmi.ObjectNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.util.Date;

@RestController
@RequestMapping(value="/scraping")
public class ScrapingResource {

    @Autowired
    ScrapingIbovespaService scrapingService;

    @Autowired
    ScrapingHistoricoAtivosService scrapingHistoricoAtivosService;

    @RequestMapping(value = "ibovespa/carregar-cotacoes-diarias/", method = RequestMethod.GET)
    public ResponseEntity<Void> pesquisar() throws ObjectNotFoundException {
        scrapingService.executar("", new Date());
        return ResponseEntity.ok().build();
    }


    @RequestMapping(value = "historico/ativos/", method = RequestMethod.GET)
    public ResponseEntity<Void> pesquisarHistoricoAtivo(
            @RequestParam("ativos") String ativos,
            @RequestParam("inicio") String inicio
    ) throws ParseException {
        final Date date = DataUtil.parseDayMonthYearSlash(inicio);
        scrapingHistoricoAtivosService.executar(ativos, date);
        return ResponseEntity.ok().build();
    }
}
