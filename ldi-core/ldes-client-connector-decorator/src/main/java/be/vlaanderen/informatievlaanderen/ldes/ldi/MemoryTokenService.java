//package be.vlaanderen.informatievlaanderen.ldes.ldi;
//
//import be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.executor.edc.TransferService;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import java.time.Duration;
//
//import static org.apache.commons.lang3.ThreadUtils.sleep;
//
//public class MemoryTokenService implements TokenService {
//
//    private final be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.executor.edc.TransferService transferService;
//    private final Logger log = LoggerFactory.getLogger(be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.executor.edc.MemoryTokenService.class);
//
//    private String token;
//
//    public MemoryTokenService(TransferService transferService) {
//        this.transferService = transferService;
//    }
//
//    public String waitForToken() {
//        if (token != null) {
//            return token;
//        }
//
//        try {
//            log.info("waiting for token..");
//            sleep(Duration.ofSeconds(5));
//        } catch (InterruptedException e) {
//            Thread.currentThread().interrupt();
//        }
//
//        return waitForToken();
//    }
//
//    public void invalidateToken() {
//        token = null;
//        transferService.refreshTransfer();
//    }
//
//    @Override
//    public void updateToken(String token) {
//        this.token = token;
//    }
//
//}
