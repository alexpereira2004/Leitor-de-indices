package br.com.lunacom.leitordeindices.domain.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class AtivoResumoDto implements Serializable, Dto, Comparable<AtivoResumoDto> {
    private static final long serialVersionUID = 1L;

    private String nome;
    private String nome_completo;
    private String codigo;
    private String tipo;

    @Override
    public int compareTo(AtivoResumoDto o) {
        if (this.getCodigo() == null || o.getCodigo() == null)
            return 0;
        return getCodigo().compareTo(o.getCodigo());
    }
}
