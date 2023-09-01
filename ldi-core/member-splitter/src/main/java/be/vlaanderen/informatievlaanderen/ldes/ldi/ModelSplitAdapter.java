package be.vlaanderen.informatievlaanderen.ldes.ldi;

import be.vlaanderen.informatievlaanderen.ldes.ldi.types.LdiAdapter;
import org.apache.jena.rdf.model.Model;

import java.util.stream.Stream;

// TODO TVB: 01/09/23 test me
public class ModelSplitAdapter implements LdiAdapter {

    private final String memberType;
    private final LdiAdapter ldiAdapter;
    private final ModelSplitter modelSplitter = new ModelSplitter();

    public ModelSplitAdapter(String memberType, LdiAdapter ldiAdapter) {
        this.memberType = memberType;
        this.ldiAdapter = ldiAdapter;
    }

    @Override
    public Stream<Model> apply(Content content) {
        return ldiAdapter.apply(content).flatMap(model -> modelSplitter.split(model, memberType).stream());
    }

}
