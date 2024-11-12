package be.vlaanderen.informatievlaanderen.ldes.ldi;

import be.vlaanderen.informatievlaanderen.ldes.ldi.exceptions.WriteActionFailedException;
import be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.executor.RequestExecutor;
import be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.valueobjects.PostRequest;
import be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.valueobjects.RequestHeaders;
import be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.valueobjects.Response;
import be.vlaanderen.informatievlaanderen.ldes.ldi.skolemisation.Skolemizer;
import be.vlaanderen.informatievlaanderen.ldes.ldi.valueobjects.SparqlQuery;
import org.apache.jena.rdf.model.Model;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class HttpSparqlOutTest {
	private static final String ENDPOINT = "http://example.com/sparql";
	@Mock
	private SparqlQuery sparqlQuery;
	@Mock
	private Skolemizer skolemizer;
	@Mock
	private RequestExecutor requestExecutor;
	@Mock
	private Model model;
	private HttpSparqlOut httpSparqlOut;

	@BeforeEach
	void setUp() {
		httpSparqlOut = new HttpSparqlOut(ENDPOINT, sparqlQuery, skolemizer, requestExecutor);
	}

	@Test
	void given_EmptyModel_when_Write_then_DoNothing() {
		when(model.isEmpty()).thenReturn(true);

		httpSparqlOut.write(model);

		verifyNoInteractions(sparqlQuery, skolemizer, requestExecutor);
	}

	@Test
	void test_Write() {
		Response response = mock();
		when(model.isEmpty()).thenReturn(false);
		when(skolemizer.skolemize(model)).thenReturn(model);
		when(sparqlQuery.getQueryForModel(model)).thenReturn("my-sparql-query");
		when(requestExecutor.execute(any())).thenReturn(response);
		when(response.isSuccess()).thenReturn(true);

		httpSparqlOut.write(model);

		verify(sparqlQuery).getQueryForModel(model);
		verify(skolemizer).skolemize(model);
		verify(requestExecutor).execute(any());
	}

	@Test
	void test_Write_when_BadRequestIsReturned() {
		when(model.isEmpty()).thenReturn(false);
		when(skolemizer.skolemize(model)).thenReturn(model);
		when(sparqlQuery.getQueryForModel(model)).thenReturn("my-sparql-query");
		when(requestExecutor.execute(any())).thenReturn(new Response(new PostRequest(ENDPOINT, new RequestHeaders(List.of()), "my-sparql-query"), List.of(), 400, "error"));

		assertThatThrownBy(() -> httpSparqlOut.write(model))
				.isExactlyInstanceOf(WriteActionFailedException.class)
				.hasMessage("Failed to post model. The request url was %s. The http response obtained from the server has code %s and body \"%s\".".formatted(ENDPOINT, 400, "error"));
		verify(sparqlQuery).getQueryForModel(model);
		verify(skolemizer).skolemize(model);
		verify(requestExecutor).execute(any());
	}
}