package be.vlaanderen.informatievlaanderen.ldes.ldio.management.status;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import ldes.client.treenodesupplier.domain.valueobject.ClientStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Tag(name = "LDES Client Status")
@RequestMapping("/admin/api/v1/pipeline/ldes-client")
public class ClientStatusController {
	private final ClientStatusService clientStatusService;

	public ClientStatusController(ClientStatusService clientStatusService) {
		this.clientStatusService = clientStatusService;
	}

	@GetMapping()
	@ApiResponse(responseCode = "200", description = "A list statuses of all active LDES Client pipelines.", content = {
			@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, array = @ArraySchema(schema = @Schema(implementation = ClientStatusTo.class))),
	})
	@Operation(summary = "Get a list of all LDES Client statuses pipelines.")
	public List<ClientStatusTo> getStatusses() {
		return clientStatusService.getClientStatuses();
	}

	@GetMapping(path = "{pipeline}", produces = "application/json")
	@ApiResponse(responseCode = "200", description = "Status of a requested pipeline", content = {
			@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ClientStatus.class)),
	})
	@ApiResponse(responseCode = "404", description = "No LDES Client pipeline exists by that name", content = {
			@Content(schema = @Schema()),
	})
	@Operation(summary = "Get the status of a requested LDES Client pipeline.")
	public ResponseEntity<ClientStatus> getPipelineStatus(@PathVariable("pipeline") String pipeline) {
		return clientStatusService.getClientStatus(pipeline)
				.map(ResponseEntity::ok)
				.orElse(ResponseEntity.notFound().build());
	}
}
