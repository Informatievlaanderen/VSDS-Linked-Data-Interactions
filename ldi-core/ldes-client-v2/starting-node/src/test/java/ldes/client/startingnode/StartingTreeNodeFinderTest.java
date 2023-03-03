package ldes.client.startingnode;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(features = { "classpath:features/starting-point-finder.feature" }, glue = {
		"ldes.client.startingtreenode" })
public class StartingTreeNodeFinderTest {
}
