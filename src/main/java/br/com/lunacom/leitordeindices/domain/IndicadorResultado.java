package br.com.lunacom.leitordeindices.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IndicadorResultado implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Integer id;

    private double valor;
    private Integer ano;
    private Integer semestre;

    @ManyToOne
    @JoinColumn(name="indicador_id")
    private Indicador indicador;

    @ManyToOne
    @JoinColumn(name="ativo_id")
    private Ativo ativo;

}
