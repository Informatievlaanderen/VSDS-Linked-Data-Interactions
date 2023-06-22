package be.vlaanderen.informatievlaanderen.ldes.ldi;

import be.vlaanderen.informatievlaanderen.ldes.ldi.rmlFunctions.ReplaceFunctions;
import be.vlaanderen.informatievlaanderen.ldes.ldi.rmlFunctions.WktFunctions;
import be.vlaanderen.informatievlaanderen.ldes.ldi.types.LdiAdapter;
import io.carml.engine.rdf.RdfRmlMapper;
import io.carml.logicalsourceresolver.CsvResolver;
import io.carml.logicalsourceresolver.JsonPathResolver;
import io.carml.logicalsourceresolver.XPathResolver;
import io.carml.model.TriplesMap;
import io.carml.util.jena.JenaCollectors;
import io.carml.util.jena.JenaConverters;
import io.carml.vocab.Rdf;
import org.apache.commons.io.IOUtils;
import org.apache.jena.query.Dataset;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Statement;

import java.util.*;
import java.util.stream.Stream;

import static java.util.stream.StreamSupport.stream;

public class RmlAdapter implements LdiAdapter {
	private final RdfRmlMapper rmlMapper;

	public RmlAdapter(Set<TriplesMap> mapping) {
		this.rmlMapper = RdfRmlMapper.builder()
				.triplesMaps(mapping)
				.setLogicalSourceResolver(Rdf.Ql.JsonPath, JsonPathResolver::getInstance)
				.setLogicalSourceResolver(Rdf.Ql.XPath, XPathResolver::getInstance)
				.setLogicalSourceResolver(Rdf.Ql.Csv, CsvResolver::getInstance)
				.addFunctions(new WktFunctions(), new ReplaceFunctions())
				.build();
	}

	@Override
	public Stream<Model> apply(Content content) {
		Dataset dataset = rmlMapper.mapToModel(IOUtils.toInputStream(content.content()))
				.stream().map(JenaConverters::toQuad)
				.collect(JenaCollectors.toDataset());

		if (!dataset.listNames().hasNext()) {
			return Stream.of(dataset.getDefaultModel());
		} else {
			return stream(Spliterators.spliteratorUnknownSize(dataset.listNames(), Spliterator.ORDERED), true)
					.map(dataset::getNamedModel)
					.map(model -> enrichModel(model, dataset));
		}
	}

	private Model enrichModel(Model input, Dataset completeDataset) {
		Stack<Statement> statementsToProcess = new Stack<>();
		List<Statement> newStatements = new ArrayList<>();

		input.listStatements().forEach(statementsToProcess::push);

		while (!statementsToProcess.isEmpty()) {
			Statement statement = statementsToProcess.pop();
			if (statement.getObject().isResource()) {
				completeDataset.getDefaultModel().listStatements(statement.getResource(), null, (RDFNode) null)
						.forEach(statement1 -> {
							newStatements.add(statement1);
							if (!statement1.getObject().isLiteral()) {
								statementsToProcess.add(statement1);
							}
						});
			}
		}

		input.add(newStatements);
		return input;
	}
}
