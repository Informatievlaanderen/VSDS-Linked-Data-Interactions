package be.vlaanderen.informatievlaanderen.ldes.client.cli;

import be.vlaanderen.informatievlaanderen.ldes.client.cli.arguments.ArgumentParser;
import be.vlaanderen.informatievlaanderen.ldes.client.cli.arguments.CommandlineArguments;
import be.vlaanderen.informatievlaanderen.ldes.client.cli.services.LdesClientCli;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.beust.jcommander.JCommander;

@SpringBootApplication
public class Application implements CommandLineRunner {

	private final ArgumentParser argumentParser;
	private final LdesClientCli ldesClientCli;

	public Application(ArgumentParser argumentParser, LdesClientCli ldesClientCli) {
		this.argumentParser = argumentParser;
		this.ldesClientCli = ldesClientCli;
	}

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

	@Override
	public void run(String... args) {
		CommandlineArguments arguments = argumentParser.parseArguments(args);
		if (arguments.isHelp()) {
			printOutUsage(arguments);
		} else {
			ldesClientCli.start(arguments.getUrl(), arguments.getSourceFormat(), arguments.getOutputFormat(),
					arguments.getExpirationInterval(), arguments.getPollingInterval(),
					arguments.getEndpointBehaviour());
		}
	}

	private void printOutUsage(CommandlineArguments arguments) {
		JCommander.newBuilder()
				.addObject(arguments)
				.build().usage();
	}
}
