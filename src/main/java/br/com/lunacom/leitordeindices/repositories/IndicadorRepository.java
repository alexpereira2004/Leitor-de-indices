package br.com.lunacom.leitordeindices.repositories;

import br.com.lunacom.leitordeindices.domain.Indicador;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IndicadorRepository extends JpaRepository<Indicador, Integer> {
}
