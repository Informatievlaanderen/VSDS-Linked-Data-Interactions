package ldes.client.vsdsdocumentloader;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.nio.file.Paths;

import org.apache.jena.ext.com.google.common.io.Files;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFParserBuilder;
import org.apache.jena.riot.lang.LangJSONLD10;
import org.apache.jena.sparql.util.Context;
import org.junit.jupiter.api.Test;

import com.github.jsonldjava.core.DocumentLoader;
import com.github.jsonldjava.core.JsonLdError;
import com.github.jsonldjava.core.JsonLdOptions;
import com.github.jsonldjava.core.RemoteDocument;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;

import ldes.client.requestexecutor.domain.valueobjects.Response;
import ldes.client.requestexecutor.domain.valueobjects.executorsupplier.DefaultConfig;
import ldes.client.requestexecutor.executor.RequestExecutor;
import ldes.client.treenodefetcher.domain.valueobjects.TreeNodeRequest;

@WireMockTest(httpPort = 10101)
public class VSDSDocumentLoaderTest {
	
	private static final Lang LANG = Lang.JSONLD11;
	private static final String URL = "http://localhost:10101/200-1-relation-3-members";
	
	@Test
	void test() {
		DocumentLoader documentLoader = new VSDSDocumentLoader();
		
		JsonLdOptions options = new JsonLdOptions();
		options.setDocumentLoader(documentLoader);

		Context context = new Context();
		Context
		context.set(LangJSONLD10.JSONLD_OPTIONS, options);
		
		RequestExecutor requestExecutor = new DefaultConfig().createRequestExecutor();
		TreeNodeRequest treeNodeRequest = new TreeNodeRequest(URL, LANG, null);

		Response response = requestExecutor.execute(treeNodeRequest.createRequest());
		String responseBody = response.getBody().orElseThrow();

		RDFParserBuilder parser = RDFParserBuilder.create().fromString(responseBody).context(context).forceLang(LANG);
		
		Model model = parser.toModel();
		
		assertEquals(1, VSDSDocumentLoader.counter, "The VSDSDocumentLoader is used to load the json context");
	}

	private class VSDSDocumentLoader extends DocumentLoader {

		private static int counter = 0;

		@Override
		public RemoteDocument loadDocument(String url) throws JsonLdError {
			try {
				Files.write((counter + " Loading URL " + url).getBytes(), Paths.get("test.log").toFile());
			} catch (Exception e) {
				
			}
			
			counter++;

			return super.loadDocument(url);
		}
	}
}
