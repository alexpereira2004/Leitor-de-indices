package br.com.lunacom.leitordeindices.amq.consumer;

import br.com.lunacom.leitordeindices.domain.message.SolicitacaoScrapingMessage;
import br.com.lunacom.leitordeindices.service.Scraping;
import javassist.tools.rmi.ObjectNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

@Slf4j
@Component
public class RabbitQMConsumer {

    @Autowired
    private Scraping scrapingSiteAdvfnService;

    @RabbitListener(queues = "${rabbitmq.queue.in}")
    public void listen(SolicitacaoScrapingMessage solicitacaoScrapingMessage) {
        try {
            scrapingSiteAdvfnService.executar(
                    new ArrayList<>(Arrays.asList(solicitacaoScrapingMessage.getAtivo())),
                    new Date(),false);
            log.info("Message read from Queue : " + solicitacaoScrapingMessage);
        } catch (ObjectNotFoundException e) {
            log.error(e.getMessage());
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }
}
