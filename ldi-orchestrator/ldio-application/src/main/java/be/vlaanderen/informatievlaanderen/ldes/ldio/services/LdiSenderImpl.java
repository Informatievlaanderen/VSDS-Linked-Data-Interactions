package be.vlaanderen.informatievlaanderen.ldes.ldio.services;

import be.vlaanderen.informatievlaanderen.ldes.ldi.services.LdiSender;
import be.vlaanderen.informatievlaanderen.ldes.ldi.types.LdiOutput;
import org.apache.jena.rdf.model.Model;
import org.springframework.stereotype.Component;

import java.util.ArrayDeque;
import java.util.List;
import java.util.Queue;

@Component
public class LdiSenderImpl implements LdiSender {
	private boolean disabled;
	private final List<LdiOutput> ldiOutputs;
	private final Queue<Model> queue = new ArrayDeque<>();

	public LdiSenderImpl(List<LdiOutput> ldiOutputs) {
		this.ldiOutputs = ldiOutputs;
	}

	@Override
	public void accept(Model model) {
		if (!disabled) {
			ldiOutputs.parallelStream().forEach(ldiOutput -> ldiOutput.accept(model));
		}
		else {
			queue.add(model);
		}
	}
}
