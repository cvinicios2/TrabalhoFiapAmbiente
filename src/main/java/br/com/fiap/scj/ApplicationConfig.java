package br.com.fiap.scj;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan("br.com.fiap.scj")
public class ApplicationConfig {

	@Bean
	public IntegrarFilasRouter executarRouter() {
		return new IntegrarFilasRouter();
	}
}
