package be.vlaanderen.informatievlaanderen.ldes.ldio.statusmanagement.pipelinestatus;

import be.vlaanderen.informatievlaanderen.ldes.ldio.types.LdioStatusComponent;

public class HaltedPipelineStatus implements PipelineStatus {
	@Override
	public void updateComponentStatus(LdioStatusComponent ldioComponent) {
		ldioComponent.pause();
	}

	@Override
	public Value getStatusValue() {
		return Value.HALTED;
	}

	@Override
	public boolean canGoToStatus(PipelineStatus status) {
		return status instanceof RunningPipelineStatus || status instanceof StoppedPipelineStatus;
	}
}
