package be.vlaanderen.informatievlaanderen.ldes.ldio.config;

import be.vlaanderen.informatievlaanderen.ldes.ldi.extractor.EmptyPropertyExtractor;
import be.vlaanderen.informatievlaanderen.ldes.ldi.extractor.PropertyExtractor;
import be.vlaanderen.informatievlaanderen.ldes.ldi.extractor.PropertyPathExtractor;
import be.vlaanderen.informatievlaanderen.ldes.ldio.LdioVersionObjectCreator;
import be.vlaanderen.informatievlaanderen.ldes.ldio.configurator.LdioTransformerConfigurator;
import be.vlaanderen.informatievlaanderen.ldes.ldio.exception.ConfigPropertyMissingException;
import be.vlaanderen.informatievlaanderen.ldes.ldio.types.LdioTransformer;
import be.vlaanderen.informatievlaanderen.ldes.ldio.valueobjects.ComponentProperties;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

import static be.vlaanderen.informatievlaanderen.ldes.ldio.LdioVersionObjectCreator.NAME;

@Configuration
public class LdioVersionObjectCreatorAutoConfig {
	@Bean(NAME)
	public LdioTransformerConfigurator ldioConfigurator() {
		return new LdioVersionObjectCreatorTransformerConfigurator();
	}

	public static class LdioVersionObjectCreatorTransformerConfigurator implements LdioTransformerConfigurator {

		public static final String DATE_OBSERVED = "date-observed-property";
		public static final String MEMBER_TYPE = "member-type";
		public static final String DELIMITER = "delimiter";
		public static final String GENERATED_AT = "generatedAt-property";
		public static final String VERSION_OF = "versionOf-property";
		public static final String DEFAULT_PROV_GENERATED_AT_TIME = "http://www.w3.org/ns/prov#generatedAtTime";
		public static final String DEFAULT_VERSION_OF_KEY = "http://purl.org/dc/terms/isVersionOf";

		@Override
		public LdioTransformer configure(ComponentProperties properties) {
			Model initModel = ModelFactory.createDefaultModel();

			PropertyExtractor dateObservedPropertyExtractor = properties.getOptionalProperty(DATE_OBSERVED)
					.map(PropertyPathExtractor::from)
					.map(PropertyExtractor.class::cast)
					.orElseGet(EmptyPropertyExtractor::new);

			List<Resource> memberTypes = properties.getPropertyList(MEMBER_TYPE).stream()
					.map(initModel::createResource).toList();
			if (memberTypes.isEmpty()) {
				throw new ConfigPropertyMissingException(properties.getPipelineName(), properties.getComponentName(), MEMBER_TYPE);
			}

			String delimiter = properties.getOptionalProperty(DELIMITER).orElse("/");

			Property generatedAtProperty = properties.getOptionalProperty(GENERATED_AT)
					.map(initModel::createProperty)
					.orElseGet(() -> initModel.createProperty(DEFAULT_PROV_GENERATED_AT_TIME));

			Property versionOfProperty = properties.getOptionalProperty(VERSION_OF)
					.map(initModel::createProperty)
					.orElseGet(() -> initModel.createProperty(DEFAULT_VERSION_OF_KEY));

			return new LdioVersionObjectCreator(dateObservedPropertyExtractor, memberTypes, delimiter,
					generatedAtProperty,
					versionOfProperty);
		}
	}

}
