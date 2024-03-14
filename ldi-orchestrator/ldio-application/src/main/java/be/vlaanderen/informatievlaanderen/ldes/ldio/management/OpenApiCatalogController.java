package be.vlaanderen.informatievlaanderen.ldes.ldio.management;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;

@Tag(name = "Catalog controller")
public interface OpenApiCatalogController {

    @ApiResponse(responseCode = "200", description = "A list of all available components is returned.", content = {
            @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = CatalogController.LdioCatalog.class))
    })
    @Operation(summary = "Get a list of all available components.")
    CatalogController.LdioCatalog catalog();

}
