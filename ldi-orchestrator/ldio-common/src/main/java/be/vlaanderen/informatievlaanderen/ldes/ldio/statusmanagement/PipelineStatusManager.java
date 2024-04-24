package be.vlaanderen.informatievlaanderen.ldes.ldio.statusmanagement;

import be.vlaanderen.informatievlaanderen.ldes.ldio.statusmanagement.pipelinestatus.*;
import be.vlaanderen.informatievlaanderen.ldes.ldio.types.LdioInput;
import be.vlaanderen.informatievlaanderen.ldes.ldio.types.LdioStatusComponent;
import be.vlaanderen.informatievlaanderen.ldes.ldio.valueobjects.StatusChangeSource;

import java.util.List;

import static be.vlaanderen.informatievlaanderen.ldes.ldio.statusmanagement.pipelinestatus.PipelineStatus.*;
import static be.vlaanderen.informatievlaanderen.ldes.ldio.statusmanagement.pipelinestatus.PipelineStatus.Value.*;
import static be.vlaanderen.informatievlaanderen.ldes.ldio.statusmanagement.pipelinestatus.PipelineStatus.Value.RUNNING;

public class PipelineStatusManager {
	private PipelineStatus pipelineStatus;
	private StatusChangeSource lastStatusChangeSource;
	private final String pipelineName;
	private final LdioInput input;
	private final List<LdioStatusComponent> outputs;

	public PipelineStatusManager(String pipelineName, LdioInput input, List<LdioStatusComponent> outputs) {
		this.pipelineName = pipelineName;
		this.input = input;
		this.outputs = outputs;
	}

	public static PipelineStatusManager initWithStatus(String name, LdioInput input, List<LdioStatusComponent> outputs, PipelineStatus status) {
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
			this.lastStatusChangeSource = statusChangeSource;
			updateComponentsStatus();
		}
		return this.pipelineStatus;
	}

	public PipelineStatus updatePipelineStatus(PipelineStatus pipelineStatus) {
		return this.updatePipelineStatus(pipelineStatus, StatusChangeSource.MANUAL);
	}

	private boolean tryUpdatePipelineStatus(PipelineStatus newPipelineStatus) {
		final Value currentSatusValue = pipelineStatus == null ? INIT : pipelineStatus.getStatusValue();

		return switch (newPipelineStatus.getStatusValue()) {
			case RUNNING -> {
				if (currentSatusValue == HALTED || currentSatusValue == INIT) {
					this.pipelineStatus = newPipelineStatus;
					yield true;
				}
				yield false;
			}
			case HALTED -> {
				if (currentSatusValue == RUNNING) {
					this.pipelineStatus = newPipelineStatus;
					yield true;
				}
				yield false;
			}
			default -> {
				this.pipelineStatus = newPipelineStatus;
				yield true;
			}
		};
	}

	private void updateComponentsStatus() {
		pipelineStatus.updateComponentStatus(input);
		outputs.forEach(pipelineStatus::updateComponentStatus);

	}
}
