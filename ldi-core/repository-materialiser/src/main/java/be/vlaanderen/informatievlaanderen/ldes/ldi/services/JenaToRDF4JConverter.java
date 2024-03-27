package be.vlaanderen.informatievlaanderen.ldes.ldi.services;

import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.RDFNode;
import org.eclipse.rdf4j.model.*;
import org.eclipse.rdf4j.model.impl.LinkedHashModel;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;

public class JenaToRDF4JConverter {

	private JenaToRDF4JConverter() {
	}

	public static Model convert(org.apache.jena.rdf.model.Model jenaModel) {
		Model rdf4jModel = new LinkedHashModel();

		jenaModel.listStatements().forEachRemaining(jenaStatement -> {
			Resource subject = convertResource(jenaStatement.getSubject());
			IRI predicate = convertProperty(jenaStatement.getPredicate());
			Value object = convertValue(jenaStatement.getObject());

			rdf4jModel.add(subject, predicate, object);
		});

		return rdf4jModel;
	}

	private static Resource convertResource(org.apache.jena.rdf.model.Resource resource) {
		if(resource.isURIResource()) {
			return SimpleValueFactory.getInstance().createIRI(resource.getURI());
		}
		return SimpleValueFactory.getInstance().createBNode(resource.getId().getLabelString());
	}

	private static IRI convertProperty(org.apache.jena.rdf.model.Property property) {
		return SimpleValueFactory.getInstance().createIRI(property.getURI());
	}

	private static Value convertValue(RDFNode node) {
		if (node.isResource()) {
			org.apache.jena.rdf.model.Resource resource = (org.apache.jena.rdf.model.Resource) node;
			return convertResource(resource);
		} else {
			Literal literal = node.asLiteral();
			if(literal.getLanguage() != null && !literal.getLanguage().isEmpty()) {
				return SimpleValueFactory.getInstance().createLiteral(literal.getValue().toString(), literal.getLanguage());
			}
			return SimpleValueFactory.getInstance().createLiteral(literal.getValue().toString());
		}
	}
}
