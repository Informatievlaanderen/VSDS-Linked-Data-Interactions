package be.vlaanderen.informatievlaanderen.ldes.ldio.statusmanagement.pipelinestatus;

import be.vlaanderen.informatievlaanderen.ldes.ldio.types.LdioStatusComponent;

public class RunningPipelineStatus implements PipelineStatus {
	@Override
	public void updateComponentStatus(LdioStatusComponent ldioComponent) {
		ldioComponent.resume();
	}

	@Override
	public Value getStatusValue() {
		return Value.RUNNING;
	}

	@Override
	public boolean canGoToStatus(PipelineStatus status) {
		return status instanceof HaltedPipelineStatus || status instanceof StoppedPipelineStatus;
	}
}
