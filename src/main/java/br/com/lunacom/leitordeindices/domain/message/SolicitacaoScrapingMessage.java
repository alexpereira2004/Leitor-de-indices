package br.com.lunacom.leitordeindices.domain.message;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.springframework.stereotype.Component;

@ToString
@Getter
@Setter
@NoArgsConstructor
@Builder
@Component
public class SolicitacaoScrapingMessage {
    private String ativo;

    public SolicitacaoScrapingMessage(@JsonProperty("ativo") String ativo) {
        this.ativo = ativo;
    }

}
