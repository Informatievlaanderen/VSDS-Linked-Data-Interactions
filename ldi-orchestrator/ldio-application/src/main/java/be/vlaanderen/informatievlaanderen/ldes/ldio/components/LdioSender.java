package be.vlaanderen.informatievlaanderen.ldes.ldio.components;

import be.vlaanderen.informatievlaanderen.ldes.ldi.types.LdiOutput;
import be.vlaanderen.informatievlaanderen.ldes.ldio.types.LdioTransformer;
import io.micrometer.core.instrument.Metrics;
import org.apache.jena.rdf.model.Model;

import java.util.List;

import static be.vlaanderen.informatievlaanderen.ldes.ldio.config.PipelineConfig.PIPELINE_NAME;

public class LdioSender extends LdioTransformer {
	private final List<LdiOutput> ldiOutputs;
	private final String pipelineName;
	private static final String LDIO_DATA_OUT = "ldio_data_out";

	public LdioSender(String pipelineName,
	                  List<LdiOutput> ldiOutputs) {
		this.ldiOutputs = ldiOutputs;
		this.pipelineName = pipelineName;
		Metrics.counter(LDIO_DATA_OUT, PIPELINE_NAME, pipelineName).increment(0);
	}

	@SuppressWarnings({"java:S131", "java:S1301"})

	@Override
	public void apply(Model model) {
		Metrics.counter(LDIO_DATA_OUT, PIPELINE_NAME, pipelineName).increment();
		ldiOutputs.parallelStream().forEach(ldiOutput -> ldiOutput.accept(model));
	}
}
