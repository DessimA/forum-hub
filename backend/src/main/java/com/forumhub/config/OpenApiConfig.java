package com.forumhub.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        String description = """
            **FórumHub** is a state-of-the-art knowledge exchange platform designed with a focus on **Proactive Security**, **Clean Architecture**, and **Developer Experience**.
            
            ### 🚀 Key Features
            *   **Modular Monolith Architecture:** Domain-driven design with clear boundaries between Auth, Topics, Courses, and User Management.
            *   **Engagement System:** Built-in **Favorite Topics** and **Real-time Notifications** for updates on discussions.
            *   **Administrative Oversight:** Advanced **Audit Logs** with entity restoration capabilities and multi-level moderation.
            *   **Advanced Security:** JWT authentication with real-time block enforcement and proactive security filters.
            *   **Deterministic UUIDs:** High-performance binary IDs (MySQL 8 optimized) to prevent enumeration attacks.
            *   **Native Internationalization:** Full support for English and Portuguese (PT-BR) error messages.
            
            ### 🛡️ Security Note
            This API is protected. Use the **Authorize** button with your Bearer Token to test endpoints.
            
            ---
            Developed with ❤️ by **José Anderson da Silva Costa**.
            
            [**Website / My Links**](https://meus-links-olive.vercel.app/) | [**GitHub (DessimA)**](https://github.com/DessimA) | [**LinkedIn (in/dessim)**](https://www.linkedin.com/in/dessim)
            
            *"Knowledge grows when shared."*
            """;

        return new OpenAPI()
                .info(new Info()
                        .title("FórumHub API - Knowledge Exchange Platform")
                        .description(description)
                        .contact(new Contact()
                                .name("José Anderson da Silva Costa")
                                .url("https://meus-links-olive.vercel.app/"))
                        .version("v1.2.0")
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0.html")))
                .addSecurityItem(new SecurityRequirement().addList("bearer-key"))
                .components(new Components()
                        .addSecuritySchemes("bearer-key",
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                                        .description("Enter the JWT token obtained from the /auth/login endpoint.")));
    }
}