package be.vlaanderen.informatievlaanderen.ldes.ldi.functions;

import io.carml.engine.function.FnoFunction;
import io.carml.engine.function.FnoParam;

public class ReplaceFunctions {

	@FnoFunction(LDI.REPLACE_FUNCTION)
	public String replaceFunction(@FnoParam(LDI.CONTENT) String content,
			@FnoParam(LDI.TARGET) String target, @FnoParam(LDI.REPLACEMENT) String replacement) {
		return content.replace(target, replacement);
	}
}
