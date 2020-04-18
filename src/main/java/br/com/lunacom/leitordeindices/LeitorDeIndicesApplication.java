package br.com.lunacom.leitordeindices;

import br.com.lunacom.leitordeindices.service.ScrapingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class LeitorDeIndicesApplication {

	public static void main(String[] args) {
		SpringApplication.run(LeitorDeIndicesApplication.class, args);
	}

}
