package be.vlaanderen.informatievlaanderen.ldes.ldio.services;

import be.vlaanderen.informatievlaanderen.ldes.ldi.services.ComponentExecutor;
import be.vlaanderen.informatievlaanderen.ldes.ldi.types.LdiTransformer;
import org.apache.jena.rdf.model.Model;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ComponentExecutorImpl implements ComponentExecutor {

	private final ExecutorService executorService;
	private final List<LdiTransformer> ldiTransformers;
	private final LdiSender ldiSender;

	public ComponentExecutorImpl(List<LdiTransformer> ldiTransformers, LdiSender ldiSender) {
		this.executorService = Executors.newSingleThreadExecutor();
		this.ldiTransformers = ldiTransformers;
		this.ldiSender = ldiSender;
	}

	@Override
	public void transformLinkedData(final Model linkedDataModel) {
		executorService.execute(() -> {
			List<Model> transformedLinkedDataModels = List.of(linkedDataModel);
			for (LdiTransformer component : ldiTransformers) {
				transformedLinkedDataModels = transformedLinkedDataModels.stream()
						.flatMap(model -> component.apply(model).stream()).toList();
			}

			transformedLinkedDataModels.forEach(ldiSender);
		});
	}

	public List<LdiTransformer> getLdiTransformers() {
		return ldiTransformers;
	}

	public LdiSender getLdiSender() {
		return ldiSender;
	}
}
