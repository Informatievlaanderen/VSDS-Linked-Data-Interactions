package be.vlaanderen.informatievlaanderen.ldes.ldio.config;

import be.vlaanderen.informatievlaanderen.ldes.ldi.HttpSparqlOut;
import be.vlaanderen.informatievlaanderen.ldes.ldi.SkolemisationTransformer;
import be.vlaanderen.informatievlaanderen.ldes.ldi.factory.DeleteFunctionBuilder;
import be.vlaanderen.informatievlaanderen.ldes.ldi.factory.InsertFunctionBuilder;
import be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.executor.RequestExecutor;
import be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.services.RequestExecutorFactory;
import be.vlaanderen.informatievlaanderen.ldes.ldi.skolemisation.EmptySkolemizer;
import be.vlaanderen.informatievlaanderen.ldes.ldi.skolemisation.Skolemizer;
import be.vlaanderen.informatievlaanderen.ldes.ldi.skolemisation.SkolemizerImpl;
import be.vlaanderen.informatievlaanderen.ldes.ldi.types.LdiOutput;
import be.vlaanderen.informatievlaanderen.ldes.ldi.valueobjects.DeleteFunction;
import be.vlaanderen.informatievlaanderen.ldes.ldi.valueobjects.InsertFunction;
import be.vlaanderen.informatievlaanderen.ldes.ldi.valueobjects.SparqlQuery;
import be.vlaanderen.informatievlaanderen.ldes.ldio.LdioHttpSparqlOut;
import be.vlaanderen.informatievlaanderen.ldes.ldio.pipeline.creation.LdioOutputConfigurator;
import be.vlaanderen.informatievlaanderen.ldes.ldio.pipeline.creation.valueobjects.ComponentProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static be.vlaanderen.informatievlaanderen.ldes.ldio.LdioHttpSparqlOut.NAME;

@Configuration
public class LdioHttpSparqlOutAutoConfig {

	@SuppressWarnings("java:S6830")
	@Bean(name = NAME)
	public LdioHttpSparqlOutConfigurator ldiHttpSparqlOutConfigurator() {
		return new LdioHttpSparqlOutConfigurator();
	}

	public static class LdioHttpSparqlOutConfigurator implements LdioOutputConfigurator {

		@Override
		public LdiOutput configure(ComponentProperties properties) {
			final LdioHttpSparqlOutProperties httpSparqlOutProperties = new LdioHttpSparqlOutProperties(properties);
			final DeleteFunction deleteFunction = createDeleteFunction(httpSparqlOutProperties);
			final InsertFunction insertFunction = httpSparqlOutProperties.getGraph()
					.map(InsertFunctionBuilder::withGraph)
					.orElseGet(InsertFunctionBuilder::create)
					.build();
			final Skolemizer skolemizer = httpSparqlOutProperties.getSkolemisationDomain()
					.map(SkolemisationTransformer::new)
					.map(skolemisationTransformer -> (Skolemizer) new SkolemizerImpl(skolemisationTransformer))
					.orElseGet(EmptySkolemizer::new);
			final RequestExecutor requestExecutor = new RequestExecutorFactory().createNoAuthExecutor();

			final SparqlQuery sparqlQuery = new SparqlQuery(insertFunction, deleteFunction);
			final HttpSparqlOut httpSparqlOut = new HttpSparqlOut(httpSparqlOutProperties.getEndpoint(), sparqlQuery, skolemizer, requestExecutor);

			return new LdioHttpSparqlOut(httpSparqlOut);
		}

		private static DeleteFunction createDeleteFunction(LdioHttpSparqlOutProperties properties) {
			if (!properties.isReplacementEnabled()) {
				return DeleteFunctionBuilder.disabled();
			}
			return properties.getReplacementDeleteFunction()
					.map(DeleteFunction::ofQuery)
					.orElseGet(() -> properties.getGraph()
							.map(DeleteFunctionBuilder::withGraph)
							.orElseGet(DeleteFunctionBuilder::create)
							.withDepth(properties.getReplacementDepth())
					);
		}
	}
}
