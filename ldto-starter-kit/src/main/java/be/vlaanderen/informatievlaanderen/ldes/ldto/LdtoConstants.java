package be.vlaanderen.informatievlaanderen.ldes.ldto;

import org.apache.jena.riot.Lang;

public class LdtoConstants {

	// Transformer constants
	public static final String QUERY = "query";
	public static final String QUERY_VALIDATION_MSG = "Must provide a valid construct query";
	public static final String INFER = "infer";


	// Output constants
	public static final String CONTENT_TYPE = "content-type";
	public static final String ENDPOINT = "endpoint";
	public static final Lang DEFAULT_OUTPUT_LANG = Lang.NQUADS;
}
