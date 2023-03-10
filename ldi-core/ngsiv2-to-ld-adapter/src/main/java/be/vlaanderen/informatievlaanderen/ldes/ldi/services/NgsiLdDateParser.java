package be.vlaanderen.informatievlaanderen.ldes.ldi.services;

public class NgsiLdDateParser {

	private NgsiLdDateParser() {
	}

	public static String normaliseDate(String date) {
		if (!date.endsWith("Z")) {
			return date + "Z";
		}

		return date;
	}
}
