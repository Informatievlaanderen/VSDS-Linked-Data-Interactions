package be.vlaanderen.informatievlaanderen.ldes.ldi.datasetsplitter;

import org.apache.jena.query.Dataset;
import org.apache.jena.rdf.model.Model;

import java.util.List;

@FunctionalInterface
public interface DatasetSplitter {
	List<Model> split(Dataset dataset);
}
