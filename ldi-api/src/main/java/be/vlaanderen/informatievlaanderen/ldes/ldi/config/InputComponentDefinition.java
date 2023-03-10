package be.vlaanderen.informatievlaanderen.ldes.ldi.config;

public class InputComponentDefinition extends ComponentDefinition {
	ComponentDefinition adapter;

	public ComponentDefinition getAdapter() {
		return adapter;
	}

	public void setAdapter(ComponentDefinition adapter) {
		this.adapter = adapter;
	}
}
