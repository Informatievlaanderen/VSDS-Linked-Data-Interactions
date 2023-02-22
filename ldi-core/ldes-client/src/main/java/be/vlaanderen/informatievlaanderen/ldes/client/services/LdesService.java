package be.vlaanderen.informatievlaanderen.ldes.client.services;

import be.vlaanderen.informatievlaanderen.ldes.client.state.LdesStateManager;
import be.vlaanderen.informatievlaanderen.ldes.client.valueobjects.LdesFragment;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.riot.Lang;

import java.util.stream.Stream;

public interface LdesService {

	Lang getDataSourceFormat();

	void setDataSourceFormat(Lang dataSourceFormat);

	/**
	 * Queues a fragment for processing.
	 *
	 * @param fragmentId
	 *            the fragment id (i.e. URL) to process.
	 */
	void queueFragment(String fragmentId);

	/**
	 * Checks if there are unprocessed fragments
	 *
	 * @return true if there are unprocessed fragments, false otherwise.
	 */
	boolean hasFragmentsToProcess();

	/**
	 * Processes the next available LDES fragment.
	 *
	 * @return the processed LDES fragment containing the members and an expiration
	 *         date.
	 */
	LdesFragment processNextFragment();

	LdesStateManager getStateManager();

	Stream<Statement> extractRelations(Model fragmentModel);

	/**
	 * Returns the interval that is added to construct a fragment expiration
	 * interval.
	 *
	 * When a fragment is received without an expiration interval, e.g. for HTTP
	 * requests without a max-age element in the Cache-control header, this value is
	 * added to the current time to set the expiration date.
	 *
	 * @return the interval that is added to construct a fragment expiration
	 *         interval
	 */
	Long getFragmentExpirationInterval();

	void setFragmentExpirationInterval(Long fragmentExpirationInterval);
}
