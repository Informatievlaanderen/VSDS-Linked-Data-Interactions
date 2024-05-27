package be.vlaanderen.informatievlaanderen.ldes.ldio.statusmanagement;

import be.vlaanderen.informatievlaanderen.ldes.ldio.statusmanagement.pipelinestatus.InitPipelineStatus;
import be.vlaanderen.informatievlaanderen.ldes.ldio.statusmanagement.pipelinestatus.PipelineStatus;
import be.vlaanderen.informatievlaanderen.ldes.ldio.statusmanagement.pipelinestatus.StartedPipelineStatus;
import be.vlaanderen.informatievlaanderen.ldes.ldio.types.LdioInput;
import be.vlaanderen.informatievlaanderen.ldes.ldio.types.LdioStatusComponent;
import be.vlaanderen.informatievlaanderen.ldes.ldio.types.LdioStatusOutput;
import be.vlaanderen.informatievlaanderen.ldes.ldio.valueobjects.StatusChangeSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class PipelineStatusManager {
	private static final Logger log = LoggerFactory.getLogger(PipelineStatusManager.class);
	private PipelineStatus pipelineStatus;
	private StatusChangeSource lastStatusChangeSource;
	private final String pipelineName;
	private final LdioInput input;
	private final List<LdioStatusOutput> outputs;

	private PipelineStatusManager(String pipelineName, LdioInput input, List<LdioStatusOutput> outputs) {
		this.pipelineName = pipelineName;
		this.input = input;
		this.outputs = outputs;
	}

	public static PipelineStatusManager initialize(String pipelineName, LdioInput input, List<LdioStatusOutput> outputs) {
		return initializeWithStatus(pipelineName, input, outputs, new InitPipelineStatus());
	}

	public static PipelineStatusManager initializeWithStatus(String name, LdioInput input, List<LdioStatusOutput> outputs, PipelineStatus status) {
		final var pipelineStatusManager = new PipelineStatusManager(name, input, outputs);
		pipelineStatusManager.updatePipelineStatus(status, StatusChangeSource.AUTO);
		return pipelineStatusManager;
	}

	public StatusChangeSource getLastStatusChangeSource() {
		return lastStatusChangeSource;
	}

	public PipelineStatus getPipelineStatus() {
		return pipelineStatus;
	}

	public PipelineStatus.Value getPipelineStatusValue() {
		return pipelineStatus.getStatusValue();
	}

	public String getPipelineName() {
		return pipelineName;
	}

	public LdioInput getInput() {
		return input;
	}

	public List<LdioStatusComponent> getOutputs() {
		return List.copyOf(outputs);
	}

	public PipelineStatus updatePipelineStatus(PipelineStatus pipelineStatus, StatusChangeSource statusChangeSource) {
		final boolean isStatusUpdated = tryUpdatePipelineStatus(pipelineStatus);
		if (isStatusUpdated) {
			log.atInfo().log("UPDATED status for pipeline '{}' to {}", pipelineName, pipelineStatus.getStatusValue());
			this.lastStatusChangeSource = statusChangeSource;
			updateComponentsStatus();
		}
		return this.pipelineStatus;
	}

	public PipelineStatus updatePipelineStatus(PipelineStatus pipelineStatus) {
		return this.updatePipelineStatus(pipelineStatus, StatusChangeSource.MANUAL);
	}

	private boolean tryUpdatePipelineStatus(PipelineStatus newPipelineStatus) {
		if(pipelineStatus == null) {
			if(newPipelineStatus instanceof InitPipelineStatus || newPipelineStatus instanceof StartedPipelineStatus) {
				pipelineStatus = newPipelineStatus;
				return true;
			}
			return false;
		}
		if(pipelineStatus.canGoToStatus(newPipelineStatus)) {
			pipelineStatus = newPipelineStatus;
			return true;
		}
		return false;
	}

	private void updateComponentsStatus() {
		pipelineStatus.updateComponentStatus(input);
		outputs.forEach(pipelineStatus::updateComponentStatus);

	}
}
