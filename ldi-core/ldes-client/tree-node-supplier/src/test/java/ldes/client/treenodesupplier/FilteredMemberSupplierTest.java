package ldes.client.treenodesupplier;

import ldes.client.treenodesupplier.domain.valueobject.SuppliedMember;
import ldes.client.treenodesupplier.filters.ExactlyOnceFilter;
import ldes.client.treenodesupplier.membersuppliers.FilteredMemberSupplier;
import ldes.client.treenodesupplier.membersuppliers.MemberSupplier;
import ldes.client.treenodesupplier.repository.inmemory.InMemoryMemberIdRepository;
import org.apache.jena.rdf.model.ModelFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FilteredMemberSupplierTest {
	@Mock
	private MemberSupplier memberSupplier;
	private SuppliedMember member;
	private SuppliedMember member2;
	private FilteredMemberSupplier filterMemberSupplier;

	@Nested
	class ExactlyOnceFilterTest {

		@BeforeEach
		void setup() {
			member = new SuppliedMember("id1", ModelFactory.createDefaultModel());
			member2 = new SuppliedMember("id2", ModelFactory.createDefaultModel());

			ExactlyOnceFilter filter = new ExactlyOnceFilter(new InMemoryMemberIdRepository(), false);
			filterMemberSupplier = new FilteredMemberSupplier(memberSupplier, filter);
		}

		@Test
		void when_RepeatedMember_Then_MemberIsSkipped() {
			when(memberSupplier.get()).thenReturn(member, member, member2);

			SuppliedMember actual1 = filterMemberSupplier.get();
			SuppliedMember actual2 = filterMemberSupplier.get();

			assertEquals("id1", actual1.getId());
			assertEquals("id2", actual2.getId());
		}
	}

	@Nested
	class LatestStateFilterTest {
		@BeforeEach
		void setUp() {
//			final LatestStateFilter latestStateFilter =
//			filterMemberSupplier = new FilteredMemberSupplier()
		}
	}

}