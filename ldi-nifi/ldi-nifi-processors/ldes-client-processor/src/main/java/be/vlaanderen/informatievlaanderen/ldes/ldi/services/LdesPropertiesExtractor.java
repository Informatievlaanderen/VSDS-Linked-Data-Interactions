package be.vlaanderen.informatievlaanderen.ldes.ldi.services;

import be.vlaanderen.informatievlaanderen.ldes.ldi.domain.valueobjects.LdesProperties;
import be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.valueobjects.Request;
import be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.valueobjects.Response;
import be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.executor.RequestExecutor;
import ldes.client.treenodefetcher.domain.valueobjects.TreeNodeRequest;
import ldes.client.treenodesupplier.StartingTreeNodeSupplier;
import ldes.client.treenodesupplier.domain.valueobject.LdesMetaData;
import ldes.client.treenodesupplier.domain.valueobject.StartingTreeNode;
import org.apache.jena.rdf.model.*;
import org.apache.jena.riot.RDFParser;

import java.util.Optional;

import static org.apache.jena.rdf.model.ResourceFactory.createProperty;

public class LdesPropertiesExtractor {

	private static final String LDES = "https://w3id.org/ldes#";
	private static final Property LDES_VERSION_OF = createProperty(LDES, "versionOfPath");
	private static final Property LDES_TIMESTAMP_PATH = createProperty(LDES, "timestampPath");
	private static final Property TREE_SHAPE = createProperty("https://w3id.org/tree#", "shape");
	private final RequestExecutor requestExecutor;

	public LdesPropertiesExtractor(RequestExecutor requestExecutor) {
		this.requestExecutor = requestExecutor;
	}

	private Optional<String> getPropertyValue(Model model, Property property) {
		return model.listStatements(null, property, (Resource) null)
				.nextOptional()
				.map(Statement::getObject)
				.map(RDFNode::asResource)
				.map(Resource::toString);
	}

	public LdesProperties getLdesProperties(LdesMetaData ldesMetaData, boolean needTimestampPath,
                                            boolean needVersionOfPath,
                                            boolean needShape) {
		StartingTreeNode startingTreeNode = new StartingTreeNodeSupplier(requestExecutor)
				.getStart(ldesMetaData.getStartingNodeUrl(), ldesMetaData.getLang());
		TreeNodeRequest request = startingTreeNode.createRequest(startingTreeNode.getStartingNodeUrl());
		Request httpRequest = request.createRequest();
		Response response = requestExecutor.execute(httpRequest);
		Model model = RDFParser
				.fromString(response.getBody().orElseThrow())
				.lang(request.getLang())
				.build()
				.toModel();
		String timestampPath = getResource(needTimestampPath, getResource(model), LDES_TIMESTAMP_PATH);
		String versionOfPath = getResource(needVersionOfPath, getVersionOfPath(model), LDES_VERSION_OF);
		String shape = getResource(needShape, getShaclShape(model), TREE_SHAPE);
		return new LdesProperties(timestampPath, versionOfPath, shape);
	}

	private String getResource(boolean resourceNeeded, Optional<String> resource, Property property) {
		return resourceNeeded ? resource
				.orElseThrow(() -> new LdesPropertyNotFoundException(property.toString()))
				: null;
	}

	public Optional<String> getResource(Model model) {
		return getPropertyValue(model, LDES_TIMESTAMP_PATH);
	}

	public Optional<String> getVersionOfPath(Model model) {
		return getPropertyValue(model, LDES_VERSION_OF);
	}

	public Optional<String> getShaclShape(Model model) {
		return getPropertyValue(model, TREE_SHAPE);
	}

}
