package be.vlaanderen.informatievlaanderen.ldes.ldi;

import org.apache.jena.rdf.model.*;
import org.apache.jena.vocabulary.RDF;

import java.util.*;

import static org.apache.jena.rdf.model.ResourceFactory.createProperty;

public class SplitModel implements Splittable {

    @Override
    public List<Model> split(Model model, String memberType) {
        return splitToMap(model, memberType).values().stream().toList();
    }

    public Map<String, Model> splitToMap(Model input, String memberType) {
        Map<String, Model> map = new HashMap<>();
//        Set<Resource> subjects = new HashSet<>();

        // TODO: 31/08/23 stop cyclic dependencies
        Property property = createProperty(memberType);
        Set<Resource> subjects = input.listSubjectsWithProperty(RDF.type, property).toSet();
//        input.listSubjects().forEach((Resource subject) -> {
//            if (subject.isAnon()) {
//                return;
//            }
//            subjects.add(subject);
//        });
        subjects.forEach((Resource subject) -> {
            Stack<Resource> subjectsOfIncludedStatements = new Stack<>();
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
            map.put(subject.toString(), LDESMemberModel);
        });

        return map;
    }
}
