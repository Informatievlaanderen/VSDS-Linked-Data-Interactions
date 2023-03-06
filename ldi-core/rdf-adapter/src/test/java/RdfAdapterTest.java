import be.vlaanderen.informatievlaanderen.ldes.ldi.RdfAdapter;
import be.vlaanderen.informatievlaanderen.ldes.ldi.types.LdiAdapter;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFParserBuilder;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

class RdfAdapterTest {

	private static final String expectedQuads = """
			_:b0 <http://schema.org/jobTitle> "Professor" .
			_:b0 <http://schema.org/name> "Jane Doe" .
			_:b0 <http://schema.org/telephone> "(425) 123-4567" .
			_:b0 <http://schema.org/url> <http://www.janedoe.com> .
			_:b0 <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://schema.org/Person> .
			""";

	private static final Model expectedModel = RDFParserBuilder.create().fromString(expectedQuads).lang(Lang.NQUADS)
			.toModel();

	@Test
	void adapt_jsonLd() {
		RdfAdapter adapter = new RdfAdapter();

		String content = """
				{
				  "@context": "http://schema.org/",
				  "@type": "Person",
				  "name": "Jane Doe",
				  "jobTitle": "Professor",
				  "telephone": "(425) 123-4567",
				  "url": "http://www.janedoe.com"
				}""";

		Model result = adapter.apply(LdiAdapter.InputObject.of(content, "application/ld+json"))
				.findFirst()
				.orElseThrow();

		assertTrue(result.isIsomorphicWith(expectedModel));
	}

	@Test
	void adapt_turtle() {
		RdfAdapter adapter = new RdfAdapter();

		String content = """
				@prefix schema: <http://schema.org/> .

				 []
				   schema:jobTitle "Professor" ;
				   schema:name "Jane Doe" ;
				   schema:telephone "(425) 123-4567" ;
				   schema:url <http://www.janedoe.com> ;
				   a schema:Person .
				""";

		Model result = adapter.apply(LdiAdapter.InputObject.of(content, "text/turtle"))
				.findFirst()
				.orElseThrow();

		assertTrue(result.isIsomorphicWith(expectedModel));
	}
}
