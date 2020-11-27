package br.com.lunacom.leitordeindices.resources;

import br.com.lunacom.leitordeindices.domain.dto.AtivoResumoDto;
import br.com.lunacom.leitordeindices.service.AtivoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@RestController
@RequestMapping(value="/ativo")
public class AtivoResource {

    private final AtivoService service;

    @RequestMapping(method= RequestMethod.GET, value = "/todos-com-cotacao")
    public ResponseEntity<List<AtivoResumoDto>> findAll() {

        final Optional<List<AtivoResumoDto>> ativosComCotacao = service.buscarTodosAtivosComCotacao();
        List<AtivoResumoDto> ativos = new ArrayList<>();
        if (ativosComCotacao.isPresent()) {
            ativos = ativosComCotacao.get();
        }
        return new ResponseEntity(ativos, HttpStatus.OK);
    }
}
