package be.vlaanderen.informatievlaanderen.ldes.ldio.config;

import be.vlaanderen.informatievlaanderen.ldes.ldio.configurator.LdioTransformerConfigurator;
import be.vlaanderen.informatievlaanderen.ldes.ldio.types.LdioTransformer;
import be.vlaanderen.informatievlaanderen.ldes.ldio.valueobjects.ComponentProperties;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LdioVersionMaterialiserAutoConfig {
	@Bean("be.vlaanderen.informatievlaanderen.ldes.ldi.VersionMaterialiser")
	public LdioTransformerConfigurator ldioConfigurator() {
		return new LdioVersionMaterialiserTransformerConfigurator();
	}

	public static class LdioVersionMaterialiserTransformerConfigurator implements LdioTransformerConfigurator {

		@Override
		public LdioTransformer configure(ComponentProperties config) {
			Model initModel = ModelFactory.createDefaultModel();

			Property versionOfProperty = config.getOptionalProperty("versionOf-property")
					.map(initModel::createProperty)
					.orElse(initModel.createProperty("http://purl.org/dc/terms/isVersionOf"));
			boolean restrictToMembers = config.getOptionalBoolean("restrict-to-members").orElse(false);

			return new LdioVersionMaterialiser(versionOfProperty, restrictToMembers);
		}
	}
}
