package com.devsu.cuentas.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("MS Cuentas API")
                        .version("1.0.0")
                        .description("Microservicio de gestión de cuentas, movimientos y reportes bancarios")
                        .contact(new Contact()
                                .name("Devsu")
                                .email("devsu@devsu.com")));
    }
}
