package be.vlaanderen.informatievlaanderen.ldes.client.cli.arguments;

import be.vlaanderen.informatievlaanderen.ldes.client.cli.model.EndpointBehaviour;
import org.apache.jena.riot.Lang;
import org.junit.jupiter.api.Test;

import com.beust.jcommander.ParameterException;

import static org.junit.jupiter.api.Assertions.*;

class ArgumentParserTest {

	ArgumentParser argumentParser = new ArgumentParser();

	@Test
	void when_argsAreProvided_ArgumentParserReturnsCommandLineArguments() {
		CommandlineArguments commandlineArguments = argumentParser.parseArguments(
				"--url", "url",
				"--input-format", "n-quads",
				"--output-format", "n-triples",
				"--expiration-interval", "10",
				"--polling-interval", "20", "--endpoint-behaviour",
				"waiting", "--help");
		assertEquals("url", commandlineArguments.getUrl());
		assertEquals(Lang.NQUADS, commandlineArguments.getSourceFormat());
		assertEquals(Lang.NTRIPLES, commandlineArguments.getOutputFormat());
		assertEquals(10, commandlineArguments.getExpirationInterval());
		assertEquals(20, commandlineArguments.getPollingInterval());
		assertEquals(EndpointBehaviour.WAITING, commandlineArguments.getEndpointBehaviour());
		assertTrue(commandlineArguments.isHelp());
	}

	@Test
	void when_nullArgsAreProvided_ArgumentParserReturnsDefaultCommandLineArguments() {
		CommandlineArguments commandlineArguments = argumentParser.parseArguments(
				"--url", "url");
		assertEquals("url", commandlineArguments.getUrl());
		assertEquals(Lang.JSONLD, commandlineArguments.getSourceFormat());
		assertEquals(Lang.NQUADS, commandlineArguments.getOutputFormat());
		assertEquals(604800, commandlineArguments.getExpirationInterval());
		assertEquals(60, commandlineArguments.getPollingInterval());
		assertEquals(EndpointBehaviour.STOPPING, commandlineArguments.getEndpointBehaviour());
		assertFalse(commandlineArguments.isHelp());
	}

	@Test
	void when_unknownArgIsProvided_ArgumentParserThrowsParameterException() {
		ParameterException parameterException = assertThrows(ParameterException.class,
				() -> argumentParser.parseArguments(
						"--unkownargs"));
		assertEquals("Was passed main parameter '--unkownargs' but no main parameter was defined in your arg class",
				parameterException.getMessage());
	}

}
