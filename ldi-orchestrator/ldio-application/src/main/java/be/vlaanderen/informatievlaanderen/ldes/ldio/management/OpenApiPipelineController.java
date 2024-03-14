package be.vlaanderen.informatievlaanderen.ldes.ldio.management;

import be.vlaanderen.informatievlaanderen.ldes.ldio.valueobjects.PipelineConfigTO;
import be.vlaanderen.informatievlaanderen.ldes.ldio.valueobjects.PipelineTO;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.apache.jena.riot.WebContent.*;

@Tag(name = "Pipeline controller")
public interface OpenApiPipelineController {

    @ApiResponse(responseCode = "200", description = "A list of all active pipelines is shown.", content = {
            @Content(mediaType = contentTypeJSON, schema = @Schema(implementation = PipelineTO.class), examples = {
                    @ExampleObject(value = """
                                 [
                                   {
                                     "name": "demo",
                                     "status": "RUNNING",
                                     "description": "",
                                     "input": {
                                       "name": "Ldio:HttpIn",
                                       "adapter": {
                                         "name": "Ldio:RdfAdapter",
                                         "config": { }
                                       },
                                       "config": { }
                                     },
                                     "transformers": [ ],
                                     "outputs": [
                                       {
                                         "name": "Ldio:ConsoleOut",
                                         "config": { }
                                       }
                                     ]
                                   }
                                 ]
                                 """)
            })
    })
    @Operation(summary = "Get a list of all active pipelines.")
    @GetMapping
    List<PipelineTO> overview();

    @PostMapping()
    PipelineTO addPipeline(@RequestBody PipelineConfigTO config);

    @ApiResponse(responseCode = "202")
    @ApiResponse(responseCode = "204")
    @Operation(summary = "Delete a pipeline.")
    @DeleteMapping("/{pipeline}")
    ResponseEntity<Void> deletePipeline(@PathVariable String pipeline);

}
