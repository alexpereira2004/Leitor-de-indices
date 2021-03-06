package br.com.lunacom.leitordeindices.service;

import br.com.lunacom.leitordeindices.domain.Ativo;
import br.com.lunacom.leitordeindices.domain.Cotacao;
import br.com.lunacom.leitordeindices.domain.dto.CotacaoAtivoDto;
import br.com.lunacom.leitordeindices.repositories.CotacaoRepository;
import javassist.tools.rmi.ObjectNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class CotacaoService {

    @Autowired
    private CotacaoRepository repo;

    @Autowired
    private AtivoService ativoService;

    public Cotacao insert(Cotacao c) {
        c.setImportacao(new Date());
        return repo.save(c);
    }

    public void insertAll(List<Cotacao> cotacoes) {
        repo.saveAll(cotacoes);
    }

    public List<Cotacao> findAllByAtivoAndReferenciaGreaterThanEqual (Ativo a, Date d) {
        return repo.findAllByAtivoAndReferenciaGreaterThanEqual(a, d);
    }
    public List<Cotacao> findAllByAtivoAndReferenciaAfter(Ativo a, Date d) {

        return repo.findAllByAtivoAndReferenciaAfter(a, d);
    }

    public List<Cotacao> findAllByAtivoAndReferencia(Ativo a, Date d) {
        return repo.findAllByAtivoAndReferencia(a, d);
    }

    public List<CotacaoAtivoDto> find(String codigoAtivo, Date datainicial, Date datafinal) throws ObjectNotFoundException {
        final Ativo ativo = ativoService.searchAtivoByCodigo(codigoAtivo);
        final List<Cotacao> cotacoes = repo.findAllByAtivoAndReferenciaBetween(ativo, datainicial, datafinal);
        final List<CotacaoAtivoDto> cotacaoAtivoDtoList = cotacoes.stream().map(Cotacao::toCotacaoAtivoDto).collect(Collectors.toList());
        Collections.sort(cotacaoAtivoDtoList, Collections.reverseOrder());
        return cotacaoAtivoDtoList;
    }

    public Cotacao buscarCotacaoMaisRecente(Ativo ativo) {
        Optional<Cotacao> optionalCotacao = repo.findTopByAtivoOrderByReferenciaDesc(ativo);
        return optionalCotacao
                .orElseThrow(
                        () -> new NoSuchElementException("Cota????o n??o existe para o "+ ativo.getCodigo()));
    }
}
