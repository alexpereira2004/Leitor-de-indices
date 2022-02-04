package br.com.lunacom.leitordeindices.domain;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Data
public class Indicador implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Integer id;

    private String nome;
    private String codigo;

    @Column(columnDefinition="text")
    private String descricao;
    private Integer tipo;
    private Integer tipoValor;

}
