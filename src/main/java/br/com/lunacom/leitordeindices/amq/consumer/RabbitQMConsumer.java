package br.com.lunacom.leitordeindices.amq.consumer;

import br.com.lunacom.leitordeindices.amq.producer.ScrapingHistoricoIncidesProducer;
import br.com.lunacom.leitordeindices.domain.Ativo;
import br.com.lunacom.leitordeindices.domain.Cotacao;
import br.com.lunacom.leitordeindices.domain.dto.ResultadoScrapingDto;
import br.com.lunacom.leitordeindices.domain.message.SolicitacaoScrapingMessage;
import br.com.lunacom.leitordeindices.service.AtivoService;
import br.com.lunacom.leitordeindices.service.CotacaoService;
import br.com.lunacom.leitordeindices.service.Scraping;
import javassist.tools.rmi.ObjectNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

@Slf4j
@Component
public class RabbitQMConsumer {

    @Autowired
    private Scraping scrapingSiteAdvfnService;

    @Autowired
    private AtivoService ativoService;

    @Autowired
    private CotacaoService cotacaoService;

    @Autowired
    ScrapingHistoricoIncidesProducer producer;

    @RabbitListener(queues = "${rabbitmq.queue.scraping_solicitacao}")
    public void listen(SolicitacaoScrapingMessage solicitacaoScrapingMessage) {
        try {
            final String ativo = solicitacaoScrapingMessage.getAtivo();
            scrapingSiteAdvfnService.executar(
                    new ArrayList<>(Arrays.asList(ativo)),
                    new Date(),false);
            log.info("Message read from Queue : " + solicitacaoScrapingMessage);
            LocalDateTime ultimaCotacao = determinarUltimaCotacao(ativo);
            final ResultadoScrapingDto dto = ResultadoScrapingDto.builder()
                    .ativo(ativo)
                    .dia(ultimaCotacao.toString())
                    .build();
            producer.produce(dto);
        } catch (ObjectNotFoundException e) {
            log.error(e.getMessage());
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    private LocalDateTime determinarUltimaCotacao(String ativo) throws ObjectNotFoundException {
        Ativo obj = ativoService.searchAtivoByCodigo(ativo);
        Cotacao cotacao = cotacaoService.buscarCotacaoMaisRecente(obj);
        return cotacao.getReferencia().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
    }
}
