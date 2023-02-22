package be.vlaanderen.informatievlaanderen.ldes.client.services;

import be.vlaanderen.informatievlaanderen.ldes.client.config.LdesClientConfig;
import be.vlaanderen.informatievlaanderen.ldes.client.exceptions.FragmentFetcherException;
import be.vlaanderen.informatievlaanderen.ldes.client.exceptions.UnparseableFragmentException;
import be.vlaanderen.informatievlaanderen.ldes.client.valueobjects.LdesFragment;
import org.apache.http.HeaderElement;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFParser;
import org.apache.jena.riot.RiotException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URI;
import java.time.LocalDateTime;
import java.util.Optional;

import static be.vlaanderen.informatievlaanderen.ldes.client.LdesClientDefaults.DEFAULT_DATA_SOURCE_FORMAT;
import static be.vlaanderen.informatievlaanderen.ldes.client.LdesClientDefaults.DEFAULT_POLLING_INTERVAL;
import static java.util.Arrays.stream;

public class LdesFragmentFetcherImpl implements LdesFragmentFetcher {

	public static final String CACHE_CONTROL = "cache-control";
	public static final String IMMUTABLE = "immutable";
	public static final String MAX_AGE = "max-age";

	protected LdesClientConfig config;
	protected Lang dataSourceFormat;
	private final HttpClient httpClient;
	private static final Logger LOGGER = LoggerFactory.getLogger(LdesFragmentFetcherImpl.class);

	public LdesFragmentFetcherImpl(LdesClientConfig config, HttpClient httpClient) {
		this.httpClient = httpClient;
		this.config = config;
		this.dataSourceFormat = DEFAULT_DATA_SOURCE_FORMAT;
	}

	@Override
	public Lang getDataSourceFormat() {
		return dataSourceFormat;
	}

	@Override
	public void setDataSourceFormat(Lang dataSourceFormat) {
		this.dataSourceFormat = dataSourceFormat;
	}

	@Override
	public LdesFragment fetchFragment(String fragmentUrl) {
		LdesFragment fragment = new LdesFragment();

		try {
			HttpClientContext context = HttpClientContext.create();

			HttpGet request = new HttpGet(fragmentUrl);
			request.addHeader("Accept", dataSourceFormat.getContentType().toHeaderString());

			if (config.hasApiKey()) {
				request.addHeader(config.getApiKeyHeader(), config.getApiKey());
			}

			HttpResponse httpResponse = httpClient.execute(request, context);

			fragment.setFragmentId(Optional.ofNullable(context.getRedirectLocations())
					.flatMap(uris -> uris.stream().reduce((uri, uri2) -> uri2)).map(URI::toString).orElse(fragmentUrl));

			if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				RDFParser.source(httpResponse.getEntity().getContent()).forceLang(dataSourceFormat)
						.parse(fragment.getModel());

				stream(httpResponse.getHeaders(CACHE_CONTROL)).findFirst().ifPresent(header -> {
					if (stream(header.getElements())
							.noneMatch(headerElement -> IMMUTABLE.equals(headerElement.getName()))) {
						stream(header.getElements())
								.filter(headerElement -> MAX_AGE.equals(headerElement.getName())).findFirst()
								.map(HeaderElement::getValue).map(Long::parseLong)
								.ifPresent(pollingInterval -> fragment
										.setExpirationDate(LocalDateTime.now().plusSeconds(pollingInterval)));
						LOGGER.debug("FETCHED MUTABLE fragment with id: {}", fragment.getFragmentId());
					} else {
						fragment.setImmutable(true);
						LOGGER.debug("FETCHED IMMUTABLE fragment with id: {}", fragment.getFragmentId());
					}
				});
			} else if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_NOT_MODIFIED) {
				fragment.setExpirationDate(LocalDateTime.now().plusSeconds(DEFAULT_POLLING_INTERVAL));
				LOGGER.debug("Fragment was NOT MODIFIED. Did not fetch fragment with id: {}", fragment.getFragmentId());
			} else {
				throw new HttpResponseException(httpResponse.getStatusLine().getStatusCode(),
						httpResponse.getStatusLine().getReasonPhrase());
			}
		} catch (RiotException r) {
			throw new UnparseableFragmentException("Riot can't parse fragment data for " + fragmentUrl + " (Accept: "
					+ dataSourceFormat.getContentType().toHeaderString() + ")", r);
		} catch (ClientProtocolException c) {
			throw new FragmentFetcherException("Protocol exception while fetching fragment " + fragmentUrl
					+ " (Accept: " + dataSourceFormat.getContentType().toHeaderString() + ")", c);
		} catch (IOException ioe) {
			throw new FragmentFetcherException("I/O exception while fetching fragment " + fragmentUrl, ioe);
		}

		return fragment;
	}
}
