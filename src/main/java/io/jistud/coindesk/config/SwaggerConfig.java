package io.jistud.coindesk.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;

/**
 * Configuration class for Swagger OpenAPI documentation.
 */
@Configuration
public class SwaggerConfig {

    /**
     * Configures the OpenAPI documentation with application information.
     *
     * @return the OpenAPI configuration
     */
    @Bean
    public OpenAPI coinDeskOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("CoinDesk API")
                        .description("API for managing cryptocurrency data and integrating with CoinDesk")
                        .version("v1.0")
                        .contact(new Contact()
                                .name("Coindesk API Team")
                                .email("support@example.com")));
    }
}
