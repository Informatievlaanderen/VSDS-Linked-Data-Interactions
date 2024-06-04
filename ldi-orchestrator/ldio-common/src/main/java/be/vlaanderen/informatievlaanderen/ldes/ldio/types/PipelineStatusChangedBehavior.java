package be.vlaanderen.informatievlaanderen.ldes.ldio.types;

import be.vlaanderen.informatievlaanderen.ldes.ldi.types.LdiComponent;

/**
 * Behavior interface that can be implemented for each pipeline status change
 */
@FunctionalInterface
public interface PipelineStatusChangedBehavior<T extends LdiComponent> {
	void applyNewStatus(T ldioComponent);
}
