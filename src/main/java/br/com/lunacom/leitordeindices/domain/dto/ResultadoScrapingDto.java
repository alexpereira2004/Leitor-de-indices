package br.com.lunacom.leitordeindices.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

@Data
@Component
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ResultadoScrapingDto {
    private String ativo;
    private String resultado;
    private String dia;
}
