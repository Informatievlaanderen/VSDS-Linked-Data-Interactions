package be.vlaanderen.informatievlaanderen.ldes.ldi.client;

import be.vlaanderen.informatievlaanderen.ldes.ldi.config.ComponentProperties;
import be.vlaanderen.informatievlaanderen.ldes.ldi.config.LdioConfigurator;
import be.vlaanderen.informatievlaanderen.ldes.ldi.services.ComponentExecutor;
import be.vlaanderen.informatievlaanderen.ldes.ldi.types.LdiComponent;
import ldes.client.requestexecutor.domain.valueobjects.AuthStrategy;
import ldes.client.requestexecutor.domain.valueobjects.executorsupplier.RequestExecutorFactory;
import ldes.client.requestexecutor.executor.RequestExecutor;
import ldes.client.treenodefetcher.TreeNodeFetcher;
import ldes.client.treenodesupplier.LdesProvider;
import ldes.client.treenodesupplier.MemberSupplier;
import ldes.client.treenodesupplier.TreeNodeProcessor;
import ldes.client.treenodesupplier.domain.valueobject.Ldes;
import ldes.client.treenodesupplier.repository.inmemory.InMemoryMemberRepository;
import ldes.client.treenodesupplier.repository.inmemory.InMemoryTreeNodeRecordRepository;
import ldes.client.treenodesupplier.repository.sqlite.SqliteMemberRepository;
import ldes.client.treenodesupplier.repository.sqlite.SqliteTreeNodeRepository;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFLanguages;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import java.util.Optional;

import static ldes.client.requestexecutor.domain.valueobjects.AuthStrategy.NO_AUTH;

@Configuration
@EnableConfigurationProperties()
@ComponentScan("be.vlaanderen.informatievlaanderen.ldes")
public class LdioLdesClientAutoConfig {
	@Bean("be.vlaanderen.informatievlaanderen.ldes.ldi.client.LdioLdesClient")
	public LdioConfigurator ldioConfigurator(ComponentExecutor componentExecutor) {
		return new LdioLdesClientConfigurator(componentExecutor);
	}

	public static class LdioLdesClientConfigurator implements LdioConfigurator {

		public static final String CLIENT_ID = "clientId";
		public static final String CLIENT_SECRET = "clientSecret";
		public static final String TOKEN_ENDPOINT = "tokenEndpoint";
		public static final String OAUTH_SCOPE = "oAuthScope";
		public static final String AUTHENTICATION = "authentication";
		Logger logger = LoggerFactory.getLogger(LdioLdesClientConfigurator.class);
		public static final String URL = "url";
		public static final String SOURCE_FORMAT = "sourceFormat";
		public static final String STATE = "state";
		public static final String KEEP_STATE = "keepState";
		public static final String API_KEY = "apiKey";
		public static final String API_KEY_HEADER = "apiKeyHeader";
		private final ComponentExecutor componentExecutor;

		public LdioLdesClientConfigurator(ComponentExecutor componentExecutor) {
			this.componentExecutor = componentExecutor;
		}

		@Override
		public LdiComponent configure(ComponentProperties properties) {
			String targetUrl = properties.getProperty(URL);
			Lang sourceFormat = properties.getOptionalProperty(SOURCE_FORMAT)
					.map(RDFLanguages::nameToLang)
					.orElse(Lang.JSONLD);
			String state = properties.getOptionalProperty(STATE)
					.orElse("memory");
			boolean keepState = properties.getOptionalProperty(KEEP_STATE)
					.map(Boolean::valueOf)
					.orElse(false);
			RequestExecutor requestExecutor = getRequestExecutor(properties);
			logger.info("Identifying starting node of LDES: {}", targetUrl);
			Ldes ldes = new LdesProvider(requestExecutor).getLdes(targetUrl, sourceFormat);
			logger.info("Identified starting node of LDES: {}", targetUrl);
			TreeNodeProcessor treeNodeProcessor;
			if (state.equals("sqlite")) {
				treeNodeProcessor = new TreeNodeProcessor(ldes, new SqliteTreeNodeRepository(),
						new SqliteMemberRepository(),
						new TreeNodeFetcher(requestExecutor), keepState);
			} else {
				treeNodeProcessor = new TreeNodeProcessor(ldes, new InMemoryTreeNodeRecordRepository(),
						new InMemoryMemberRepository(),
						new TreeNodeFetcher(requestExecutor), keepState);
			}
			MemberSupplier memberSupplier = new MemberSupplier(treeNodeProcessor);
			LdesClientRunner ldesClientRunner = new LdesClientRunner(memberSupplier, componentExecutor);
			return new LdioLdesClient(componentExecutor, ldesClientRunner);
		}

		private RequestExecutor getRequestExecutor(ComponentProperties componentProperties) {
			Optional<AuthStrategy> authentication = AuthStrategy
					.from(componentProperties.getOptionalProperty(AUTHENTICATION).orElse(NO_AUTH.name()));
			if (authentication.isPresent()) {
				RequestExecutorFactory requestExecutorFactory = new RequestExecutorFactory();
				return switch (authentication.get()) {
					case NO_AUTH -> requestExecutorFactory.createNoAuthExecutor();
					case API_KEY ->
						requestExecutorFactory.createApiKeyExecutor(componentProperties.getProperty(API_KEY_HEADER),
								componentProperties.getProperty(API_KEY));
					case OAUTH2_CLIENT_CREDENTIALS ->
						requestExecutorFactory.createClientCredentialsExecutor(
								componentProperties.getProperty(CLIENT_ID),
								componentProperties.getProperty(CLIENT_SECRET),
								componentProperties.getProperty(TOKEN_ENDPOINT),
								componentProperties.getProperty(OAUTH_SCOPE));

				};
			}
			throw new UnsupportedOperationException(
					"Requested authentication not available: " + authentication);
		}
	}

}
