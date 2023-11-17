package be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.executor.edc.services;

import be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.valueobjects.Response;

public interface TransferService {

	Response startTransfer(String transfer);

	void refreshTransfer();

}
