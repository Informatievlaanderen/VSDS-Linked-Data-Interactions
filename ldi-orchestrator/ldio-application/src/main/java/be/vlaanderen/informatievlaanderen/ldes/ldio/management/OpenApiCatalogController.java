package be.vlaanderen.informatievlaanderen.ldes.ldio.management;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;

@Tag(name = "Catalog controller")
public interface OpenApiCatalogController {

    @ApiResponse(responseCode = "200", description = "A list of all available components is returned.", content = {
            @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, examples = {
                    @ExampleObject(value = """
                            {
                               "inputs": [
                                 "Ldio:HttpIn",
                                 "Ldio:HttpInPoller",
                                 "Ldio:KafkaIn",
                                 "Ldio:AmqpIn",
                                 "Ldio:LdesClient",
                                 "Ldio:LdesClientConnector",
                                 "Ldio:ArchiveFileIn"
                               ],
                               "adapters": [
                                 "Ldio:RdfAdapter",
                                 "Ldio:RmlAdapter",
                                 "Ldio:NgsiV2ToLdAdapter",
                                 "Ldio:JsonToLdAdapter"
                               ],
                               "transformers": [
                                 "Ldio:SparqlConstructTransformer",
                                 "Ldio:GeoJsonToWktTransformer",
                                 "Ldio:HttpEnricher",
                                 "Ldio:VersionObjectCreator",
                                 "Ldio:VersionMaterialiser"
                               ],
                               "outputs": [
                                 "Ldio:KafkaOut",
                                 "Ldio:AmqpOut",
                                 "Ldio:HttpOut",
                                 "Ldio:ConsoleOut",
                                 "Ldio:AzureBlobOut",
                                 "Ldio:FileOut",
                                 "Ldio:RepositoryMaterialiser",
                                 "Ldio:NoopOut"
                               ]
                             }
                                 """)
            })
    })
    @Operation(summary = "Get a list of all available components.")
    CatalogController.LdioCatalog catalog();

}
