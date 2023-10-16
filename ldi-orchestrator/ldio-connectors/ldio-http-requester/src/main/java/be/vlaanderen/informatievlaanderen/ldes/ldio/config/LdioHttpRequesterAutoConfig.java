package be.vlaanderen.informatievlaanderen.ldes.ldio.config;

import be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.executor.RequestExecutorSupplier;
import be.vlaanderen.informatievlaanderen.ldes.ldi.types.LdiAdapter;
import be.vlaanderen.informatievlaanderen.ldes.ldi.types.LdiComponent;
import be.vlaanderen.informatievlaanderen.ldes.ldio.LdioHttpRequester;
import be.vlaanderen.informatievlaanderen.ldes.ldio.configurator.LdioConfigurator;
import be.vlaanderen.informatievlaanderen.ldes.ldio.requestexecutor.LdioRequestExecutorSupplier;
import be.vlaanderen.informatievlaanderen.ldes.ldio.valueobjects.ComponentProperties;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static be.vlaanderen.informatievlaanderen.ldes.ldio.config.LdioHttpRequesterProperties.HTTP_REQUESTER_ADAPTER;

@Configuration
public class LdioHttpRequesterAutoConfig {

    @Bean("be.vlaanderen.informatievlaanderen.ldes.ldio.LdioHttpRequester")
    public LdioConfigurator ldioConfigurator(ConfigurableApplicationContext configContext) {
        return new LdioConfigurator() {
            @Override
            public LdiComponent configure(ComponentProperties config) {
                final String adapterBeanName = config.getProperty(HTTP_REQUESTER_ADAPTER);
                final LdioConfigurator ldioConfigurator = (LdioConfigurator) configContext.getBean(adapterBeanName);
                final LdiAdapter adapter = (LdiAdapter) ldioConfigurator.configure(config);
                return new LdioHttpRequester(adapter, new LdioRequestExecutorSupplier().getRequestExecutor(config));
            }
        };
    }


}
