package be.vlaanderen.informatievlaanderen.ldes.ldi.services;

import org.apache.jena.rdf.model.Model;

/**
 * The Component Executor build a pipeline for the Linked Data Interactions
 * Orchestrator.
 *
 * Each linked data model (RDF) passed on to the Component Executor will be
 * passed through all configured
 * LDI Transformers to finally be exported by all configured LDI Outputs
 */
public interface ComponentExecutor {
	/**
	 * Passes linked data model to the Linked Data Interactions Orchestrator
	 * pipeline
	 *
	 * @param linkedDataModel
	 *            A RDF Model to be transformed
	 */
	void transformLinkedData(Model linkedDataModel);
}
