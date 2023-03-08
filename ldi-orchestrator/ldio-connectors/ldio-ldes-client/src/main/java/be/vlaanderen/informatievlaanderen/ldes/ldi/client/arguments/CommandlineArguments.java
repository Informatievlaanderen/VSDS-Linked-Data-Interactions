package be.vlaanderen.informatievlaanderen.ldes.ldi.client.arguments;

import be.vlaanderen.informatievlaanderen.ldes.ldi.client.model.EndpointBehaviour;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFLanguages;

import com.beust.jcommander.Parameter;

import static be.vlaanderen.informatievlaanderen.ldes.client.LdesClientDefaults.DEFAULT_DATA_DESTINATION_FORMAT;
import static be.vlaanderen.informatievlaanderen.ldes.client.LdesClientDefaults.DEFAULT_DATA_SOURCE_FORMAT;
import static be.vlaanderen.informatievlaanderen.ldes.client.LdesClientDefaults.DEFAULT_FRAGMENT_EXPIRATION_INTERVAL;
import static be.vlaanderen.informatievlaanderen.ldes.client.LdesClientDefaults.DEFAULT_POLLING_INTERVAL;

@SuppressWarnings({ "FieldMayBeFinal", "FieldCanBeLocal" })
public class CommandlineArguments {

	@Parameter(names = "--url", description = "The base fragment url of the LDES", required = true, order = 0)
	private String url;
	@Parameter(names = { "--input-format",
			"-if" }, description = "Input format of the LDES (n-quads, json-ld, ...)", order = 1)
	private String sourceFormat = DEFAULT_DATA_SOURCE_FORMAT.getHeaderString();
	@Parameter(names = { "--output-format",
			"-of" }, description = "Output format of the members (n-quads, json-ld, ...)", order = 2)
	private String outputFormat = DEFAULT_DATA_DESTINATION_FORMAT.getHeaderString();
	@Parameter(names = { "--expiration-interval", "-ei" }, description = "Expiration interval", order = 3)
	private Long expirationInterval = DEFAULT_FRAGMENT_EXPIRATION_INTERVAL;
	@Parameter(names = { "--polling-interval", "-pi" }, description = "Polling interval", order = 4)
	private Long pollingInterval = DEFAULT_POLLING_INTERVAL;
	@Parameter(names = { "--endpoint-behaviour",
			"-eb" }, description = "The desired behaviour the Client should follow when the endpoint is not available", order = 5)
	private EndpointBehaviour endpointBehaviour = EndpointBehaviour.STOPPING;

	@Parameter(names = "--help", description = "Enabling displays usage of cli", help = true, order = 6)
	private boolean help;

	public String getUrl() {
		return url;
	}

	public Lang getSourceFormat() {
		return RDFLanguages.nameToLang(sourceFormat);
	}

	public Lang getOutputFormat() {
		return RDFLanguages.nameToLang(outputFormat);
	}

	public Long getExpirationInterval() {
		return expirationInterval;
	}

	public Long getPollingInterval() {
		return pollingInterval;
	}

	public boolean isHelp() {
		return help;
	}

	public EndpointBehaviour getEndpointBehaviour() {
		return endpointBehaviour;
	}
}
