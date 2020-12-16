package br.com.lunacom.leitordeindices.factory;

import br.com.lunacom.leitordeindices.service.Scraping;
import br.com.lunacom.leitordeindices.service.ScrapingInvestingComService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ScrapingHistoricoAtivosFactory {

    @Autowired
    private static List<Scraping> scrapingHistoricoAtivosServiceList;

    public static Scraping create(String site) {
        final Scraping service = scrapingHistoricoAtivosServiceList
                .stream()
                .filter(r -> r.getOrigem().equals(site))
                .findFirst()
                .orElse(new ScrapingInvestingComService());
        return service;
    }
}
