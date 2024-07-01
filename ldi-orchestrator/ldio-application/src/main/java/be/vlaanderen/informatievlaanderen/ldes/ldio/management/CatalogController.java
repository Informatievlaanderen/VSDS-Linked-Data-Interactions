package be.vlaanderen.informatievlaanderen.ldes.ldio.management;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;

@RestController
@RequestMapping(path = "/admin/api/v1/catalog")
public class CatalogController implements OpenApiCatalogController {
	private final CatalogService service;

	public CatalogController(CatalogService service) {
		this.service = service;
	}

	@Override
	@GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
	public LdioCatalog catalog() {
		return new LdioCatalog(service.inputs(), service.adapters(), service.transformers(), service.outputs());
	}

	public record LdioCatalog(Set<String> inputs, Set<String> adapters, Set<String> transformers, Set<String> outputs) {
	}
}
