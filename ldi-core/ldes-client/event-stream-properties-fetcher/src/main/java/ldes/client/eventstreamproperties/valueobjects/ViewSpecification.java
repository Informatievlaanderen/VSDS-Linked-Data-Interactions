package ldes.client.eventstreamproperties.valueobjects;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;

import java.util.Optional;

import static org.apache.jena.rdf.model.ResourceFactory.createProperty;

public class ViewSpecification implements StartingNodeSpecification {
	public static final String LDES = "https://w3id.org/ldes#";
	public static final Property LDES_EVENT_STREAM = createProperty(LDES, "EventStream");
	public static final String RDF_SYNTAX = "http://www.w3.org/1999/02/22-rdf-syntax-ns#";
	public static final Property RDF_SYNTAX_TYPE = createProperty(RDF_SYNTAX, "type");
	public static final Property TREE_SHAPE = createProperty("https://w3id.org/tree#", "shape");
	public static final Property LDES_VERSION_OF_PATH = createProperty(LDES, "versionOfPath");
	public static final Property LDES_TIMESTAMP_PATH = createProperty(LDES, "timestampPath");

	private final Model model;

	public ViewSpecification(Model model) {
		this.model = model;
	}

	@Override
	public EventStreamProperties extractEventStreamProperties() {
		final Resource subject = extractEventStream(model).orElseThrow();
		final String shapeUri = Optional.ofNullable(subject.getPropertyResourceValue(TREE_SHAPE))
				.map(Resource::getURI)
				.orElse("");
		return new EventStreamProperties(
				subject.getURI(),
				subject.getPropertyResourceValue(LDES_VERSION_OF_PATH).getURI(),
				subject.getPropertyResourceValue(LDES_TIMESTAMP_PATH).getURI(),
				shapeUri
		);
	}

	public static boolean isViewSpecification(Model model) {
		return extractEventStream(model).isPresent();
	}

	private static Optional<Resource> extractEventStream(Model model) {
		return model.listSubjectsWithProperty(RDF_SYNTAX_TYPE, LDES_EVENT_STREAM).nextOptional();
	}
}
