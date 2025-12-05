package be.vlaanderen.informatievlaanderen.ldes.ldio.sparqlselect;

import be.vlaanderen.informatievlaanderen.ldes.ldio.converter.ValueConverter;
import be.vlaanderen.informatievlaanderen.ldes.ldio.dto.ColumnsDTO;
import be.vlaanderen.informatievlaanderen.ldes.ldio.dto.DataModelDTO;
import be.vlaanderen.informatievlaanderen.ldes.ldio.dto.ValuesDTO;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.Model;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class SparqlSelectService {
    private final Log logger = LogFactory.getLog(getClass());
    private final ValueConverter valueConverter;

    public SparqlSelectService(ValueConverter valueConverter) {
        this.valueConverter = valueConverter;
    }

    public DataModelDTO execute(Model model, String sparqlQuery, DataModelDTO dataModelDTO) {
        if (model == null || sparqlQuery == null || sparqlQuery.isBlank() || dataModelDTO == null) {
            logger.error("Model (%s), sparqlQuery (%s) and dataModelDTO (%s) must not be null - throwing an IllegalArgumentException".formatted(model, sparqlQuery, dataModelDTO));
            throw new IllegalArgumentException("Model, sparqlQuery and dataModelDTO must not be null");
        }

        Query query = QueryFactory.create(sparqlQuery);

        logger.debug("Executing SPARQL query: " + sparqlQuery);
        try (QueryExecution queryExecution = QueryExecutionFactory.create(query, model)) {
            ResultSet rs = queryExecution.execSelect();
            if (dataModelDTO.getColumns() == null) {
                dataModelDTO.setColumns(new ColumnsDTO(rs.getResultVars()));
                logger.debug("Extracted the columns (%s) from the result set, and stored them the into the DataModelDTO".formatted(dataModelDTO.getColumns()));
            }
            List<String> columns = dataModelDTO.getColumns().columns();

            List<List<Object>> values = new ArrayList<>();
            // TODO multiple rows
            while (rs.hasNext()) {
                QuerySolution rowData = rs.next();

                List<Object> rowValues = columns.stream()
                        .map(rowData::get)
                        .map(valueConverter::convert)
                        .collect(Collectors.toList());
                values.add(rowValues);
            }
            dataModelDTO.setData(new ValuesDTO(values));
            if (logger.isDebugEnabled()) {
                logger.debug("Results from the SPARQL SELECT query: " + dataModelDTO);
            } else if (logger.isInfoEnabled()) {
                if (model.listSubjects() != null && model.listSubjects().hasNext()) {
                    logger.info("processing: " + model.listSubjects().next().toString());
                }
            }
            return dataModelDTO;
        } catch (Exception e) {
            logger.error("Error executing SPARQL query: " + sparqlQuery, e);
            throw new RuntimeException(e);
        }
    }
}
