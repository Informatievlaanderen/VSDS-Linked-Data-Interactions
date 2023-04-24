package be.vlaanderen.informatievlaanderen.ldes.ldio.events;

import be.vlaanderen.informatievlaanderen.ldes.ldi.types.LdiAdapter;
import be.vlaanderen.informatievlaanderen.ldes.ldio.types.LdiOrder;

public record PipelineDataAdaptEvent(String targetComponent, LdiOrder ldiOrder, Content content) {
	record Content(String content, String mimeType) {
		public static LdiAdapter.Content of(String content, String mimeType) {
			return new LdiAdapter.Content(content, mimeType);
		}
	}
}
