package be.vlaanderen.informatievlaanderen.ldes.ldio.config;

import be.vlaanderen.informatievlaanderen.ldes.ldi.types.LdiComponent;
import be.vlaanderen.informatievlaanderen.ldes.ldio.LdioRepositoryMaterialiser;
import be.vlaanderen.informatievlaanderen.ldes.ldio.configurator.LdioOutputConfigurator;
import be.vlaanderen.informatievlaanderen.ldes.ldio.valueobjects.ComponentProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LioRepositoryMaterialiserAutoConfig {

	@Bean("be.vlaanderen.informatievlaanderen.ldes.ldi.RepositoryMaterialiser")
	public LdioOutputConfigurator ldiRepoMaterialiserConfigurator() {
		return new LdiRepoMaterialiserProcessorConfigurator();
	}

	public static class LdiRepoMaterialiserProcessorConfigurator implements LdioOutputConfigurator {
		@Override
		public LdiComponent configure(ComponentProperties config) {
			return new LdioRepositoryMaterialiser(config);
		}
	}
}
