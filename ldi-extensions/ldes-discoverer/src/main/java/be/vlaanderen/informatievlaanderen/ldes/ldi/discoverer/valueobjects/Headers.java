package be.vlaanderen.informatievlaanderen.ldes.ldi.discoverer.valueobjects;

import org.apache.http.Header;
import org.apache.http.message.BasicHeader;
import org.springframework.boot.ApplicationArguments;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Headers {
	private static final String HEADER = "header";
	private static final Pattern headerPattern = Pattern.compile("([^:]+):\\s*(\\S.*?)");

	private final Arguments arguments;

	public Headers(ApplicationArguments arguments) {
		this.arguments = new Arguments(arguments);
	}

	public List<Header> getHeaders() {
		return arguments.getArgumentValues(HEADER).stream()
				.map(headerPattern::matcher)
				.filter(Matcher::matches)
				.map(matcher -> new BasicHeader(matcher.group(1), matcher.group(2)))
				.collect(Collectors.toUnmodifiableList());
	}
}
