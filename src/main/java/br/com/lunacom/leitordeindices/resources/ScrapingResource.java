package br.com.lunacom.leitordeindices.resources;

import br.com.lunacom.leitordeindices.service.ScrapingCotacaoAtualService;
import br.com.lunacom.leitordeindices.service.ScrapingHistoricoAtivosService;
import br.com.lunacom.leitordeindices.service.ScrapingIbovespaService;
import br.com.lunacom.leitordeindices.util.DataUtil;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
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
    ScrapingCotacaoAtualService scrapingCotacaoAtualService;

    @Autowired
    ScrapingHistoricoAtivosService scrapingHistoricoAtivosService;


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
            @RequestParam("ativos") String ativos,
            @RequestParam("inicio") String inicio
    ) throws ParseException {
        final Date date = DataUtil.parseDayMonthYearSlash(inicio);
        scrapingHistoricoAtivosService.executar(ativos, date);
        return ResponseEntity.ok().build();
    }
}
