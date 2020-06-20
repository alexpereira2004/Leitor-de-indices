package br.com.lunacom.leitordeindices.domain.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class CotacaoAtivoDto implements Serializable, Dto, Comparable<CotacaoAtivoDto> {
    private static final long serialVersionUID = 1L;

    private Double preco;
    private Double abertura;
    private Double variacao;
    private Double maxima;
    private Double minima;
    private Date importacao;
    private Date referencia;
    private String volume;
    private String origem;

    @Override
    public int compareTo(CotacaoAtivoDto o) {
        if (this.getReferencia() == null || o.getReferencia() == null)
            return 0;
        return getReferencia().compareTo(o.getReferencia());
    }
}
