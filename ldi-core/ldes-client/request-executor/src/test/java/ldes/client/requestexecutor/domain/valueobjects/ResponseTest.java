package ldes.client.requestexecutor.domain.valueobjects;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class ResponseTest {

	@Test
	void hasStatus() {
		Response response = new Response(List.of(), 200, null);
		assertTrue(response.hasStatus(200));
		assertFalse(response.hasStatus(100));
	}

}