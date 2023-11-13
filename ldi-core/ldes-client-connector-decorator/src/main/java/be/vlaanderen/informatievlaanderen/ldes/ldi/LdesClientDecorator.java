//package be.vlaanderen.informatievlaanderen.ldes.ldi;
//
//import be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.exceptions.HttpRequestException;
//import ldes.client.treenodesupplier.MemberSupplier;
//import ldes.client.treenodesupplier.domain.valueobject.SuppliedMember;
//
//import java.time.LocalDateTime;
//import java.time.temporal.ChronoUnit;
//import java.util.Optional;
//
//import static java.lang.Thread.sleep;
//
//public class LdesClientDecorator implements TokenSubscriber {
//
// TODO TVB: 13/11/23 REMOVE THIS MODULE
//    private String token;
//    private final TokenService tokenService;
//    private final MemberSupplier memberSupplier;
//
//    public LdesClientDecorator(TokenService tokenService, MemberSupplier memberSupplier) {
//        this.tokenService = tokenService;
//        tokenService.subscribe(this);
//        this.memberSupplier = memberSupplier;
//    }
//
//    SuppliedMember nextMember() {
//        if (token == null) {
//            // sleep
//            return nextMember();
//        }
//
//
//        String token = tokenService.waitForNewToken();
//        // update requestExecutor with new token
//        try {
//            return memberSupplier.get();
//        } catch (HttpRequestException httpRequestException) {
//            // if forbidden
//            tokenService.invalidateToken();
//            return nextMember();
//        }
//    }
//
//    @Override
//    public void updateToken(String token) {
//
//    }
//
//}
