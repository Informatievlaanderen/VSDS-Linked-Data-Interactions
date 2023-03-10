package be.vlaanderen.informatievlaanderen.ldes.ldio;

import be.vlaanderen.informatievlaanderen.ldes.ldi.services.ComponentExecutor;
import be.vlaanderen.informatievlaanderen.ldes.ldi.types.LdiAdapter;
import be.vlaanderen.informatievlaanderen.ldes.ldi.types.LdiInput;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping
public class LdioHttpIn extends LdiInput {
	public LdioHttpIn(ComponentExecutor executor, LdiAdapter adapter) {
		super(executor, adapter);
	}

	@PostMapping("data")
	public void accept(@RequestBody String input, @RequestHeader("Content-Type") String contentType) {
		getAdapter().apply(LdiAdapter.Content.of(input, contentType))
				.forEach(getExecutor()::transformLinkedData);
	}
}
