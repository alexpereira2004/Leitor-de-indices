package br.com.lunacom.leitordeindices.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;
import java.util.Set;

@Entity
@Getter
@Setter
public class Ativo implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Integer id;

    private String nome;
    private String nome_completo;
    private String codigo;
    private String tipo;
    private String pais;
    private String caminho;

    @OneToMany(mappedBy = "ativo")
    private List<Cotacao> cotacoes;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "ativo_indice",
            joinColumns = @JoinColumn(name = "ativo_id"),
            inverseJoinColumns = @JoinColumn(name = "indice_id"))
    Set<Indice> indices;


}
