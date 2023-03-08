package ldes.client.treenodesupplier;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(features = { "classpath:features/member-supplier.feature" }, glue = {
		"ldes.client.treenodesupplier" })
public class MemberSupplierIT {
}
