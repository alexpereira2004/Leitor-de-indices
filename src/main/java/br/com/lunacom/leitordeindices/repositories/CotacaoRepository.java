package br.com.lunacom.leitordeindices.repositories;

import br.com.lunacom.leitordeindices.domain.Cotacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CotacaoRepository extends JpaRepository<Cotacao, Integer> {
}
