package br.com.lunacom.leitordeindices.domain;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Data
public class IndicadorAno implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Integer id;

    private Integer ano;
    private Integer semestre;

    @ManyToOne
    @JoinColumn(name="ativo_id")
    private Ativo ativo;

}
