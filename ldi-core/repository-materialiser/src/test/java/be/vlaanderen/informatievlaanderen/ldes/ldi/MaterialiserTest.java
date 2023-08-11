package be.vlaanderen.informatievlaanderen.ldes.ldi;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdfconnection.RDFConnectionRemote;
import org.apache.jena.rdfconnection.RDFConnectionRemoteBuilder;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFParser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static be.vlaanderen.informatievlaanderen.ldes.ldi.Materialiser.constructRDF4JSparqlEndpoint;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class MaterialiserTest {
	private String person1 = "http://somewhere/MattJones/";
	private String person2 = "http://somewhere/SarahJones/";
	private String person4 = "http://somewhere/DickJones/";
	private RDFConnectionRemoteBuilder mockBuilder;
	private RDFConnectionRemote mockConnection;
	private Materialiser materialiser;

	@BeforeEach
	void setUp() throws IOException {
		mockBuilder = mock(RDFConnectionRemoteBuilder.class);
		mockConnection = mock(RDFConnectionRemote.class);
		materialiser = new Materialiser("", "");
		when(mockBuilder.build()).thenReturn(mockConnection);
		when(mockConnection.fetch(person1))
				.thenReturn(readModelFromFile("src/test/resources/id1.nq"));
		when(mockConnection.fetch(person4))
				.thenReturn(readModelFromFile("src/test/resources/id4.nq"));
		when(mockConnection.fetch(person2))
				.thenReturn(readModelFromFile("src/test/resources/id2.nq"));
		when(mockConnection.fetch(matches("^(?!(?:http)).*$")))
				.thenReturn(readModelFromFile("src/test/resources/id3.nq"));

		materialiser.setConnectionBuilder(mockBuilder);
	}

	@Test
	void when_RepoHasAnonNode_Then_AnonNodeAlsoRemoved() throws IOException {
		Model model = readModelFromFile("src/test/resources/people_data_01.nq");

		materialiser.process(model);

		verify(mockConnection, times(3)).fetch(anyString());
		verify(mockConnection, times(3)).delete(anyString());
		verify(mockConnection).load(model);
		verify(mockConnection).commit();
		verify(mockConnection).close();
		verifyNoMoreInteractions(mockConnection);
	}

	@Test
	void when_ModelHasNodes_Then_NodesInRepoReplaced() throws IOException {
		Model model = readModelFromFile("src/test/resources/people_data_02.nq");

		materialiser.process(model);

		verify(mockConnection).fetch(person4);
		verify(mockConnection).fetch(person2);
		verify(mockConnection).delete(person4);
		verify(mockConnection).delete(person2);
		verify(mockConnection).load(model);
		verify(mockConnection).commit();
		verify(mockConnection).close();
		verifyNoMoreInteractions(mockConnection);
	}

	@Test
	void when_ReceiveHostUrlAndRepoId_Then_ConstructSparqlEndpoint() {
		String hostUrl = "http://host";
		String repoId = "id";

		assertEquals("http://host/repositories/id/statements", constructRDF4JSparqlEndpoint(hostUrl, repoId));
	}

	private Model readModelFromFile(String path) throws IOException {
		String data = Files.readString(Path.of(path));
		Model model = ModelFactory.createDefaultModel();
		RDFParser.fromString(data)
				.lang(Lang.NQ)
				.parse(model);
		return model;
	}
}
