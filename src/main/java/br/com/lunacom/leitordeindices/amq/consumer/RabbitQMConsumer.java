package br.com.lunacom.leitordeindices.amq.consumer;

import br.com.lunacom.leitordeindices.amq.producer.ScrapingHistoricoIncidesProducer;
import br.com.lunacom.leitordeindices.domain.dto.ResultadoScrapingDto;
import br.com.lunacom.leitordeindices.domain.message.SolicitacaoScrapingMessage;
import br.com.lunacom.leitordeindices.service.Scraping;
import javassist.tools.rmi.ObjectNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

@Slf4j
@Component
public class RabbitQMConsumer {

    @Autowired
    private Scraping scrapingSiteAdvfnService;

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
            final ResultadoScrapingDto dto = ResultadoScrapingDto.builder()
                    .ativo(ativo)
                    .dia(LocalDateTime.now().toString())
                    .build();
            producer.produce(dto);
        } catch (ObjectNotFoundException e) {
            log.error(e.getMessage());
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }
}
