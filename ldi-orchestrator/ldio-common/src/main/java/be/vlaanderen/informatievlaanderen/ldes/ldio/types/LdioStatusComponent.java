package be.vlaanderen.informatievlaanderen.ldes.ldio.types;

import be.vlaanderen.informatievlaanderen.ldes.ldi.types.LdiComponent;

public interface LdioStatusComponent extends LdiComponent {
	void start();
	void resume();
	void pause();
	void shutdown();
}
