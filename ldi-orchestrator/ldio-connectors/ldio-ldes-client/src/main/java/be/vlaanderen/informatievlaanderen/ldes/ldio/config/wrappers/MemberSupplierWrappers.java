package be.vlaanderen.informatievlaanderen.ldes.ldio.config.wrappers;

import be.vlaanderen.informatievlaanderen.ldes.ldio.LdioLdesClientProperties;
import ldes.client.eventstreamproperties.valueobjects.EventStreamProperties;
import ldes.client.treenodesupplier.membersuppliers.MemberSupplier;

import java.util.List;

public class MemberSupplierWrappers {
	private final List<MemberSupplierWrapper> wrappers;

	public MemberSupplierWrappers(List<MemberSupplierWrapper> wrappers) {
		this.wrappers = wrappers;
	}

	public MemberSupplier wrapMemberSupplier(MemberSupplier memberSupplier) {
		for(MemberSupplierWrapper wrapper : wrappers) {
			memberSupplier = wrapper.wrapMemberSupplier(memberSupplier);
		}
		return memberSupplier;
	}

	public static class Builder {
		private EventStreamProperties eventStreamProperties;
		private LdioLdesClientProperties ldioLdesClientProperties;

		public Builder withEventStreamProperties(EventStreamProperties eventStreamProperties) {
			this.eventStreamProperties = eventStreamProperties;
			return this;
		}

		public Builder withLdioLdesClientProperties(LdioLdesClientProperties ldioLdesClientProperties) {
			this.ldioLdesClientProperties = ldioLdesClientProperties;
			return this;
		}

		public MemberSupplierWrappers build() {
			return new MemberSupplierWrappers(List.of(
					new ExactlyOnceMemberSupplierWrapper(ldioLdesClientProperties),
					new LatestStateMemberSupplierWrapper(eventStreamProperties, ldioLdesClientProperties),
					new VersionMaterialiserMemberSupplierWrapper(eventStreamProperties, ldioLdesClientProperties)
			));
		}

	}
}
