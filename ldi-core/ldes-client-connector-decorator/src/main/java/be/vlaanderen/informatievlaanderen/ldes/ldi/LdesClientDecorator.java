package be.vlaanderen.informatievlaanderen.ldes.ldi;

import be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.exceptions.HttpRequestException;
import ldes.client.treenodesupplier.MemberSupplier;
import ldes.client.treenodesupplier.domain.valueobject.SuppliedMember;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

import static java.lang.Thread.sleep;

public class LdesClientDecorator {

    private final TokenService tokenService;
    private final MemberSupplier memberSupplier;

    public LdesClientDecorator(TokenService tokenService, MemberSupplier memberSupplier) {
        this.tokenService = tokenService;
        this.memberSupplier = memberSupplier;
    }

    // TODO TVB: 08/11/23 mss met eventlisteners werken?
    @EventListener
    SuppliedMember nextMember() {
        String token = tokenService.waitForToken();
        // update requestExecutor with new token
        try {
            return memberSupplier.get();
        } catch (HttpRequestException httpRequestException) {
            // if forbidden
            tokenService.invalidateToken();
            tokenService.waitForNewToken();
        }
    }

}
