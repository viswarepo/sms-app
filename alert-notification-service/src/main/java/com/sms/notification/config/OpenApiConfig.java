package com.sms.notification.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.tags.Tag;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI subscriptionServiceOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("Notification Service API")
                        .description("""
                                notification service send email and sms notification to customers
                                """)
                        .version("v1")
                        .contact(new Contact().name("sms Team").email("test@sms.com"))
                        .license(new License().name("Apache 2.0").url("https://www.apache.org/licenses/LICENSE-2.0")))
                .tags(List.of(
                        new Tag().name("Email").description("send to customers"),
                        new Tag().name("Sms").description("send to customers")
                ));
    }
}
