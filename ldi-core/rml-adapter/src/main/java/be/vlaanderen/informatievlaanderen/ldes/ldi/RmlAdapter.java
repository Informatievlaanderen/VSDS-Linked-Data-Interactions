package be.vlaanderen.informatievlaanderen.ldes.ldi;

import be.vlaanderen.informatievlaanderen.ldes.ldi.functions.ReplaceFunctions;
import be.vlaanderen.informatievlaanderen.ldes.ldi.functions.TimeFunctions;
import be.vlaanderen.informatievlaanderen.ldes.ldi.functions.WktFunctions;
import be.vlaanderen.informatievlaanderen.ldes.ldi.types.LdiAdapter;
import io.carml.engine.rdf.RdfRmlMapper;
import io.carml.logicalsourceresolver.CsvResolver;
import io.carml.logicalsourceresolver.JsonPathResolver;
import io.carml.logicalsourceresolver.XPathResolver;
import io.carml.model.TriplesMap;
import io.carml.util.RmlMappingLoader;
import io.carml.util.jena.JenaCollectors;
import io.carml.util.jena.JenaConverters;
import io.carml.vocab.Rdf;
import org.apache.commons.io.IOUtils;
import org.apache.jena.query.Dataset;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Statement;
import org.eclipse.rdf4j.model.Resource;
import org.eclipse.rdf4j.model.ValueFactory;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.eclipse.rdf4j.model.util.ModelCollector;
import org.eclipse.rdf4j.model.vocabulary.RDF;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.rio.Rio;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Stream;

import static java.util.stream.StreamSupport.stream;

public class RmlAdapter implements LdiAdapter {
	private final RdfRmlMapper rmlMapper;

	public RmlAdapter(String mappingString) {
		try {
			Set<TriplesMap> mapping = RmlMappingLoader.build()
					.load(convertToCarmlMappingModel(mappingString));
			this.rmlMapper = RdfRmlMapper.builder()
					.triplesMaps(mapping)
					.setLogicalSourceResolver(Rdf.Ql.JsonPath, JsonPathResolver::getInstance)
					.setLogicalSourceResolver(Rdf.Ql.XPath, XPathResolver::getInstance)
					.setLogicalSourceResolver(Rdf.Ql.Csv, CsvResolver::getInstance)
					.addFunctions(new WktFunctions(), new ReplaceFunctions(), new TimeFunctions())
					.build();
		} catch (IOException e) {
			throw new InvalidMappingException(mappingString);
		}
	}

	@Override
	public Stream<Model> apply(Content content) {
		Dataset dataset = Objects.requireNonNull(rmlMapper.map(IOUtils.toInputStream(content.content(), StandardCharsets.UTF_8))
						.collect(ModelCollector.toModel()).share().block())
				.stream().map(JenaConverters::toQuad)
				.collect(JenaCollectors.toDataset());

		if (!dataset.listNames().hasNext()) {
			return Stream.of(dataset.getDefaultModel());
		} else {
			return stream(Spliterators.spliteratorUnknownSize(dataset.listNames(), Spliterator.ORDERED), false)
					.map(dataset::getNamedModel)
					.map(model -> enrichModel(model, dataset));
		}
	}

	private org.eclipse.rdf4j.model.Model convertToCarmlMappingModel(String mappingString) throws IOException {
		final String RML = "http://semweb.mmlab.be/ns/rml#";

		org.eclipse.rdf4j.model.Model mappingModel = Rio.parse(
				IOUtils.toInputStream(mappingString, StandardCharsets.UTF_8), RDFFormat.TURTLE);

		ValueFactory vf = SimpleValueFactory.getInstance();
		var logicalSourceStmts = mappingModel.getStatements(null, RDF.TYPE,
				vf.createIRI(RML, "LogicalSource"));

		List<Resource> sourcesToClean = new ArrayList<>();
		List<org.eclipse.rdf4j.model.Statement> newStatements = new ArrayList<>();

		for (org.eclipse.rdf4j.model.Statement logicalSourceStmt : logicalSourceStmts) {
			Resource sourceSubject = logicalSourceStmt.getSubject();
			sourcesToClean.add(sourceSubject);

			org.eclipse.rdf4j.model.Statement statement = vf.createStatement(vf.createBNode(), RDF.TYPE,
					vf.createIRI("http://carml.taxonic.com/carml/", "Stream"));
			newStatements.add(vf.createStatement(sourceSubject, vf.createIRI(RML, "source"),
					statement.getSubject()));
			newStatements.add(statement);
		}

		sourcesToClean.forEach(source -> {
			mappingModel.remove(source, RDF.TYPE, vf.createIRI(RML, "LogicalSource"));
			mappingModel.remove(source, vf.createIRI(RML, "source"), null);
		});
		mappingModel.addAll(newStatements);

		return mappingModel;
	}

	private Model enrichModel(Model input, Dataset completeDataset) {
		Deque<Statement> statementsToProcess = new ArrayDeque<>();
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
