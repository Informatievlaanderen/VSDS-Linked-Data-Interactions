package be.vlaanderen.informatievlaanderen.ldes.ldio.config;

import be.vlaanderen.informatievlaanderen.ldes.ldio.configurator.LdioConfigurator;
import be.vlaanderen.informatievlaanderen.ldes.ldio.exception.ConfigPropertyMissingException;
import be.vlaanderen.informatievlaanderen.ldes.ldio.valueobjects.ComponentProperties;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.provider.ArgumentsSource;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import static be.vlaanderen.informatievlaanderen.ldes.ldio.config.LdioVersionObjectCreatorAutoConfig.LdioVersionObjectCreatorTransformerConfigurator.MEMBER_TYPE;
import static org.junit.jupiter.api.Assertions.assertThrows;

class LdioVersionObjectCreatorAutoConfigTest {
    private final LdioConfigurator configurator = new LdioVersionObjectCreatorAutoConfig().ldioConfigurator();
    @ParameterizedTest
    @ArgumentsSource(configProvider.class)
    void when_NoMemberTypes_Then_exceptionIsThrown(Map<String, String> config) {
        ComponentProperties componentProperties = new ComponentProperties("pipelineName", "cName", config);

        assertThrows(ConfigPropertyMissingException.class,() -> configurator.configure(componentProperties)) ;
    }

    static class configProvider implements ArgumentsProvider {
        @Override
        public Stream<Arguments> provideArguments(ExtensionContext extensionContext) {
            Map<String, String> empty = new HashMap<>();
            empty.put(MEMBER_TYPE, "");
            return Stream.of(
                    Arguments.of(new HashMap<String, String>()),
                    Arguments.of(empty)
            );
        }
    }
}