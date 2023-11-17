package be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.valueobjects;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ResponseTest {

	@Test
	void test_isSuccess() {
		assertFalse(new Response(null, List.of(), 199, null).isSuccess());
		assertTrue(new Response(null, List.of(), 200, null).isSuccess());
		assertTrue(new Response(null, List.of(), 201, null).isSuccess());
		assertFalse(new Response(null, List.of(), 300, null).isSuccess());
		assertFalse(new Response(null, List.of(), 400, null).isSuccess());
		assertFalse(new Response(null, List.of(), 500, null).isSuccess());
	}

	@Test
	void isFobidden() {
		assertFalse(new Response(null, List.of(), 200, null).isFobidden());
		assertFalse(new Response(null, List.of(), 400, null).isFobidden());
		assertTrue(new Response(null, List.of(), 403, null).isFobidden());
		assertFalse(new Response(null, List.of(), 500, null).isFobidden());
	}

}