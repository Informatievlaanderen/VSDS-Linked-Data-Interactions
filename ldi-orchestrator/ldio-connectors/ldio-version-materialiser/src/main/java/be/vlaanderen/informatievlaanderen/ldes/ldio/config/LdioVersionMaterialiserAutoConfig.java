package be.vlaanderen.informatievlaanderen.ldes.ldio.config;

import be.vlaanderen.informatievlaanderen.ldes.ldi.VersionMaterialiser;
import be.vlaanderen.informatievlaanderen.ldes.ldi.config.ComponentProperties;
import be.vlaanderen.informatievlaanderen.ldes.ldi.config.LdioConfigurator;
import be.vlaanderen.informatievlaanderen.ldes.ldi.types.LdiComponent;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties()
@ComponentScan("be.vlaanderen.informatievlaanderen.ldes")
public class LdioVersionMaterialiserAutoConfig {
	@Bean("be.vlaanderen.informatievlaanderen.ldes.ldi.VersionMaterialiser")
	public LdioConfigurator ldioConfigurator() {
		return new LdioVersionMaterialiserConfigurator();
	}

	public static class LdioVersionMaterialiserConfigurator implements LdioConfigurator {

		@Override
		public LdiComponent configure(ComponentProperties config) {
			Model initModel = ModelFactory.createDefaultModel();

			Property versionOfProperty = config.getOptionalProperty("versionOf-property")
					.map(initModel::createProperty)
					.orElse(initModel.createProperty("http://purl.org/dc/terms/isVersionOf"));
			boolean restrictToMembers = config.getOptionalBoolean("restrict-to-members").orElse(false);
			return new VersionMaterialiser(versionOfProperty, restrictToMembers);
		}
	}
}
