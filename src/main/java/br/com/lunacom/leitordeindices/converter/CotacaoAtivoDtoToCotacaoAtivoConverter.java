package br.com.lunacom.leitordeindices.converter;

import br.com.lunacom.leitordeindices.domain.Cotacao;
import br.com.lunacom.leitordeindices.domain.dto.CotacaoAtivoDto;
import org.springframework.stereotype.Component;

@Component
public class CotacaoAtivoDtoToCotacaoAtivoConverter implements Converter<CotacaoAtivoDto, Cotacao> {

    @Override
    public Cotacao encode(CotacaoAtivoDto input) {
        Cotacao cotacao = new Cotacao();
        cotacao.setPreco(input.getPreco());
        cotacao.setAbertura(input.getAbertura());
        cotacao.setVariacao(input.getVariacao());
        cotacao.setMaxima(input.getMaxima());
        cotacao.setMinima(input.getMinima());
        cotacao.setImportacao(input.getImportacao());
        cotacao.setReferencia(input.getReferencia());
        cotacao.setVolume(input.getVolume());
        cotacao.setOrigem(input.getOrigem());
        return cotacao;
    }

    @Override
    public CotacaoAtivoDto decode(Cotacao input) {
        return null;
    }
}
