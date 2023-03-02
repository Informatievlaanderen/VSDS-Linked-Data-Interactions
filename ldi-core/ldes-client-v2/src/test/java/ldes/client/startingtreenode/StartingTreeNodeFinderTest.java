package ldes.client.startingtreenode;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import ldes.client.startingtreenode.domain.valueobjects.Endpoint;
import ldes.client.startingtreenode.domain.valueobjects.TreeNode;
import org.apache.jena.riot.Lang;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;

import java.util.Optional;

@RunWith(Cucumber.class)
@CucumberOptions(
        features = { "classpath:features/starting-point-finder.feature" },
        glue = {"ldes.client.startingtreenode" })
public class StartingTreeNodeFinderTest {}