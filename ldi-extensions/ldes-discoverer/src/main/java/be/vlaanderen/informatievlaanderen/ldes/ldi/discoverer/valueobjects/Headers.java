package be.vlaanderen.informatievlaanderen.ldes.ldi.discoverer.valueobjects;

import org.apache.http.Header;
import org.apache.http.message.BasicHeader;
import org.springframework.boot.ApplicationArguments;

import java.util.List;
import java.util.stream.Collectors;

public class Headers {
	private static final String HEADER = "header";

	private final Arguments arguments;

	public Headers(ApplicationArguments arguments) {
		this.arguments = new Arguments(arguments);
	}

	public List<Header> getHeaders() {
		return arguments.getArgumentValues(HEADER).stream()
				.map(headerString -> headerString.split(": "))
				.map(headerArray -> new BasicHeader(headerArray[0], headerArray[1]) )
				.collect(Collectors.toUnmodifiableList());
	}
}
