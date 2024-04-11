package be.vlaanderen.informatievlaanderen.ldes.ldio.statusmanagement.pipelinestatus;

import be.vlaanderen.informatievlaanderen.ldes.ldio.types.LdioStatusComponent;

public class ResumedPipelineStatus implements PipelineStatus {
	@Override
	public void updateComponentStatus(LdioStatusComponent ldioComponent) {
		ldioComponent.resume();
	}

	@Override
	public Value getStatusValue() {
		return Value.RUNNING;
	}
}
