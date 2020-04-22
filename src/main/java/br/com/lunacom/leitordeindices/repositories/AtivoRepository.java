package br.com.lunacom.leitordeindices.repositories;

import br.com.lunacom.leitordeindices.domain.Ativo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AtivoRepository extends JpaRepository<Ativo, Integer> {

    public Optional<Ativo> findByNome(String nome);
//    public Optional<Ativo> findByNomeIgnoreCaseContainsAndTipoContains(String nome, String tipo);
//    public Optional<Ativo> findByNomeIgnoreCaseStartingWithAndTipoContains(String nome, String tipo);
    public Optional<Ativo> findFirstByNomeIgnoreCaseAndTipoContains(String nome, String tipo);

}
