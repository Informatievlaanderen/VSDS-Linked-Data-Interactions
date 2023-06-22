package be.vlaanderen.informatievlaanderen.ldes.ldi.rmlFunctions;

import io.carml.engine.function.FnoFunction;
import io.carml.engine.function.FnoParam;

public class ReplaceFunctions {

	@FnoFunction(LDI.replaceFunction)
	public String replaceFunction(@FnoParam(LDI.content) String content,
			@FnoParam(LDI.target) String target, @FnoParam(LDI.replacement) String replacement) {
		return content.replace(target, replacement);
	}
}
