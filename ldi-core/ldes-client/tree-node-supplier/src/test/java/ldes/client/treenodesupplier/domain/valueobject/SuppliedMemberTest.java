package ldes.client.treenodesupplier.domain.valueobject;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class SuppliedMemberTest {

	@Test
	void when_SuppliedMemberIsCreated_ModelIsDeepCopied() {
		Model model = ModelFactory.createDefaultModel();
		SuppliedMember suppliedMember = new SuppliedMember("id", model);

		assertThat(suppliedMember.getModel())
				.isNotNull()
				.matches(Model::isEmpty);
	}

}