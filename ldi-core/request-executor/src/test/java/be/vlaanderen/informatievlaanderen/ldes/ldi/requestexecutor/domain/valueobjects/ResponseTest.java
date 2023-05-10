package be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.domain.valueobjects;

import org.apache.http.message.BasicHeader;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ResponseTest {

	@Test
	void getValueOfHeader() {
		Response response = new Response(null, List.of(new BasicHeader("location", "value")), 302, null);
		assertEquals("value", response.getFirstHeaderValue("LOCATION").orElseThrow());
		assertEquals("value", response.getFirstHeaderValue("lOcAtIon").orElseThrow());
		assertEquals("value", response.getFirstHeaderValue("location").orElseThrow());
	}

	// public static void main(String[] args) {
	// Response response1 = new Response(new
	// Request("https://example.com/blog/article", RequestHeaders.empty()), List.of(
	// new BasicHeader(HttpHeaders.LOCATION, "/chat")
	// ), 302, null);
	// Response response2 = new Response(new
	// Request("https://example.com/blog/article", RequestHeaders.empty()), List.of(
	// new BasicHeader(HttpHeaders.LOCATION, "https://example.org/blog/chat")
	// ), 302, null);
	// Response response3 = new Response(new
	// Request("https://example.com/blog/article", RequestHeaders.empty()), List.of(
	// new BasicHeader(HttpHeaders.LOCATION, "chat")
	// ), 302, null);
	//
	// System.out.println(getRedirect(response1));
	// System.out.println(getRedirect(response2));
	// System.out.println(getRedirect(response3));
	// }

}