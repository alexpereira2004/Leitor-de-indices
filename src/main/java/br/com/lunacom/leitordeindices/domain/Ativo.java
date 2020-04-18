package br.com.lunacom.leitordeindices.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

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

    @OneToMany(mappedBy = "ativo")
    private List<Cotacao> cotacoes;
}
