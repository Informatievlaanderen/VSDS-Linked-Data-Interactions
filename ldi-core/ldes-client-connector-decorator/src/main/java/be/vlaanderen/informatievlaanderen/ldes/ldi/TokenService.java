package be.vlaanderen.informatievlaanderen.ldes.ldi;

import java.util.Optional;

public interface TokenService {

    String waitForNewToken();

    void invalidateToken();

    void updateToken(String token);

}
