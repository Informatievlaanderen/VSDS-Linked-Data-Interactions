package be.vlaanderen.informatievlaanderen.ldes.ldio;

import be.vlaanderen.informatievlaanderen.ldes.ldi.services.ComponentExecutor;
import be.vlaanderen.informatievlaanderen.ldes.ldi.types.LdiInput;
import be.vlaanderen.informatievlaanderen.ldes.ldio.config.LdioArchiveFileInAutoConfig;
import be.vlaanderen.informatievlaanderen.ldes.ldio.valueobjects.ComponentProperties;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import org.apache.jena.rdf.model.Model;

import java.util.Map;

import static be.vlaanderen.informatievlaanderen.ldes.ldio.config.LdioArchiveFileInAutoConfig.ARCHIVE_ROOT_DIR_PROP;
import static org.mockito.Mockito.mock;

public class ArchiveFileInITSteps {

    private ComponentExecutor componentExecutor;

    private String archiveRootDir;
    private LdiInput archiveFileIn;
    private Model model;
    private Model otherModel;

    @Given("I create an archive-file-in component with archive-dir {string} and no source-format")
    public void iCreateAnArchiveFileInComponentWithArchiveDir(String archiveDir) {
        componentExecutor = new ComponentExecutor() {
            @Override
            public void transformLinkedData(Model linkedDataModel) {
                int size = linkedDataModel.listStatements().toList().size();
                System.out.println("hej there, I have statemnt count: " + size);
            }
        };
        var props = new ComponentProperties(Map.of(ARCHIVE_ROOT_DIR_PROP, archiveDir));
        var ldioInputConfigurator = new LdioArchiveFileInAutoConfig().ldiArchiveFileInConfigurator();
        archiveFileIn = (LdiInput) ldioInputConfigurator.configure(null, componentExecutor, props);
    }


    @Then("I print all")
    public void iPrintAll() {

    }
}
