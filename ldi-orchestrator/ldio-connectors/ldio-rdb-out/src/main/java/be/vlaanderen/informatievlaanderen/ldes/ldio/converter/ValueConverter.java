package be.vlaanderen.informatievlaanderen.ldes.ldio.converter;

import org.apache.jena.datatypes.xsd.impl.XSDDateTimeType;
import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;

public class ValueConverter {
    public Object convert(RDFNode node) {
        if (node == null) {
            return null;
        } else if (node.isLiteral()) {
            Literal literal = node.asLiteral();
            switch (literal.getDatatype()) {
                case XSDDateTimeType ignored -> {
                    if (literal.getLexicalForm().endsWith("Z")) {
                        return OffsetDateTime.parse(literal.getLexicalForm());
                    } else if (literal.getLexicalForm().contains("+")) {
                        return OffsetDateTime.parse(literal.getLexicalForm());
                    } else {
                        return LocalDateTime.parse(literal.getLexicalForm());
                    }
                }
                default -> {
                    return literal.getValue();
                }
            }
        } else if (node.isResource()) {
            Resource resource = node.asResource();
            if (resource.isURIResource()) {
                return resource.getURI();
            } else {
                return resource.toString();
            }
        }
        throw new RuntimeException("Unexpected node type: " + node.getClass().getName());
    }
}