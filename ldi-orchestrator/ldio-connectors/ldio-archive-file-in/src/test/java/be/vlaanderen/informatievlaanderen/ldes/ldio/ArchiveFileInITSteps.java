package be.vlaanderen.informatievlaanderen.ldes.ldio;

import be.vlaanderen.informatievlaanderen.ldes.ldi.services.ComponentExecutor;
import be.vlaanderen.informatievlaanderen.ldes.ldio.config.LdioArchiveFileInAutoConfig;
import be.vlaanderen.informatievlaanderen.ldes.ldio.valueobjects.ComponentProperties;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.RDFParser;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static be.vlaanderen.informatievlaanderen.ldes.ldio.config.LdioArchiveFileInAutoConfig.ARCHIVE_ROOT_DIR_PROP;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ArchiveFileInITSteps {

    private List<Model> members;

    @Given("I start an archive-file-in component with archive-dir {string} and no source-format")
    public void iCreateAnArchiveFileInComponentWithArchiveDir(String archiveDir) {
        members = new ArrayList<>();
        ComponentExecutor componentExecutor = linkedDataModel -> members.add(linkedDataModel);
        var props = new ComponentProperties(Map.of(ARCHIVE_ROOT_DIR_PROP, archiveDir));
        var ldioInputConfigurator = new LdioArchiveFileInAutoConfig().ldiArchiveFileInConfigurator();
        ldioInputConfigurator.configure(null, componentExecutor, props);
    }

    @Then("All the members from the archive are passed to the pipeline in lexical order")
    public void allTheMembersFromTheArchiveArePassedToThePipelineInLexicalOrder() {
        assertEquals(11, members.size());
        assertModel(0, "archive/2022/04/19/2022-04-19-12-12-49-470000000.nq");
        assertModel(1, "archive/2022/04/19/2022-04-19-19-50-42-003000000.nq");
        assertModel(2, "archive/2022/04/20/2022-04-20-17-20-52-730000000.nq");
        assertModel(3, "archive/2022/05/06/2022-05-06-06-56-41-407000000-1.nq");
        assertModel(4, "archive/2022/05/06/2022-05-06-06-56-41-407000000.nq");
        assertModel(5, "archive/2022/05/10/2022-05-10-06-17-02-690000000-1.nq");
        assertModel(6, "archive/2022/05/10/2022-05-10-06-17-02-690000000-2.nq");
        assertModel(7, "archive/2022/05/10/2022-05-10-06-17-02-690000000.nq");
        assertModel(8, "archive/2022/05/10/2022-05-10-06-18-03-383000000.nq");
        assertModel(9, "archive/2022/05/10/2022-05-10-07-01-37-783000000.nq");
        assertModel(10, "archive/2023/05/09/2023-05-09-08-17-18-567000000.nq");
    }

    private void assertModel(int memberIndex, String filePath) {
        Model actualModel = RDFParser.source(filePath).toModel();
        Model resultModel = members.get(memberIndex);
        assertTrue(actualModel.isIsomorphicWith(resultModel));
    }

}
