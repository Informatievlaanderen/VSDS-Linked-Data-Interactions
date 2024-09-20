package be.vlaanderen.informatievlaanderen.ldes.ldi;

import be.vlaanderen.informatievlaanderen.ldes.ldi.repositories.HashedStateMemberRepository;
import be.vlaanderen.informatievlaanderen.ldes.ldi.repositories.inmemory.InMemoryHashedStateMemberRepository;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.RDFParser;
import org.junit.After;
import org.testcontainers.containers.PostgreSQLContainer;

import static org.assertj.core.api.Assertions.assertThat;

public class ChangeDetectionFilterSteps {
	private static final String DATABASE_INSTANCE_NAME = "change-detection-instance-name";
	private ChangeDetectionFilter changeDetectionFilter;
	private Model filteredModel;
	private PostgreSQLContainer<?> postgreSQLContainer;

	@After
	public void teardown() {
		if (postgreSQLContainer != null) {
			postgreSQLContainer.stop();
			postgreSQLContainer = null;
		}
	}

	@Given("A ChangeDetectionFilter with state persistence strategy MEMORY")
	public void aChangeDetectionFilterWithStatePersistenceStrategyMEMORY() {
		HashedStateMemberRepository hashedStateMemberRepository = new InMemoryHashedStateMemberRepository();
		changeDetectionFilter = new ChangeDetectionFilter(hashedStateMemberRepository, false);
	}

	@Then("The filtered member is not empty")
	public void theFilteredMemberIsNotEmpty() {
		assertThat(filteredModel).matches(model -> !model.isEmpty());
	}

	@Then("The filtered member is empty")
	public void theFilteredMemberIsEmpty() {
		assertThat(filteredModel).matches(Model::isEmpty);
	}


	@When("I receive member {string}")
	public void iReceiveMember(String fileName) {
		Model model = RDFParser.source(fileName).toModel();
		filteredModel = changeDetectionFilter.transform(model);
	}

	@Then("The filtered member is isomorphic with {string}")
	public void theFilteredMemberIsIsomorphicWith(String fileName) {
		Model model = RDFParser.source(fileName).toModel();
		assertThat(filteredModel).matches(model::isIsomorphicWith);
	}

	@Then("The filter is destroyed")
	public void theFilterIsDestroyed() {
		changeDetectionFilter.destroyState();
	}
}
