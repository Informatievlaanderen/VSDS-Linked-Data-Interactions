package be.vlaanderen.informatievlaanderen.ldes.ldio.exception;

import static be.vlaanderen.informatievlaanderen.ldes.ldio.util.MemberIdExtractor.TREE_MEMBER;

public class MemberIdNotFoundException extends RuntimeException {

	private final String modelString;

	public MemberIdNotFoundException(String modelString) {
		super();
		this.modelString = modelString;
	}

	@Override
	public String getMessage() {
		return "Could not extract " + TREE_MEMBER + " statement from " + modelString;
	}
}
