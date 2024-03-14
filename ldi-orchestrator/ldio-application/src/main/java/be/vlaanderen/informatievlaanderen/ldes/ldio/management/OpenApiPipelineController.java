package be.vlaanderen.informatievlaanderen.ldes.ldio.management;

import be.vlaanderen.informatievlaanderen.ldes.ldio.valueobjects.PipelineConfigTO;
import be.vlaanderen.informatievlaanderen.ldes.ldio.valueobjects.PipelineTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@Tag(name = "Pipeline controller")
public interface OpenApiPipelineController {

    @ApiResponse(responseCode = "200", description = "A list of all active pipelines is shown.", content = {
            @Content(mediaType = "application/json", schema = @Schema(implementation = PipelineTO.class), examples = {
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
            }), @Content(mediaType = "application/yaml", schema = @Schema(implementation = PipelineTO.class), examples = {
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

    @ApiResponse(responseCode = "201", description = "The new pipeline is returned.", content = {
            @Content(mediaType = "application/json", schema = @Schema(implementation = PipelineTO.class), examples = {
                    @ExampleObject(value = """
                            {
                              "name": "demo",
                              "status": "RUNNING",
                              "updateSource": "AUTO",
                              "description": "",
                              "input": {
                                "name": "Ldio:HttpIn",
                                "adapter": {
                                  "name": "Ldio:RdfAdapter",
                                  "config": {}
                                },
                                "config": {}
                              },
                              "transformers": [],
                              "outputs": [
                                {
                                  "name": "Ldio:ConsoleOut",
                                  "config": {}
                                }
                              ]
                            }
                                 """)
            }),
            @Content(mediaType = "application/yaml", schema = @Schema(implementation = PipelineTO.class), examples = {
                    @ExampleObject(value = """
                            {
                              "name": "demo",
                              "status": "RUNNING",
                              "updateSource": "AUTO",
                              "description": "",
                              "input": {
                                "name": "Ldio:HttpIn",
                                "adapter": {
                                  "name": "Ldio:RdfAdapter",
                                  "config": {}
                                },
                                "config": {}
                              },
                              "transformers": [],
                              "outputs": [
                                {
                                  "name": "Ldio:ConsoleOut",
                                  "config": {}
                                }
                              ]
                            }
                                 """)
            })
    })
    @Operation(summary = "Create a new pipeline.")
    @PostMapping
    PipelineTO addPipeline(@RequestBody(description = "The pipeline configuration", content = {
            @Content(mediaType = "application/json", schema = @Schema(implementation = PipelineTO.class), examples = {
                    @ExampleObject(value = """
                            {
                              "name": "demo",
                              "description": "",
                              "input": {
                                "name": "Ldio:HttpIn",
                                "adapter": {
                                  "name": "Ldio:RdfAdapter",
                                  "config": {}
                                },
                                "config": {}
                              },
                              "transformers": [],
                              "outputs": [
                                {
                                  "name": "Ldio:ConsoleOut",
                                  "config": {}
                                }
                              ]
                            }
                            """)
            }),
            @Content(mediaType = "application/yaml", schema = @Schema(implementation = PipelineTO.class), examples = {
                    @ExampleObject(value = """
                            {
                              "name": "demo",
                              "description": "",
                              "input": {
                                "name": "Ldio:HttpIn",
                                "adapter": {
                                  "name": "Ldio:RdfAdapter",
                                  "config": {}
                                },
                                "config": {}
                              },
                              "transformers": [],
                              "outputs": [
                                {
                                  "name": "Ldio:ConsoleOut",
                                  "config": {}
                                }
                              ]
                            }
                            """)
            }),
    }) PipelineConfigTO config);

    @ApiResponse(responseCode = "202")
    @ApiResponse(responseCode = "204")
    @Operation(summary = "Delete a pipeline.")
    @DeleteMapping("/{pipeline}")
    ResponseEntity<Void> deletePipeline(@PathVariable String pipeline);

}
