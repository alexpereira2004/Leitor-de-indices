package br.com.lunacom.leitordeindices.repositories;

import br.com.lunacom.leitordeindices.domain.Ativo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AtivoRepository extends JpaRepository<Ativo, Integer> {

    public Optional<Ativo> findByNome(String nome);
    public Optional<Ativo> findByCodigo(String codigo);
//    public Optional<Ativo> findByNomeIgnoreCaseContainsAndTipoContains(String nome, String tipo);
//    public Optional<Ativo> findByNomeIgnoreCaseStartingWithAndTipoContains(String nome, String tipo);
    public Optional<Ativo> findFirstByNomeIgnoreCaseAndTipoContains(String nome, String tipo);

    public Optional<List<Ativo>> findAllByCotacoesIsNotNull();


    public Optional<List<Ativo>> findDistinctByCotacoesIsNotNull();

//    @Query("SELECT DISTINCT(a.codigo) AS codigo FROM Ativo a WHERE a.cotacoes ")
//    public Optional<List<Ativo>> teste();


}
