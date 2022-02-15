package br.com.lunacom.leitordeindices.enumeration;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.EnumSet;

@AllArgsConstructor
@Getter
public enum TipoIndicador {
    VALUATION(1, "INDICADORES DE VALUATION"),
    ENDIVIDAMENTO(2, "INDICADORES DE ENDIVIDAMENTO"),
    EFICIENCIA(3, "INDICADORES DE EFICIÊNCIA"),
    RENTABILIDADE(4, "INDICADORES DE RENTABILIDADE"),
    CRESCIMENTO(5, "INDICADORES DE CRESCIMENTO");

    Integer codigo;
    String nomeCompleto;

    public static TipoIndicador fromCodigo(Integer value) {
        return EnumSet.allOf(TipoIndicador.class)
                .stream()
                .filter(it -> it.getCodigo().equals(value))
                .findFirst()
                .orElse(VALUATION);
    }
}
