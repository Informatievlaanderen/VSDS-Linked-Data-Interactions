package be.vlaanderen.informatievlaanderen.ldes.ldio.valueobjects;

import java.util.Map;

public class InputComponentDefinition extends ComponentDefinition {

	public InputComponentDefinition(String name, Map<String, String> config, ComponentDefinition adapter) {
		super(name, config);
		this.adapter = adapter;
	}

	public InputComponentDefinition() {
	}

	private ComponentDefinition adapter;

	public ComponentDefinition getAdapter() {
		return adapter;
	}

	public void setAdapter(ComponentDefinition adapter) {
		this.adapter = adapter;
	}
}
