package be.vlaanderen.informatievlaanderen.ldes.ldi.exceptions;

import org.apache.jena.rdf.model.Statement;

public class ObjectIdentifierException extends RuntimeException {

	private final String memberStatementSubject;
	private final String memberStatementPredicate;

	public ObjectIdentifierException(Statement memberStatement) {
		super();
		this.memberStatementSubject = memberStatement.getSubject().toString();
		this.memberStatementPredicate = memberStatement.getPredicate().toString();
	}

	@Override
	public String getMessage() {
		return "Statement <subject: " + memberStatementSubject +
				" predicate: " + memberStatementPredicate
				+ "> should have object identifier as object.";
	}

}
