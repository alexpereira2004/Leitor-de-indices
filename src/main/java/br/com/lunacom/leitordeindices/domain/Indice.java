package br.com.lunacom.leitordeindices.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Set;

@Entity
@Getter
@Setter
public class Indice implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Integer id;

    private String nome;
    private String codigo;
    private String tipo;
    private String pais;
    private String caminho;

    @ManyToMany(mappedBy = "indices", fetch = FetchType.LAZY)
    Set<Ativo> ativos;
}
