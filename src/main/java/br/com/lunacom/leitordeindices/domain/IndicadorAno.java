package br.com.lunacom.leitordeindices.domain;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;

@Data
@RequiredArgsConstructor
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
