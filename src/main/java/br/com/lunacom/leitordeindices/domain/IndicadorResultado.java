package br.com.lunacom.leitordeindices.domain;

import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Data
@Builder
public class IndicadorResultado implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Integer id;

    private double valor;
    @NonNull
    private Integer ano;
    private Integer semestre;

    @ManyToOne
    @JoinColumn(name="indicador_id")
    private Indicador indicador;

    @ManyToOne
    @JoinColumn(name="ativo_id")
    @NonNull
    private Ativo ativo;

}
