package be.vlaanderen.informatievlaanderen.ldes.ldi.services;

import be.vlaanderen.informatievlaanderen.ldes.ldi.domain.valueobjects.LdesProperties;
import ldes.client.requestexecutor.domain.valueobjects.Request;
import ldes.client.requestexecutor.domain.valueobjects.Response;
import ldes.client.requestexecutor.executor.RequestExecutor;
import ldes.client.treenodefetcher.domain.valueobjects.TreeNodeRequest;
import ldes.client.treenodesupplier.domain.valueobject.Ldes;
import org.apache.jena.rdf.model.*;
import org.apache.jena.riot.RDFParser;

import java.util.Optional;

import static org.apache.jena.rdf.model.ResourceFactory.createProperty;

public class LdesPropertiesExtractor {

	public static final String LDES = "https://w3id.org/ldes#";
	public static final Property LDES_VERSION_OF = createProperty(LDES, "versionOfPath");
	public static final Property LDES_TIMESTAMP_PATH = createProperty(LDES, "timestampPath");
	public static final Property TREE_SHAPE = createProperty("https://w3id.org/tree#", "shape");
	private final RequestExecutor requestExecutor;

	public LdesPropertiesExtractor(RequestExecutor requestExecutor) {
		this.requestExecutor = requestExecutor;
	}

	protected Optional<String> getPropertyValue(Model model, Property property) {
		return model.listStatements(null, property, (Resource) null)
				.nextOptional()
				.map(Statement::getObject)
				.map(RDFNode::asResource)
				.map(Resource::toString);
	}

	public LdesProperties getLdesProperties(Ldes ldes, boolean needTimestampPath, boolean needVersionOfPath,
			boolean needShape) {
		TreeNodeRequest request = ldes.createRequest(ldes.getStartingNodeUrl());
		Request httpRequest = request.createRequest();
		Response response = requestExecutor.execute(httpRequest);
		Model model = RDFParser
				.fromString(response.getBody().orElseThrow())
				.lang(request.getLang())
				.build()
				.toModel();
		String timestampPath = getProperty(needTimestampPath, getProperty(model), LDES_TIMESTAMP_PATH);
		String versionOfPath = getProperty(needVersionOfPath, getVersionOfPath(model), LDES_VERSION_OF);
		String shape = getProperty(needShape, getShaclShape(model), TREE_SHAPE);
		return new LdesProperties(timestampPath, versionOfPath, shape);
	}

	private String getProperty(boolean needTimestampPath, Optional<String> model, Property ldesTimestampPath) {
		return needTimestampPath ? model
				.orElseThrow(() -> new LdesPropertyNotFoundException(ldesTimestampPath.toString()))
				: null;
	}

	public Optional<String> getProperty(Model model) {
		return getPropertyValue(model, LDES_TIMESTAMP_PATH);
	}

	public Optional<String> getVersionOfPath(Model model) {
		return getPropertyValue(model, LDES_VERSION_OF);
	}

	public Optional<String> getShaclShape(Model model) {
		return getPropertyValue(model, TREE_SHAPE);
	}

}
