package be.vlaanderen.informatievlaanderen.ldes.client.services;

import be.vlaanderen.informatievlaanderen.ldes.client.valueobjects.LdesFragment;
import org.apache.jena.riot.Lang;

public interface LdesFragmentFetcher {

	/**
	 * Returns the expected {@link Lang} of the data source.
	 *
	 * This value is forced on the jena parsers.
	 *
	 * @return the expected {@link Lang} of the data source
	 */
	Lang getDataSourceFormat();

	void setDataSourceFormat(Lang dataSourceFormat);

	LdesFragment fetchFragment(String fragmentUrl);
}
