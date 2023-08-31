package be.vlaanderen.informatievlaanderen.ldes.ldi;

import org.apache.jena.rdf.model.*;

import java.util.*;

public class SplitModel implements Splittable {

    @Override
    public List<Model> split(Model model) {
        return splitToMap(model).values().stream().toList();
    }

    public Map<String, Model> splitToMap(Model input) {
        Map<String, Model> map = new HashMap<>();
        Set<Resource> subjects = new HashSet<>();

        input.listSubjects().forEach((Resource subject) -> {
            if (subject.isAnon())
                return;
            subjects.add(subject);
        });
        subjects.forEach((Resource subject) -> {
            Stack<Resource> subjectsOfIncludedStatements = new Stack<>();
            subjectsOfIncludedStatements.push(subject);
            Model LDESMemberModel = ModelFactory.createDefaultModel();
            while (!subjectsOfIncludedStatements.isEmpty()) {
                Resource includedSubject = subjectsOfIncludedStatements.pop();
                input.listStatements(includedSubject, null, (String) null).forEach((Statement includedStatement) -> {
                    LDESMemberModel.add(includedStatement);
                    RDFNode object = includedStatement.getObject();
                    if (object.isAnon()) {
                        subjectsOfIncludedStatements.push((Resource) object);
                    }
                });
            }
            map.put(subject.toString(), LDESMemberModel);
        });

        return map;
    }
}
