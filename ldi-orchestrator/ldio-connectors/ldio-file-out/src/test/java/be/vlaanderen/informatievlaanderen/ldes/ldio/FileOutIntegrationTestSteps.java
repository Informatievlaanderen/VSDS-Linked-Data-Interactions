package be.vlaanderen.informatievlaanderen.ldes.ldio;

import be.vlaanderen.informatievlaanderen.ldes.ldi.types.LdiOutput;
import be.vlaanderen.informatievlaanderen.ldes.ldio.config.LdioFileOutAutoConfig;
import be.vlaanderen.informatievlaanderen.ldes.ldio.valueobjects.ComponentProperties;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.RDFParser;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import static be.vlaanderen.informatievlaanderen.ldes.ldio.config.LdioFileOutAutoConfig.ARCHIVE_ROOT_DIR_PROP;
import static be.vlaanderen.informatievlaanderen.ldes.ldio.config.LdioFileOutAutoConfig.TIMESTAMP_PATH_PROP;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class FileOutIntegrationTestSteps {

	private String archiveRootDir;
	private LdiOutput ldiFileOut;
	private Model model;
	private Model otherModel;

	@Given("I an empty archive-dir {string}")
	public void iAnEmptyArchiveDir(String dir) throws IOException {
		archiveRootDir = FilenameUtils.separatorsToSystem(dir);
		FileUtils.deleteDirectory(new File(archiveRootDir));
	}

	@And("I create a file-out-component with the archive-dir and timestampPath {string}")
	public void iCreateAFileOutComponentWithArchiveDirAndTimestampPath(String path) {
		var props = new ComponentProperties(Map.of(ARCHIVE_ROOT_DIR_PROP, archiveRootDir, TIMESTAMP_PATH_PROP, path));
		ldiFileOut = (LdiOutput) new LdioFileOutAutoConfig().ldiFileOutConfigurator().configure(props);
	}

	@And("I have a model defined in {string} containing this timestampPath")
	public void iHaveAModelContainingThisTimestampPath(String fileUri) {
		model = RDFParser.source(fileUri).toModel();
	}

	@When("I have another model defined in {string}")
	public void iHaveAnotherModelDefinedIn(String fileUri) {
		otherModel = RDFParser.source(fileUri).toModel();
	}

	@When("I send the model to the file-out-component")
	public void iSendTheModelToTheFileOutComponent() {
		ldiFileOut.accept(model);
	}

	@Then("The model is written to {string}")
	public void theModelIsWrittenTo(String expectedFilePath) {
		Model actualModel = RDFParser.source(FilenameUtils.separatorsToSystem(expectedFilePath)).toModel();
		assertTrue(model.isIsomorphicWith(actualModel));
	}

	@Then("The other model is written to {string}")
	public void theOtherModelIsWrittenTo(String expectedFilePath) {
		Model actualModel = RDFParser.source(FilenameUtils.separatorsToSystem(expectedFilePath)).toModel();
		assertTrue(otherModel.isIsomorphicWith(actualModel));
	}
}
