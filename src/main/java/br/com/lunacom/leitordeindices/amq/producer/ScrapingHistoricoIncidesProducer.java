package br.com.lunacom.leitordeindices.amq.producer;

import br.com.lunacom.leitordeindices.domain.dto.ResultadoScrapingDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ScrapingHistoricoIncidesProducer {
    @Autowired
    private AmqpTemplate amqpTemplate;

    @Value("${rabbitmq.exchange}")
    private String exchange;

    @Value("${rabbitmq.routingkey.concluido}")
    private String routingkey;

    public void produce(ResultadoScrapingDto data){
        amqpTemplate.convertAndSend(exchange, routingkey, data);
        log.info("Send msg = " + data);
    }
}
