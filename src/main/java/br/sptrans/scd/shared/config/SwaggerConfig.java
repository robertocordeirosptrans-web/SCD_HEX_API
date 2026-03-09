package br.sptrans.scd.shared.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import br.sptrans.scd.shared.version.ApiVersionConfig;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("API REST - SCD")
                        .version(ApiVersionConfig.CURRENT_VERSION)
                        .description("""
                                API para gerenciamento de dados com versionamento.

                                **Versões Disponíveis:**
                                - v1: Versão atual e estável

                                **Base Path:** /api/v1

                                Todos os endpoints estão versionados e acessíveis através do prefixo /api/v1.
                                """)
                        .contact(new Contact()
                                .name("Time de Desenvolvimento")
                                .email("dev@suaempresa.com")
                                .url("https://www.sptrans.com.br/"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://springdoc.org")))
                .components(new Components()
                        .addSecuritySchemes("bearer-key",
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")))
                .addSecurityItem(new SecurityRequirement().addList("bearer-key"));
    }
}
