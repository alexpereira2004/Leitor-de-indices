package br.com.lunacom.leitordeindices.domain;

import br.com.lunacom.leitordeindices.domain.dto.CotacaoAtivoDto;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.modelmapper.ModelMapper;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Getter
@Setter
@EqualsAndHashCode
public class Cotacao implements Serializable, Comparable<Cotacao> {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @EqualsAndHashCode.Exclude
    private Integer id;

    private Double preco;
    private Double abertura;
    private Double variacao;
    private Double maxima;
    private Double minima;

    @EqualsAndHashCode.Exclude
    private Date importacao;
    private Date referencia;
    private String volume;

    @EqualsAndHashCode.Exclude
    private String origem;

    @ManyToOne
    @JoinColumn(name="ativo_id")
    @EqualsAndHashCode.Exclude
    private Ativo ativo;

    public CotacaoAtivoDto toCotacaoAtivoDto() {
        ModelMapper mapper = new ModelMapper();
        return mapper.map(this, CotacaoAtivoDto.class);
    }

    @Override
    public int compareTo(Cotacao o) {
        if (this.getReferencia() == null || o.getReferencia() == null)
            return 0;
        return getReferencia().compareTo(o.getReferencia());
    }
}
