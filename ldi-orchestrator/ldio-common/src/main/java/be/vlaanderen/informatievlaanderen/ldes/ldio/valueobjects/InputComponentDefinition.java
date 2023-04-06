package be.vlaanderen.informatievlaanderen.ldes.ldio.valueobjects;

public class InputComponentDefinition extends ComponentDefinition {
	private ComponentDefinition adapter;

	public ComponentDefinition getAdapter() {
		return adapter;
	}

	public void setAdapter(ComponentDefinition adapter) {
		this.adapter = adapter;
	}
}
