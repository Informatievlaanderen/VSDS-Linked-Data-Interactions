package be.vlaanderen.informatievlaanderen.ldes.ldio.config;

import be.vlaanderen.informatievlaanderen.ldes.ldi.VersionObjectCreator;
import be.vlaanderen.informatievlaanderen.ldes.ldi.extractor.EmptyPropertyExtractor;
import be.vlaanderen.informatievlaanderen.ldes.ldi.extractor.PropertyExtractor;
import be.vlaanderen.informatievlaanderen.ldes.ldi.extractor.PropertyPathExtractor;
import be.vlaanderen.informatievlaanderen.ldes.ldi.types.LdiComponent;
import be.vlaanderen.informatievlaanderen.ldes.ldio.configurator.LdioConfigurator;
import be.vlaanderen.informatievlaanderen.ldes.ldio.valueobjects.ComponentProperties;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Optional;

@Configuration
public class LdioVersionObjectCreatorAutoConfig {
	@Bean("be.vlaanderen.informatievlaanderen.ldes.ldi.VersionObjectCreator")
	public LdioConfigurator ldioConfigurator() {
		return new LdioVersionObjectCreatorConfigurator();
	}

	public static class LdioVersionObjectCreatorConfigurator implements LdioConfigurator {

		@Override
		public LdiComponent configure(ComponentProperties properties) {
			Model initModel = ModelFactory.createDefaultModel();

			PropertyExtractor dateObservedPropertyExtractor = properties.getOptionalProperty("date-observed-property")
					.map(PropertyPathExtractor::from)
					.map(PropertyExtractor.class::cast)
					.orElseGet(EmptyPropertyExtractor::new);

			Resource memberType = Optional.of(properties.getProperty("member-type"))
					.map(initModel::createResource).orElse(null);

			String delimiter = properties.getOptionalProperty("delimiter").orElse("/");

			Property generatedAtProperty = properties.getOptionalProperty("generatedAt-property")
					.map(initModel::createProperty)
					.orElse(null);

			Property versionOfProperty = properties.getOptionalProperty("versionOf-property")
					.map(initModel::createProperty)
					.orElse(null);

			return new VersionObjectCreator(dateObservedPropertyExtractor, memberType, delimiter, generatedAtProperty,
					versionOfProperty);
		}
	}

}
