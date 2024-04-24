package be.vlaanderen.informatievlaanderen.ldes.ldio.valueobjects;

import be.vlaanderen.informatievlaanderen.ldes.ldio.LdioLdesClientConnectorApi;
import be.vlaanderen.informatievlaanderen.ldes.ldio.statusmanagement.PipelineStatusManager;
import be.vlaanderen.informatievlaanderen.ldes.ldio.statusmanagement.pipelinestatus.PipelineStatus;
import be.vlaanderen.informatievlaanderen.ldes.ldio.types.LdioInput;
import be.vlaanderen.informatievlaanderen.ldes.ldio.types.LdioStatusComponent;

import java.util.List;

public class ConnectorPipelineStatusManager extends PipelineStatusManager {
	private final LdioLdesClientConnectorApi ldioLdesClientConnectorApi;
	private final PipelineStatusManager pipelineStatusManager;

	public ConnectorPipelineStatusManager(String pipelineName, LdioInput input, List<LdioStatusComponent> outputs, LdioLdesClientConnectorApi ldioLdesClientConnectorApi, PipelineStatusManager pipelineStatusManager) {
		super(pipelineName, input, outputs);
		this.ldioLdesClientConnectorApi = ldioLdesClientConnectorApi;
		this.pipelineStatusManager = pipelineStatusManager;
	}
}
