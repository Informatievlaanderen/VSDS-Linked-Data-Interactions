package be.vlaanderen.informatievlaanderen.ldes.ldio.statusmanagement.pipelinestatus;

import be.vlaanderen.informatievlaanderen.ldes.ldio.types.LdioStatusComponent;

public class InitPipelineStatus implements PipelineStatus {
	@Override
	public void updateComponentStatus(LdioStatusComponent ldioComponent) {
		ldioComponent.start();
	}

	@Override
	public Value getStatusValue() {
		return Value.INIT;
	}

	@Override
	public boolean canGoToStatus(PipelineStatus status) {
		return status instanceof RunningPipelineStatus || status instanceof StoppedPipelineStatus;
	}
}
