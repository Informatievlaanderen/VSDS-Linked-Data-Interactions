package be.vlaanderen.informatievlaanderen.ldes.ldio.config;

import be.vlaanderen.informatievlaanderen.ldes.ldio.LdiConsoleOut;
import be.vlaanderen.informatievlaanderen.ldes.ldi.config.LdioConfigurator;
import be.vlaanderen.informatievlaanderen.ldes.ldi.types.LdiComponent;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
@EnableConfigurationProperties()
@ComponentScan("be.vlaanderen.informatievlaanderen.ldes.ldio")
public class LdioConsoleOutAutoConfigure {
	@Bean("be.vlaanderen.informatievlaanderen.ldes.ldio.LdioConsoleOut")
	public LdioConsoleOutConfigurator ldiHttpOutConfigurator() {
		return new LdioConsoleOutConfigurator();
	}

	public static class LdioConsoleOutConfigurator implements LdioConfigurator {
		@Override
		public LdiComponent configure(Map<String, String> properties) {
			return new LdiConsoleOut(properties);
		}
	}
}