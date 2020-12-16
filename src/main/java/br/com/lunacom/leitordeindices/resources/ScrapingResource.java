package br.com.lunacom.leitordeindices.resources;

import br.com.lunacom.leitordeindices.factory.ScrapingHistoricoAtivosFactory;
import br.com.lunacom.leitordeindices.service.Scraping;
import br.com.lunacom.leitordeindices.service.ScrapingCotacaoAtualService;
import br.com.lunacom.leitordeindices.service.ScrapingIbovespaService;
import br.com.lunacom.leitordeindices.util.DataUtil;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import javassist.tools.rmi.ObjectNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.text.ParseException;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping(value="/scraping")
public class ScrapingResource {

    @Autowired
    ScrapingIbovespaService scrapingService;

    @Autowired
    ScrapingCotacaoAtualService scrapingCotacaoAtualService;

    @ApiOperation(value="Busca todas as cotações do dia dos ativos da Ibovespa")
    @RequestMapping(value = "ibovespa/carregar-cotacoes-diarias/", method = RequestMethod.GET)
    public ResponseEntity<Void> pesquisar() throws ObjectNotFoundException {
        scrapingService.executar("", new Date());
        return ResponseEntity.ok().build();
    }

    @ApiOperation(value="Pesquisa pela cotação atual dos ativos passados como parâmetro")
    @RequestMapping(value = "cotacoes-diarias/", method = RequestMethod.GET)
    public ResponseEntity<Void> cotacoesAtuais(@RequestParam("ativos") String ativos)
            throws ObjectNotFoundException {
        scrapingCotacaoAtualService.executar(ativos, new Date());
        return ResponseEntity.ok().build();
    }

    @ApiOperation(value="Pesquisa pelo histórico de cada um dos ativos e salva em banco")
    @ApiResponses(value = {
            @ApiResponse(code = 400, message = "Não é possível realizar a operação"),
            @ApiResponse(code = 404, message = "Código do ativo inexistente") })
    @RequestMapping(value = "historico/ativos/", method = RequestMethod.GET)
    public ResponseEntity<Void> pesquisarHistoricoAtivo(
            @RequestParam("ativos") List<String> listaAtivos,
            @RequestParam("inicio") String inicio,
            @RequestParam("site") String site,
            @RequestParam("invisivel") Boolean invisivel
    ) throws ParseException, ObjectNotFoundException {
        final Date date = DataUtil.parseDayMonthYearSlash(inicio);
        final Scraping scrapingService = ScrapingHistoricoAtivosFactory.create(site);
        scrapingService.executar(listaAtivos, date, invisivel);
        return ResponseEntity.ok().build();
    }
}
