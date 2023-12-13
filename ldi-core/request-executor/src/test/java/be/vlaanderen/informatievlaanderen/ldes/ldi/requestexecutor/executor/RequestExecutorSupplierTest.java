package be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.executor;

import be.vlaanderen.informatievlaanderen.ldes.ldio.valueobjects.ComponentProperties;
import org.apache.http.Header;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.executor.RequestExecutorSupplier.CUSTOM_HEADERS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RequestExecutorSupplierTest {

    @Mock
    ComponentProperties props;

    @Test
    void getCustomHeaders() {

        Map<String, String> customHeadersMap = new HashMap<>();
        customHeadersMap.put("Header1", "Value1");
        customHeadersMap.put("Header2", "Value2");

        when(props.extractNestedProperties(CUSTOM_HEADERS)).thenReturn(props);
        when(props.getConfig()).thenReturn(customHeadersMap);

        Collection<Header> resultHeaders = RequestExecutorSupplier.getCustomHeaders(props);

        assertThat(resultHeaders)
                .isNotEmpty()
                .hasSize(2)
                .extracting(Header::toString)
                .contains("Header1: Value1", "Header2: Value2");
    }

    @Test
    void getCustomHeaders_return_empty_list_when_no_values() {

        Map<String, String> customHeadersMap = new HashMap<>();

        when(props.extractNestedProperties(CUSTOM_HEADERS)).thenReturn(props);
        when(props.getConfig()).thenReturn(customHeadersMap);

        Collection<Header> resultHeaders = RequestExecutorSupplier.getCustomHeaders(props);

        assertThat(resultHeaders)
                .isEmpty();
    }

}