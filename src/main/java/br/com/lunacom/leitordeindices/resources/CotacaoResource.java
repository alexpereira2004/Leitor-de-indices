package br.com.lunacom.leitordeindices.resources;

import br.com.lunacom.leitordeindices.domain.dto.CotacaoAtivoDto;
import br.com.lunacom.leitordeindices.service.CotacaoService;
import br.com.lunacom.leitordeindices.util.DataUtil;
import javassist.tools.rmi.ObjectNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping(value="/cotacao")
public class CotacaoResource {

    @Autowired
    private CotacaoService cotacaoService;

    @RequestMapping(method= RequestMethod.GET)
    public ResponseEntity<List<CotacaoAtivoDto>> find(
            @RequestParam("ativo") String ativo,
            @RequestParam("datainicial") String datainicial,
            @RequestParam("datafinal") String datafinal
    ) throws ParseException, ObjectNotFoundException {
        final Date dataInicial = DataUtil.parseDayMonthYearSlash(datainicial);
        final Date dataFinal = DataUtil.parseDayMonthYearSlash(datafinal);
        List<CotacaoAtivoDto> obj = cotacaoService.find(ativo, dataInicial, dataFinal);
        return ResponseEntity.ok().body(obj);
    }
}
