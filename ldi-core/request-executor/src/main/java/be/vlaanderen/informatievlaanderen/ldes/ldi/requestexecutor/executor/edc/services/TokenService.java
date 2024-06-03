package be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.executor.edc.services;

import be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.valueobjects.RequestHeader;

public interface TokenService {

	/**
	 * Awaits on the token header until the EDC token is available
	 *
	 * @return http header that contains the EDC token
	 */
	RequestHeader waitForTokenHeader();

	/**
	 * Clears the EDC token, so it cannot be used anymore
	 */
	void invalidateToken();

	/**
	 * Sets the EDC token based on the string
	 *
	 * @param token string representation of the EDC token
	 */
	void updateToken(String token);

	void pause();

	void resume();

	void shutdown();
}
