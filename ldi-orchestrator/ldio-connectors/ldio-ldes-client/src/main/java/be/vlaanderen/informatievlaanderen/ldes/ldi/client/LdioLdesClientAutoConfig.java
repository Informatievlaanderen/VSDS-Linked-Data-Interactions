package be.vlaanderen.informatievlaanderen.ldes.ldi.client;

import be.vlaanderen.informatievlaanderen.ldes.ldi.config.ComponentProperties;
import be.vlaanderen.informatievlaanderen.ldes.ldi.config.LdioConfigurator;
import be.vlaanderen.informatievlaanderen.ldes.ldi.services.ComponentExecutor;
import be.vlaanderen.informatievlaanderen.ldes.ldi.types.LdiComponent;
import ldes.client.requestexecutor.domain.valueobjects.ApiKeyConfig;
import ldes.client.requestexecutor.domain.valueobjects.DefaultConfig;
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

@Configuration
@EnableConfigurationProperties()
@ComponentScan("be.vlaanderen.informatievlaanderen.ldes")
public class LdioLdesClientAutoConfig {
    @Bean("be.vlaanderen.informatievlaanderen.ldes.ldi.client.LdioLdesClient")
    public LdioConfigurator ldioConfigurator(ComponentExecutor componentExecutor) {
        return new LdioLdesClientConfigurator(componentExecutor);
    }

    public static class LdioLdesClientConfigurator implements LdioConfigurator {

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
            Optional<String> apiKey = properties.getOptionalProperty(API_KEY);
            RequestExecutor requestExecutor = new DefaultConfig().createRequestExecutor();
            if (apiKey.isPresent()) {
                requestExecutor = new ApiKeyConfig(properties.getOptionalProperty(API_KEY_HEADER).orElse("X-API-KEY"),
                        apiKey.get()).createRequestExecutor();
            }
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
    }

}
