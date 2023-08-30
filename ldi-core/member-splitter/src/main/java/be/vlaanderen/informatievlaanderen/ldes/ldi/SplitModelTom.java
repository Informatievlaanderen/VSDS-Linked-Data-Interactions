package be.vlaanderen.informatievlaanderen.ldes.ldi;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;

import java.util.List;
import java.util.Map;
import java.util.Set;

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

    Map<Integer, Set<Statement>> statementsPerModel;
    Map<Resource, Integer> subjects;
    Map<RDFNode, Integer> objects;


    @Override
    public List<Model> split(Model model) {
        return null;
    }

}
