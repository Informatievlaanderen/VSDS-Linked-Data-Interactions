package be.vlaanderen.informatievlaanderen.ldes.ldio.services;

import be.vlaanderen.informatievlaanderen.ldes.ldi.services.ComponentExecutor;
import be.vlaanderen.informatievlaanderen.ldes.ldi.types.LdiTransformer;
import be.vlaanderen.informatievlaanderen.ldes.ldi.types.LdiOutput;
import org.apache.jena.rdf.model.Model;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ComponentExecutorImpl implements ComponentExecutor {

	private final ExecutorService executorService;
	private final List<LdiTransformer> ldiTransformers;
	private final List<LdiOutput> ldiOutputs;

	public ComponentExecutorImpl(List<LdiTransformer> ldiTransformers, List<LdiOutput> ldiOutputs) {
		this.executorService = Executors.newSingleThreadExecutor();
		this.ldiTransformers = ldiTransformers;
		this.ldiOutputs = ldiOutputs;
	}

	@Override
	public void transformLinkedData(final Model linkedDataModel) {
		executorService.execute(() -> {
			Model transformedLinkedDataModel = linkedDataModel;
			for (LdiTransformer component: ldiTransformers) {
				transformedLinkedDataModel = component.execute(transformedLinkedDataModel);
			}

			Model finalTransformedLinkedDataModel = transformedLinkedDataModel;
			ldiOutputs.parallelStream().forEach(ldiOutput -> {
				ldiOutput.sendLinkedData(finalTransformedLinkedDataModel);
			});
		});
	}
}
