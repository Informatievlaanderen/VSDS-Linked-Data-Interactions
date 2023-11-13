//package be.vlaanderen.informatievlaanderen.ldes.ldi;
//
//import be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.executor.RequestExecutor;
//import be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.valueobjects.PostRequest;
//import be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.valueobjects.RequestHeader;
//import be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.valueobjects.RequestHeaders;
//
//import java.util.List;
//
//public class MemoryTransferService implements TransferService {
//
//    private final RequestExecutor requestExecutor;
//    private String transfer;
//
//    public MemoryTransferService(RequestExecutor requestExecutor) {
//        this.requestExecutor = requestExecutor;
//    }
//
//    @Override
//    public void startTransfer(String transfer) {
//        this.transfer = transfer;
//        sendTransferRequest();
//    }
//
//    @Override
//    public void refreshTransfer() {
//        sendTransferRequest();
//    }
//
//    private void sendTransferRequest() {
//        var requestHeaders = new RequestHeaders(List.of(new RequestHeader("Content-Type", "application/json")));
//        var request = new PostRequest("url", requestHeaders, transfer);
//        this.requestExecutor.execute(request);
//    }
//
//}
