package be.vlaanderen.informatievlaanderen.ldes.ldio.config;

import be.vlaanderen.informatievlaanderen.ldes.ldi.extractor.EmptyPropertyExtractor;
import be.vlaanderen.informatievlaanderen.ldes.ldi.extractor.PropertyExtractor;
import be.vlaanderen.informatievlaanderen.ldes.ldi.extractor.PropertyPathExtractor;
import be.vlaanderen.informatievlaanderen.ldes.ldio.LdioVersionObjectCreator;
import be.vlaanderen.informatievlaanderen.ldes.ldio.configurator.LdioTransformerConfigurator;
import be.vlaanderen.informatievlaanderen.ldes.ldio.types.LdioTransformer;
import be.vlaanderen.informatievlaanderen.ldes.ldio.valueobjects.ComponentProperties;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Optional;

import static be.vlaanderen.informatievlaanderen.ldes.ldio.LdioVersionObjectCreator.NAME;

@Configuration
public class LdioVersionObjectCreatorAutoConfig {
	@Bean(NAME)
	public LdioTransformerConfigurator ldioConfigurator() {
		return new LdioVersionObjectCreatorTransformerConfigurator();
	}

	public static class LdioVersionObjectCreatorTransformerConfigurator implements LdioTransformerConfigurator {

		@Override
		public LdioTransformer configure(ComponentProperties properties) {
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
					.orElseGet(() -> initModel.createProperty("http://www.w3.org/ns/prov#generatedAtTime"));

			Property versionOfProperty = properties.getOptionalProperty("versionOf-property")
					.map(initModel::createProperty)
					.orElseGet(() -> initModel.createProperty("http://purl.org/dc/terms/isVersionOf"));

			return new LdioVersionObjectCreator(dateObservedPropertyExtractor, memberType, delimiter,
					generatedAtProperty,
					versionOfProperty);
		}
	}

}
