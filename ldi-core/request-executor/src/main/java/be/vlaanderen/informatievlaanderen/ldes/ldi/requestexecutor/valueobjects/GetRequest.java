package be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.valueobjects;

public class GetRequest extends Request {

	public static final String METHOD_NAME = "GET";

	public GetRequest(String url, RequestHeaders requestHeaders) {
		super(url, requestHeaders);
	}

	@Override
	public String getMethod() {
		return METHOD_NAME;
	}

	@Override
	public Request with(String url) {
		return new GetRequest(url, requestHeaders);
	}

	@Override
	public Request with(RequestHeaders requestHeaders) {
		return new GetRequest(url, requestHeaders);
	}

}
