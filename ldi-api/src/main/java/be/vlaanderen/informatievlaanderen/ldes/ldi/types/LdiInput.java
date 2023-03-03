package be.vlaanderen.informatievlaanderen.ldes.ldi.types;

import be.vlaanderen.informatievlaanderen.ldes.ldi.services.ComponentExecutor;

public interface LdiInput extends LdiComponent {
	LdiInput withExecutor(ComponentExecutor executor);

	LdiInput withAdapter(LdiAdapter adapter);
}
