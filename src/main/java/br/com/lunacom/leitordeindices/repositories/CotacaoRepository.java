package br.com.lunacom.leitordeindices.repositories;

import br.com.lunacom.leitordeindices.domain.Ativo;
import br.com.lunacom.leitordeindices.domain.Cotacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface CotacaoRepository extends JpaRepository<Cotacao, Integer> {

    List<Cotacao> findAllByAtivoAndReferenciaAfter(Ativo a, Date d);
}
