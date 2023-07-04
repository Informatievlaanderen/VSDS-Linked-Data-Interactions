package be.vlaanderen.informatievlaanderen.ldes.ldio.exception;

public class MemberConversionException extends RuntimeException {
	private final String modelString;

	public MemberConversionException(String modelString, Throwable ex) {
		super(ex);
		this.modelString = modelString;
	}

	@Override
	public String getMessage() {
		return "Could not convert Model:\n" + modelString;
	}
}
