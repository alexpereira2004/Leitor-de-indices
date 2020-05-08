package br.com.lunacom.leitordeindices.service;

import br.com.lunacom.leitordeindices.domain.Cotacao;
import br.com.lunacom.leitordeindices.repositories.CotacaoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class CotacaoService {

    @Autowired
    private CotacaoRepository repo;

    public Cotacao insert(Cotacao c) {
        c.setImportacao(new Date());
        return repo.save(c);
    }

    public void insertAll(List<Cotacao> cotacoes) {
        repo.saveAll(cotacoes);
    }
}