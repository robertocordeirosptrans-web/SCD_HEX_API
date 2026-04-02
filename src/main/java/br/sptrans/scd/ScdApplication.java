package br.sptrans.scd;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@EnableSpringDataWebSupport(pageSerializationMode = EnableSpringDataWebSupport.PageSerializationMode.VIA_DTO)
@EntityScan(basePackages = "br.sptrans.scd")
@EnableJpaRepositories(basePackages = "br.sptrans.scd")
public class ScdApplication {

	public static void main(String[] args) {
		SpringApplication.run(ScdApplication.class, args);
	}

}
