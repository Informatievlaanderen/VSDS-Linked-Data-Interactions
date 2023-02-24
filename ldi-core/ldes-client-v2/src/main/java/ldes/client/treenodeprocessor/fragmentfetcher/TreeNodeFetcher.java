package ldes.client.treenodeprocessor.fragmentfetcher;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.client.HttpClients;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFParser;

import java.io.IOException;

public class TreeNodeFetcher {
	protected Lang dataSourceFormat = Lang.JSONLD;
	private HttpClient httpClient= HttpClients.createDefault();

	public TreeNodeFetcher() {
	}

	public Model fetchFragment(String fragmentUrl) {

		try {
			HttpClientContext context = HttpClientContext.create();

			HttpGet request = new HttpGet(fragmentUrl);
			request.addHeader("Accept", dataSourceFormat.getContentType().toHeaderString());


			HttpResponse httpResponse = httpClient.execute(request, context);
			return RDFParser.source(httpResponse.getEntity().getContent()).forceLang(dataSourceFormat).toModel();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

	}
}
