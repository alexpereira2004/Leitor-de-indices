package br.com.lunacom.leitordeindices.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;

@Entity
@Getter
@Setter
public class Movimentacao {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Integer id;

    private String indicacao;
    private String estrategia;

    private Double preco_pago;
    private Double preco_venda;
    private Double rendimento;
    private int quantidade;
    private Double total_investido;
    private Double total_final;
    private Double diferenca;
    private Date aquisicao;
    private Date venda;
    private Integer dias;

    @ManyToOne
    @JoinColumn(name="ativo_id")
    private Ativo ativo;

}
