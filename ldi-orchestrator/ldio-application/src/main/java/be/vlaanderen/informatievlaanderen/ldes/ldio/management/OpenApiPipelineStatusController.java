package be.vlaanderen.informatievlaanderen.ldes.ldio.management;

import be.vlaanderen.informatievlaanderen.ldes.ldio.statusmanagement.pipelinestatus.PipelineStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Map;

@Tag(name = "Pipeline status controller")
public interface OpenApiPipelineStatusController {

    @ApiResponse(responseCode = "200", description = "Returns a list of all the pipelines with their current status.", content = {
            @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)
    })
    @Operation(summary = "Get a list of all pipelines with their status.")
    Map<String, PipelineStatus.Value> getPipelineStatus();

    @ApiResponse(responseCode = "200", description = "Returns the current pipeline status",
            content = {@Content(mediaType = MediaType.TEXT_PLAIN_VALUE, schema = @Schema(implementation = PipelineStatus.Value.class))
    })
    @Operation(summary = "Get the current status of a given pipeline.")
    PipelineStatus.Value getPipelineStatus(@PathVariable("pipelineId") String pipelineId);

    @ApiResponse(responseCode = "200", description = "Halts the current pipeline",
            content = {@Content(mediaType = MediaType.TEXT_PLAIN_VALUE, schema = @Schema(implementation = PipelineStatus.Value.class))
            })
    @Operation(summary = "Pause a pipeline.")
    PipelineStatus.Value haltPipeline(@PathVariable("pipelineId") String pipelineId);

    @ApiResponse(responseCode = "200", description = "Resumes the current pipeline",
            content = {@Content(mediaType = MediaType.TEXT_PLAIN_VALUE, schema = @Schema(implementation = PipelineStatus.Value.class))
            })
    @Operation(summary = "Resume a pipeline.")
    PipelineStatus.Value resumePipeline(@PathVariable("pipelineId") String pipelineId);

}
