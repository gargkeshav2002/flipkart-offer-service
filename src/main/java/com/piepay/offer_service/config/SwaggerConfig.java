package com.piepay.offer_service.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("PiePay Offer Service API")
                        .version("1.0.0")
                        .description(
                                "Backend APIs for saving and querying Flipkart-style offers.<br/><br/>" +
                                        "📘 <b>Code:</b> <a href=\"https://github.com/gargkeshav2002/flipkart-offer-service\">Click here</a><br/>" +
                                        "🗃️ <b>Access H2 Console:</b> <a href='http://localhost:8080/h2-console' target='_blank'>Open H2 Console</a><br/>" +
                                        "🔗 <b>JDBC URL:</b> <code>jdbc:h2:mem:offersdb</code><br/>" +
                                        "👤 <b>Username:</b> <code>SA</code> | <b>Password:</b> (leave blank)"
                        )
                        .contact(new Contact()
                                .name("Keshav Garg")
                                .email("keshavgarg019@gmail.com")
                                .url("https://www.linkedin.com/in/keshav-garg01/")
                        )
                );
    }
}
