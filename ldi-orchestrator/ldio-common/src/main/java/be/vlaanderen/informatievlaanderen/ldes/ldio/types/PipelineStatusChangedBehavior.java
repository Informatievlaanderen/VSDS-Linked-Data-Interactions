package be.vlaanderen.informatievlaanderen.ldes.ldio.types;

import be.vlaanderen.informatievlaanderen.ldes.ldi.types.LdiComponent;

@FunctionalInterface
public interface PipelineStatusChangedBehavior<T extends LdiComponent> {
	void applyNewStatus(T ldioComponent);
}
