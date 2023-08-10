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

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class MaterialiserTest {
	private RDFConnectionRemoteBuilder mockBuilder;
	private RDFConnectionRemote mockConnection;
	private Materialiser materialiser;

	@BeforeEach
	void setUp() throws IOException {
		mockBuilder = mock(RDFConnectionRemoteBuilder.class);
		mockConnection = mock(RDFConnectionRemote.class);
		materialiser = new Materialiser("", "");
		when(mockBuilder.build()).thenReturn(mockConnection);
		when(mockConnection.fetch("http://somewhere/MattJones/"))
				.thenReturn(readModelFromFile("src/test/resources/id1.nq"));
		when(mockConnection.fetch("http://somewhere/DickJones/"))
				.thenReturn(readModelFromFile("src/test/resources/id4.nq"));
		when(mockConnection.fetch("http://somewhere/SarahJones/"))
				.thenReturn(readModelFromFile("src/test/resources/id2.nq"));
		when(mockConnection.fetch(matches("^(?!(?:http)).*$")))
				.thenReturn(readModelFromFile("src/test/resources/id3.nq"));

		materialiser.builder = mockBuilder;
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

		verify(mockConnection).fetch("http://somewhere/DickJones/");
		verify(mockConnection).fetch("http://somewhere/SarahJones/");
		verify(mockConnection).delete("http://somewhere/DickJones/");
		verify(mockConnection).delete("http://somewhere/SarahJones/");
		verify(mockConnection).load(model);
		verify(mockConnection).commit();
		verify(mockConnection).close();
		verifyNoMoreInteractions(mockConnection);
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
