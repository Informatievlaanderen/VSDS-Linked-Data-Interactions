package be.vlaanderen.informatievlaanderen.ldes.ldio.config;

import be.vlaanderen.informatievlaanderen.ldes.ldi.types.LdiComponent;
import be.vlaanderen.informatievlaanderen.ldes.ldio.LdioRepositoryMaterialiser;
import be.vlaanderen.informatievlaanderen.ldes.ldio.configurator.LdioConfigurator;
import be.vlaanderen.informatievlaanderen.ldes.ldio.valueobjects.ComponentProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LioRepositoryMaterialiserAutoConfig {

	@Bean("be.vlaanderen.informatievlaanderen.ldes.ldi.RepositoryMaterialiser")
	public LdioConfigurator ldiRepoMaterialiserConfigurator() {
		return new LdiRepoMaterialiserConfigurator();
	}

	public static class LdiRepoMaterialiserConfigurator implements LdioConfigurator {
		@Override
		public LdiComponent configure(ComponentProperties config) {
			return new LdioRepositoryMaterialiser(config);
		}
	}
}
