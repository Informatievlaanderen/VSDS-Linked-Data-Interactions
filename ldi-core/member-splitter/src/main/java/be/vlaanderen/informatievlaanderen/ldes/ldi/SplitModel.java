package be.vlaanderen.informatievlaanderen.ldes.ldi;

import org.apache.jena.rdf.model.*;
import org.apache.jena.vocabulary.RDF;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Set;
import java.util.stream.Collectors;

import static org.apache.jena.rdf.model.ResourceFactory.createProperty;

public class SplitModel implements Splittable {

    @Override
    public Set<Model> split(Model input, String memberType) {
        Property property = createProperty(memberType);
        Set<Resource> subjects = input.listSubjectsWithProperty(RDF.type, property).toSet();

        return subjects.stream().map((Resource subject) -> {
            Deque<Resource> subjectsOfIncludedStatements = new ArrayDeque<>();
            subjectsOfIncludedStatements.push(subject);
            Model LDESMemberModel = ModelFactory.createDefaultModel();
            while (!subjectsOfIncludedStatements.isEmpty()) {
                Resource includedSubject = subjectsOfIncludedStatements.pop();
                input.listStatements(includedSubject, null, (String) null).forEach((Statement includedStatement) -> {
                    LDESMemberModel.add(includedStatement);
                    RDFNode object = includedStatement.getObject();
                    if (object.isResource()) {
                        subjectsOfIncludedStatements.push((Resource) object);
                    }
                });
            }
            return LDESMemberModel;
        }).collect(Collectors.toSet());
    }
}
