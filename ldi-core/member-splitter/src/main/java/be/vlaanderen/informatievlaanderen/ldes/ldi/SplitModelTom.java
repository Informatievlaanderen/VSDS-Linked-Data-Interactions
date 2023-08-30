package be.vlaanderen.informatievlaanderen.ldes.ldi;

import org.apache.jena.rdf.model.*;

import java.util.*;

//map1 = Map<modelId, Set<Statement>>
//        map2 = Map<Subject|Object, modelId>
//
//        Voor elke triple(Statement) zoek subject en object in map2
//        Geen match is nieuwe modelId en alles in beide mappen toevoegen
//        1 match is beide subject en object toewijzen in map2 en toevoegen in map1
//        2 matches is mergen modelIds en subject en object toewijzen (todo elegant merge algoritme)
//
//        Dan kwestie van subjects zoeken van type T en kijken in welke graphs ze zitten in map2 en de statements uit map1 gebruiken om model te maken
public class SplitModelTom implements Splittable {

    Map<Integer, Set<Statement>> statementsPerModel = new HashMap<>();
    Map<Resource, Integer> subjects = new HashMap<>();
    Map<RDFNode, Integer> objects = new HashMap<>();
    int sequence;

    @Override
    public List<Model> split(Model model) {
        List<Statement> statementsList = model.listStatements().toList();


        for (Statement statement : statementsList) {
            System.out.println("=== new run ===");
            System.out.println(subjects.values());
            System.out.println(objects.values());
            Integer subjectModelId = subjects.get(statement.getSubject());
            Integer objectModelId = objects.get(statement.getObject());

            // Not present in existing model yet -> create model, add statement to model
            // add subject and object
            if (subjectModelId == null && objectModelId == null) {
                ++sequence;
                HashSet<Statement> statements = new HashSet<>();
                statements.add(statement);
                statementsPerModel.put(sequence, statements);
                subjects.put(statement.getSubject(), sequence);
                objects.put(statement.getObject(), sequence);
                System.out.println("created: " + sequence);
                // Only present in 1 model -> add statement to model
            } else if (Objects.equals(subjectModelId, objectModelId)) {
                addStatementToModel(statement, subjectModelId);
            } else if (subjectModelId == null) {
                subjects.put(statement.getSubject(), objectModelId);
                addStatementToModel(statement, objectModelId);
            } else if (objectModelId == null) {
                objects.put(statement.getObject(), subjectModelId);
                addStatementToModel(statement, subjectModelId);
            } else {
                // Present in 2 models
                addStatementToModel(statement, subjectModelId);

                mergeModels(subjectModelId, objectModelId, statement);
            }


        }

        return statementsPerModel.values().stream().map(statements -> {
            Model m = ModelFactory.createDefaultModel();
            m.add(statements.stream().toList());
            return m;
        }).toList();
    }

    private void mergeModels(Integer subjectModelId, Integer objectModelId, Statement statement) {
        Set<Statement> mergedStatements = new HashSet<>();
        mergedStatements.addAll(statementsPerModel.get(subjectModelId));
        mergedStatements.addAll(statementsPerModel.get(objectModelId));

        statementsPerModel.remove(subjectModelId);
        statementsPerModel.remove(objectModelId);

        subjects.entrySet().removeIf(entry -> entry.getValue().equals(subjectModelId) || entry.getValue().equals(objectModelId));
        System.out.println("subject still exists " + subjects.containsValue(subjectModelId));
        System.out.println(subjects.values());
        System.out.println("removing objectId: " + objectModelId);
        objects.entrySet().removeIf(entry -> entry.getValue().equals(subjectModelId) || entry.getValue().equals(objectModelId));
        System.out.println("object still exists " + subjects.containsValue(subjectModelId));
        System.out.println(objects.values());

        statementsPerModel.put(++sequence, mergedStatements);
        subjects.put(statement.getSubject(), sequence);
        objects.put(statement.getObject(), sequence);

        System.out.println("removed: " + subjectModelId + " and " + objectModelId);
        System.out.println("created after merge: " + sequence);
    }

    private void addStatementToModel(Statement statement, Integer modelId) {
        Set<Statement> statements = statementsPerModel.get(modelId);
        if (statements == null) {
            System.out.println("help" + modelId);
        }
        statements.add(statement);
//        statements.add(statement);
//        statementsPerModel.compute(modelId, (key, value) -> {
//            if (value == null) {
//                System.out.println("help");
//            }
//            value.add(statement);
//            return value;
//        });
    }

}
