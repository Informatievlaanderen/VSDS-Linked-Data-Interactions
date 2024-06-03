package be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.executor.edc.services;

import be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.valueobjects.Response;

public interface TransferService {

	/**
	 * Sets the transfer string for the request body and sends the request
	 *
	 * @param transfer string representation of the transfer
	 * @return the response from the transfer request
	 */
	Response startTransfer(String transfer);

	void refreshTransfer();

}
