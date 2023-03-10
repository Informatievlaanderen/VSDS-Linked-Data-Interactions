package be.vlaanderen.informatievlaanderen.ldes.ldi.client;

import be.vlaanderen.informatievlaanderen.ldes.ldi.services.ComponentExecutor;
import ldes.client.treenodesupplier.MemberSupplier;

public class LdesClientRunner implements Runnable {
	private final MemberSupplier memberSupplier;
	private final ComponentExecutor componentExecutor;

	private boolean threadrunning = true;

	public LdesClientRunner(MemberSupplier memberSupplier, ComponentExecutor componentExecutor) {
		this.memberSupplier = memberSupplier;
		this.componentExecutor = componentExecutor;
	}

	@Override
	public void run() {
		while (threadrunning) {
			componentExecutor.transformLinkedData(memberSupplier.get().getModel());
		}
	}

	public void stopThread() {
		threadrunning = false;
	}
}
