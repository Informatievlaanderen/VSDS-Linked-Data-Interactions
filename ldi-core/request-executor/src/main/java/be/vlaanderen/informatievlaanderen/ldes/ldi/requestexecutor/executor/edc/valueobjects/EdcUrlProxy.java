package be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.executor.edc.valueobjects;

/**
 * <p>
 * Makes it possible to proxy a part of the url.
 * For example when you are following an LDES at "www.example.com"
 * through a consumer connector "www.my-fictional-connector.com".
 * </p>
 * <p>
 * When the original LDES has a relation to follow to
 * "www.example.com/by-location", this proxy will translate this to
 * "www.my-fictional-connector.com/by-location".
 * </p>
 * <p>
 * In this case "urlToReplace" is "www.example.com" and "replacementUrl" is
 * "www.my-fictional-connector.com".
 * This component is required until the LDES Server and Client support relative
 * urls.
 * </p>
 */
public class EdcUrlProxy {

	private final String urlToReplace;
	private final String replacementUrl;

	public EdcUrlProxy(String urlToReplace, String replacementUrl) {
		this.urlToReplace = urlToReplace;
		this.replacementUrl = replacementUrl;
	}

	public String proxy(String url) {
		return url.replace(urlToReplace, replacementUrl);
	}

}
