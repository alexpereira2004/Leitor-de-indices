package br.com.lunacom.leitordeindices.service;

import br.com.lunacom.leitordeindices.domain.Ativo;
import br.com.lunacom.leitordeindices.repositories.AtivoRepository;
import javassist.tools.rmi.ObjectNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AtivoService {

    @Autowired
    private AtivoRepository repo;

    public Ativo findByNome(String nome) throws ObjectNotFoundException {
        Optional<Ativo> obj = repo.findByNome(nome);

        Ativo ativo = obj.orElseThrow(
                () -> new ObjectNotFoundException(
                        String.format("Não foi encontrado um ativo com o nome informado %s",nome)));
        return ativo;
    }
}
