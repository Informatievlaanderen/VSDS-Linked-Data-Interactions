package be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.executor.edc;

public interface TokenService {

    String waitForToken();

    void invalidateToken();

    void updateToken(String token);

}
