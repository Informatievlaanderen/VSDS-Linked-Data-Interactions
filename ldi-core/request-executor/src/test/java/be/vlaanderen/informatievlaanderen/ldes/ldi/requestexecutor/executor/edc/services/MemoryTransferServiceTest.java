package be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.executor.edc.services;

import be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.executor.RequestExecutor;
import be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.valueobjects.PostRequest;
import be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.valueobjects.RequestHeader;
import be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.valueobjects.RequestHeaders;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.apache.http.HttpHeaders;
import org.apache.http.entity.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class MemoryTransferServiceTest {

    @InjectMocks
    private MemoryTransferService memoryTransferService;

    @Mock
    private RequestExecutor requestExecutor;

    private final String consumerConnectorUrl = "http://example.org";

    @BeforeEach
    public void setUp() throws IllegalAccessException {
        FieldUtils.writeField(memoryTransferService, "consumerConnectorUrl", consumerConnectorUrl, true);
    }

    @Test
    void refreshTransfer_ShouldThrowException_WhenNoTransferStarted() {
        assertThrows(IllegalStateException.class, memoryTransferService::refreshTransfer);
    }

    @Test
    void startTransfer_AndrefreshTransfer_ShouldSendTransfer_WhenTransferIsAvailable() {
        var transfer = "my-transfer";
        memoryTransferService.startTransfer(transfer);
        memoryTransferService.refreshTransfer();

        var contentType = new RequestHeader(HttpHeaders.CONTENT_TYPE, ContentType.APPLICATION_JSON.getMimeType());
        var requestHeaders = new RequestHeaders(List.of(contentType));
        var request = new PostRequest(consumerConnectorUrl, requestHeaders, transfer);
        verify(requestExecutor, times(2)).execute(request);
    }
}