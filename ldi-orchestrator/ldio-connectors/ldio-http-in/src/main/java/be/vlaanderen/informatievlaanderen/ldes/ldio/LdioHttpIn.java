package be.vlaanderen.informatievlaanderen.ldes.ldio;

import be.vlaanderen.informatievlaanderen.ldes.ldi.services.ComponentExecutor;
import be.vlaanderen.informatievlaanderen.ldes.ldi.types.LdiAdapter;
import be.vlaanderen.informatievlaanderen.ldes.ldi.types.LdiInput;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.ServerResponse;

import static org.springframework.web.servlet.function.RequestPredicates.GET;
import static org.springframework.web.servlet.function.RequestPredicates.POST;
import static org.springframework.web.servlet.function.RouterFunctions.route;
import static org.springframework.web.servlet.function.ServerResponse.ok;

@RequestMapping
public class LdioHttpIn extends LdiInput {
	private String endpoint;

	public LdioHttpIn(ComponentExecutor executor, LdiAdapter adapter, String endpoint) {
		super(executor, adapter);
		this.endpoint = endpoint;
	}

	@PostMapping("data")
	public void accept(@RequestBody String input, @RequestHeader("Content-Type") String contentType) {
		getAdapter().apply(LdiAdapter.Content.of(input, contentType))
				.forEach(getExecutor()::transformLinkedData);
	}

	@Bean
	RouterFunction<ServerResponse> getEmployeeByIdRoute() {
		return route(POST("/%s".formatted(endpoint)),
				req -> req.body(toMono(Employee.class))
						.doOnNext(employeeRepository()::updateEmployee)
						.then(ok().build()));
	}
}
