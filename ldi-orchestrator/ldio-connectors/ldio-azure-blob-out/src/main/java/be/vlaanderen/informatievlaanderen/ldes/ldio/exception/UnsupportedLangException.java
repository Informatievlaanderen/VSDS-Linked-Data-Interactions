package be.vlaanderen.informatievlaanderen.ldes.ldio.exception;

public class UnsupportedLangException extends RuntimeException {
	private final String lang;

	public UnsupportedLangException(String lang) {
		this.lang = lang;
	}

	@Override
	public String getMessage() {
		return "Conversion does not support lang: " + lang;
	}
}
