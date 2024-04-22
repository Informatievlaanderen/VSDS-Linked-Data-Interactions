package be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.executor.edc.services;

import be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.valueobjects.RequestHeader;

public interface TokenService {

	RequestHeader waitForTokenHeader();

	void invalidateToken();

	void updateToken(String token);

	void pause();

	void resume();

	void shutdown();
}
