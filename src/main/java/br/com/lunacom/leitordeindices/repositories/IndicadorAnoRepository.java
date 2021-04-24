package br.com.lunacom.leitordeindices.repositories;

import br.com.lunacom.leitordeindices.domain.IndicadorAno;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IndicadorAnoRepository extends JpaRepository<IndicadorAno, Integer> {
}
