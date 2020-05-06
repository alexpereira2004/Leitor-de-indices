package br.com.lunacom.leitordeindices.service;

import br.com.lunacom.leitordeindices.domain.Ativo;
import br.com.lunacom.leitordeindices.repositories.AtivoRepository;
import javassist.tools.rmi.ObjectNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.xml.bind.ValidationException;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class AtivoService {

    @Autowired
    private AtivoRepository repo;

    public Ativo searchAtivo(String nome) throws ObjectNotFoundException {
        Optional<Ativo> obj = repo.findByNome(nome);
        Ativo ativo = obj.orElseThrow(
                () -> new ObjectNotFoundException(
                        String.format("Não foi encontrado um ativo com o nome informado %s",nome)));
        return ativo;
    }

    public Ativo searchAtivoByCodigo(String codigo) throws ObjectNotFoundException {
        Optional<Ativo> obj = repo.findByCodigo(codigo);
        Ativo ativo = obj.orElseThrow(
                () -> new ObjectNotFoundException(
                        String.format("Não foi encontrado um ativo com o cóodigo informado %s",codigo)));
        return ativo;
    }
    public Ativo searchAtivo(String nome, String tipo) throws ObjectNotFoundException {

        Optional<Ativo> obj = repo.findFirstByNomeIgnoreCaseAndTipoContains(nome, tipo);

        Ativo ativo = obj.orElseThrow(
                () -> new ObjectNotFoundException(
                        String.format("Não foi encontrado um ativo com o nome (%s) e tipo (%s) informado",nome, tipo)));
        return ativo;
    }

    public Ativo getNomeETipo(String nomeEmpresa) throws ValidationException {
        String pattern = "(.*)\\s(PNA|ON|PN|Unit)";
        Pattern r = Pattern.compile(pattern);
        Matcher m = r.matcher(nomeEmpresa);
        if (!m.find()) {
            throw new ValidationException("Ativo não encontrado na linha de dados informada");
        }
        Ativo ativo = new Ativo();
        ativo.setNome(m.group(1));
        ativo.setTipo(m.group(2).replace("Unit", "UNT"));
        return ativo;
    }
}
