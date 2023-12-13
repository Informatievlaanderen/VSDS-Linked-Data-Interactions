package be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.executor;

import be.vlaanderen.informatievlaanderen.ldes.ldio.valueobjects.ComponentProperties;
import org.apache.http.Header;
import org.apache.http.message.BasicHeader;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface RequestExecutorSupplier {

	String CUSTOM_HEADERS = "headers.custom";

	RequestExecutor createRequestExecutor(Collection<Header> customHeaders);

	static Collection<Header> getCustomHeaders(ComponentProperties props) {
		Optional<Map<String, String>> customHeaders = Optional.ofNullable(props.extractNestedProperties(CUSTOM_HEADERS).getConfig());
		return customHeaders.map(stringStringMap -> stringStringMap.entrySet()
				.stream()
				.map(entry -> (Header) new BasicHeader(entry.getKey(), entry.getValue()))
				.toList()).orElse(List.of());
	}
}
