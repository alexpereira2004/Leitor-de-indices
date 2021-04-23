package br.com.lunacom.leitordeindices.domain;

import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Data
@RequiredArgsConstructor
public class IndicadorAno implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Integer id;

    @NonNull
    private Integer ano;
    private Integer semestre;

    @ManyToOne
    @JoinColumn(name="ativo_id")
    @NonNull
    private Ativo ativo;

}
