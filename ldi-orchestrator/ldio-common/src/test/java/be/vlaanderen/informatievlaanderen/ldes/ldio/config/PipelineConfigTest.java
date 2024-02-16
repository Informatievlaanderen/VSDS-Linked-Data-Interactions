package be.vlaanderen.informatievlaanderen.ldes.ldio.config;

import be.vlaanderen.informatievlaanderen.ldes.ldio.valueobjects.ComponentDefinition;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class PipelineConfigTest {

    private static final String PIPELINE_NAME = "my-pipeline";

    private PipelineConfig pipelineConfig;

    @BeforeEach
    void setUp() {
        pipelineConfig = new PipelineConfig();
    }

    @Test
    void testGetOutputs() {
        List<ComponentDefinition> componentDefinitions = getComponentDefinitions();
        pipelineConfig.setName(PIPELINE_NAME);
        pipelineConfig.setOutputs(componentDefinitions);

        List<ComponentDefinition> result = pipelineConfig.getOutputs();

        assertThat(result).hasSize(1);
        ComponentDefinition output = result.get(0);
        assertThat(output.getConfig().getProperty(PipelineConfig.PIPELINE_NAME)).isEqualTo(PIPELINE_NAME);
        assertThat(output.getConfig().getProperty("key1")).isEqualTo("value1");
    }

    private static List<ComponentDefinition> getComponentDefinitions() {
        List<ComponentDefinition> componentDefinitions = new ArrayList<>();
        Map<String, String> initialConfig = new HashMap<>();
        initialConfig.put("key1", "value1");
        ComponentDefinition componentDefinition = new ComponentDefinition("componentDefinition", initialConfig);
        componentDefinitions.add(componentDefinition);
        return componentDefinitions;
    }

}