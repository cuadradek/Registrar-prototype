package cz.metacentrum.registrar.config;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RegistrarConfig {

	@Bean
	public ModelMapper modelMapper() {
		return new ModelMapper();
	}
}
