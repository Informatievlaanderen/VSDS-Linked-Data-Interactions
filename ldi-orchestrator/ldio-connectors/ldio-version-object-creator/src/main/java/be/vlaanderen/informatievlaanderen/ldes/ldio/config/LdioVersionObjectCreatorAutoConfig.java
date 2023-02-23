package be.vlaanderen.informatievlaanderen.ldes.ldio.config;

import be.vlaanderen.informatievlaanderen.ldes.ldi.VersionObjectCreator;
import be.vlaanderen.informatievlaanderen.ldes.ldi.config.ComponentProperties;
import be.vlaanderen.informatievlaanderen.ldes.ldi.config.LdioConfigurator;
import be.vlaanderen.informatievlaanderen.ldes.ldi.types.LdiComponent;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import java.util.Optional;

@Configuration
@EnableConfigurationProperties()
@ComponentScan("be.vlaanderen.informatievlaanderen.ldes")
public class LdioVersionObjectCreatorAutoConfig {
	@Bean("be.vlaanderen.informatievlaanderen.ldes.ldi.VersionObjectCreator")
	public LdioConfigurator ldioConfigurator() {
		return new LdioVersionObjectCreatorConfigurator();
	}

	public static class LdioVersionObjectCreatorConfigurator implements LdioConfigurator {

		@Override
		public LdiComponent configure(ComponentProperties properties) {
			Model initModel = ModelFactory.createDefaultModel();

			Property dateObserved = properties.getOptionalProperty("date-observed-property")
					.map(initModel::createProperty).orElse(null);

			Resource memberType = Optional.of(properties.getProperty("member-type"))
					.map(initModel::createResource).orElse(null);

			String delimiter = properties.getOptionalProperty("delimiter").orElse("/");

			Property generatedAtProperty = properties.getOptionalProperty("generatedAt-property")
					.map(initModel::createProperty)
					.orElse(null);

			Property versionOfProperty = properties.getOptionalProperty("versionOf-property")
					.map(initModel::createProperty)
					.orElse(null);

			return new VersionObjectCreator(dateObserved, memberType, delimiter, generatedAtProperty,
					versionOfProperty);
		}
	}

}
