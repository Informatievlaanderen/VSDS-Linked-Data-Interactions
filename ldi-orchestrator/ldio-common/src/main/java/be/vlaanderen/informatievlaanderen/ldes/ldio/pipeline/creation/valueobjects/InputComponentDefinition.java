package be.vlaanderen.informatievlaanderen.ldes.ldio.pipeline.creation.valueobjects;

import java.util.Map;

public class InputComponentDefinition extends ComponentDefinition {

	public InputComponentDefinition(String pipelineName, String name, Map<String, String> config, ComponentDefinition adapter) {
		super(pipelineName, name, config == null ? Map.of() : config);
		this.adapter = adapter;
	}
	private ComponentDefinition adapter;

	public ComponentDefinition getAdapter() {
		return adapter;
	}

	public void setAdapter(ComponentDefinition adapter) {
		this.adapter = adapter;
	}
}
