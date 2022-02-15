package br.com.lunacom.leitordeindices;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@EnableEurekaClient
@SpringBootApplication
public class LeitorDeIndicesApplication {

	public static void main(String[] args) {
		SpringApplication.run(LeitorDeIndicesApplication.class, args);
	}

}
