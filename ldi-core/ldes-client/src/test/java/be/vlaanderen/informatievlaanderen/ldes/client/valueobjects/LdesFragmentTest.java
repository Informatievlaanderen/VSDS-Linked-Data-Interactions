package be.vlaanderen.informatievlaanderen.ldes.client.valueobjects;

import be.vlaanderen.informatievlaanderen.ldes.client.exceptions.LdesMemberNotFoundException;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LdesFragmentTest {

	private static final String idMember1 = "1";
	private static final String idMember2 = "2";
	private static final String idNonExistantMember = "-1";
	private static final Model helperModel = ModelFactory.createDefaultModel();

	@Test
	void whenMemberIsNotFound_thenLdesMemberNotFoundExceptionIsThrown() {
		LdesFragment fragment = new LdesFragment();

		fragment.addMember(new LdesMember(idMember1, null));
		fragment.addMember(new LdesMember(idMember2, null));

		// To be sure
		assertNotNull(fragment.getMember(idMember1));
		assertNotNull(fragment.getMember(idMember2));

		assertThrows(LdesMemberNotFoundException.class, () -> fragment.getMember(idNonExistantMember));
	}

	@Test
	void when_getPropertyValue_thenReturnIfExists() {
		LdesFragment ldesFragment = new LdesFragment();
		ldesFragment.getModel().add(helperModel.createLiteralStatement(helperModel.createResource(),
				LdesFragment.LDES_TIMESTAMP_PATH,
				helperModel.createResource("http://www.w3.org/ns/prov#generatedAtTime")));
		ldesFragment.getModel().add(helperModel.createLiteralStatement(helperModel.createResource(),
				LdesFragment.LDES_VERSION_OF, helperModel.createResource("http://purl.org/dc/terms/isVersionOf")));
		ldesFragment.getModel().add(helperModel.createLiteralStatement(helperModel.createResource(),
				LdesFragment.TREE_SHAPE,
				helperModel.createResource(
						"https://private-api.gipod.test-vlaanderen.be/api/v1/ldes/mobility-hindrances/shape")));

		assertTrue(ldesFragment.getTimestampPath().isPresent());
		assertTrue(ldesFragment.getVersionOfPath().isPresent());
		assertTrue(ldesFragment.getShaclShape().isPresent());

		assertFalse(ldesFragment.getPropertyValue(helperModel.createProperty("NON_EXISTANT")).isPresent());
	}
}
