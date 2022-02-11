package br.com.lunacom.leitordeindices.repositories;

import br.com.lunacom.leitordeindices.domain.Ativo;
import br.com.lunacom.leitordeindices.domain.Cotacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface CotacaoRepository extends JpaRepository<Cotacao, Integer> {

    List<Cotacao> findAllByAtivoAndReferenciaGreaterThanEqual(Ativo a, Date d);
    List<Cotacao> findAllByAtivoAndReferenciaGreaterThan(Ativo a, Date d);
    List<Cotacao> findAllByAtivoAndReferenciaAfter(Ativo a, Date d);
    List<Cotacao> findAllByAtivoAndReferencia(Ativo a, Date d);
    List<Cotacao> findAllByAtivoAndReferenciaBetween(Ativo a, Date inicio, Date fim);

    Optional<Cotacao> findTopByAtivoOrderByReferenciaDesc(Ativo integer);
}
