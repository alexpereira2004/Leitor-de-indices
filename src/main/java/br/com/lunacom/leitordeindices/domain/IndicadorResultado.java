package br.com.lunacom.leitordeindices.domain;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Data
public class IndicadorResultado implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Integer id;

    private double valor;
    private String tipo;

    @ManyToOne
    @JoinColumn(name="indicador_ano_id")
    private IndicadorAno indicadorAno;

    @ManyToOne
    @JoinColumn(name="indicador_id")
    private Indicador indicador;
}
