package ldes.client.treenodesupplier;

import ldes.client.treenodesupplier.domain.valueobject.EndOfLdesException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;

class MemberSupplierTest {

	@Test
	void ShouldThrowEndOfLdesException_whenThisExceptionIsThrownWithinTheExecutorService() {
		TreeNodeProcessor treeNodeProcessor = mock(TreeNodeProcessor.class);
		doThrow(EndOfLdesException.class).when(treeNodeProcessor).getMember();
		MemberSupplier memberSupplier = new MemberSupplierImpl(treeNodeProcessor, false);

		assertThrows(EndOfLdesException.class, memberSupplier::get);
	}
}