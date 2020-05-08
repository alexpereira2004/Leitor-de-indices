package br.com.lunacom.leitordeindices.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Getter
@Setter
public class Cotacao implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Integer id;

    private Double preco;
    private Double abertura;
    private Double variacao;
    private Double maxima;
    private Double minima;
    private Date importacao;
    private Date referencia;
    private String volume;
    private String origem;

    @ManyToOne
    @JoinColumn(name="ativo_id")
    private Ativo ativo;
}