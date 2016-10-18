package edu.uncfsu.softwaredesign.f16.r2;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApplicationConfiguration {

	@Bean
	public CardRegistry cardRegistry() {
		return new CardRegistry();
	}
	
}
