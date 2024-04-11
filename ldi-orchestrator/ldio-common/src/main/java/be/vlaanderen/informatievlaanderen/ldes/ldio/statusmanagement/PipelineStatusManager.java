package be.vlaanderen.informatievlaanderen.ldes.ldio.statusmanagement;

import be.vlaanderen.informatievlaanderen.ldes.ldi.types.LdiOutput;
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
	private final List<LdiOutput> outputs;

	public PipelineStatusManager(String pipelineName, LdioInput input, List<LdiOutput> outputs) {
		this.pipelineName = pipelineName;
		this.input = input;
		this.outputs = outputs;
	}

	public static PipelineStatusManager initWithStatus(String name, LdioInput input, List<LdiOutput> outputs, PipelineStatus status) {
		final var pipelineStatusManager = new PipelineStatusManager(name, input, outputs);
		pipelineStatusManager.updatePipelineStatus(status, StatusChangeSource.AUTO);
		return pipelineStatusManager;
	}

	public StatusChangeSource getLastStatusChangeSource() {
		return lastStatusChangeSource;
	}

	public String getPipelineName() {
		return pipelineName;
	}

	public LdioInput getInput() {
		return input;
	}

	public List<LdiOutput> getOutputs() {
		return outputs;
	}

	public Value updatePipelineStatus(PipelineStatus pipelineStatus, StatusChangeSource statusChangeSource) {
		final boolean isStatusUpdated = setPipelineStatus(pipelineStatus);
		if (isStatusUpdated) {
			this.lastStatusChangeSource = statusChangeSource;
			updateComponentsStatus();
		}
		return this.pipelineStatus.getStatusValue();
	}

	public Value updatePipelineStatus(PipelineStatus pipelineStatus) {
		return this.updatePipelineStatus(pipelineStatus, StatusChangeSource.MANUAL);
	}

	private boolean setPipelineStatus(PipelineStatus newPipelineStatus) {
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
		outputs.stream()
				.filter(LdioStatusComponent.class::isInstance)
				.map(LdioStatusComponent.class::cast)
				.forEach(pipelineStatus::updateComponentStatus);

	}
}
