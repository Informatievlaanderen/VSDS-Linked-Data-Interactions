package ldes.client.requestexecutor;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(features = { "classpath:features/request-processor.feature" }, glue = {
		"ldes.client.requestexecutor" })
public class RequestProcessorIT {
}
