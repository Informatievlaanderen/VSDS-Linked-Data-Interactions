package be.vlaanderen.informatievlaanderen.ldes.ldio.pipeline.creation.model;

import be.vlaanderen.informatievlaanderen.ldes.ldi.types.LdiOutput;
import be.vlaanderen.informatievlaanderen.ldes.ldio.pipeline.creation.LdioTransformer;
import io.micrometer.core.instrument.Metrics;
import org.apache.jena.rdf.model.Model;

import java.util.List;

import static be.vlaanderen.informatievlaanderen.ldes.ldio.pipeline.PipelineConfig.PIPELINE_NAME;

/**
 * Important implementation of the LdioTransformer which must be added at the end of every transformer chains of
 * a pipeline to make sure the output of that chain can be sent to the provided outputs of that pipeline
 */
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
		ldiOutputs.parallelStream().forEach(ldiOutput -> ldiOutput.accept(model));
		Metrics.counter(LDIO_DATA_OUT, PIPELINE_NAME, pipelineName).increment();
	}
}
