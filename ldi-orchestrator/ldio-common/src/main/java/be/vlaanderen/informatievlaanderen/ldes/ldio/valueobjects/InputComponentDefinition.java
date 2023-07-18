package be.vlaanderen.informatievlaanderen.ldes.ldio.valueobjects;

import java.io.Serializable;

public class InputComponentDefinition extends ComponentDefinition implements Serializable {
	private ComponentDefinition adapter;

	public ComponentDefinition getAdapter() {
		return adapter;
	}

	public void setAdapter(ComponentDefinition adapter) {
		this.adapter = adapter;
	}
}
