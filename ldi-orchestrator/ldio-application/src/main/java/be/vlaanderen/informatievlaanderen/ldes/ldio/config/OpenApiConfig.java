package be.vlaanderen.informatievlaanderen.ldes.ldio.config;

import io.swagger.v3.oas.annotations.ExternalDocumentation;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@OpenAPIDefinition(
        info=@Info(title="LDIO Management API", description = "This API makes it possible manage the LDIO application."),
        externalDocs = @ExternalDocumentation(description = "LDIO documentation",
        url = "https://informatievlaanderen.github.io/VSDS-Linked-Data-Interactions/"))
@Configuration
public class OpenApiConfig {

    @Bean
    public GroupedOpenApi publicApi() {
        return GroupedOpenApi.builder()
                .group("management")
                .packagesToScan("be.vlaanderen.informatievlaanderen.ldes.ldio.management")
                .build();
    }

}
