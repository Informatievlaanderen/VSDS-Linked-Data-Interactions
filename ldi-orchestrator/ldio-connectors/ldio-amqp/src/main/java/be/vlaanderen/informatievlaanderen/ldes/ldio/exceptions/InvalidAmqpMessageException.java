package be.vlaanderen.informatievlaanderen.ldes.ldio.exceptions;

import jakarta.jms.JMSException;

public class InvalidAmqpMessageException extends RuntimeException {
	public InvalidAmqpMessageException(JMSException e) {
		super(e);
	}
}
