package ldes.client.treenodesupplier;

import org.junit.jupiter.api.Test;

class MemberSupplierTest {

	@Test
	void test() {
		MemberSupplier memberSupplier = new MemberSupplier(
				"https://private-api.gipod.beta-vlaanderen.be/api/v1/ldes/mobility-hindrances?generatedAtTime=2020-12-28T09:36:09.72Z");

		memberSupplier.get();

	}

}