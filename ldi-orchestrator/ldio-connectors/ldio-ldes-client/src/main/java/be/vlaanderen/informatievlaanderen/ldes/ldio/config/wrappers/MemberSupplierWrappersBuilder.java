package be.vlaanderen.informatievlaanderen.ldes.ldio.config.wrappers;

import be.vlaanderen.informatievlaanderen.ldes.ldio.LdioLdesClientProperties;
import ldes.client.eventstreamproperties.valueobjects.EventStreamProperties;
import ldes.client.treenodesupplier.domain.services.MemberSupplierWrappers;

import java.util.List;

public class MemberSupplierWrappersBuilder implements MemberSupplierWrappers.Builder {
		private EventStreamProperties eventStreamProperties;
		private LdioLdesClientProperties ldioLdesClientProperties;

		public MemberSupplierWrappersBuilder withEventStreamProperties(EventStreamProperties eventStreamProperties) {
			this.eventStreamProperties = eventStreamProperties;
			return this;
		}

		public MemberSupplierWrappersBuilder withLdioLdesClientProperties(LdioLdesClientProperties ldioLdesClientProperties) {
			this.ldioLdesClientProperties = ldioLdesClientProperties;
			return this;
		}

		public MemberSupplierWrappers build() {
			return new MemberSupplierWrappers(List.of(
					new ExactlyOnceMemberSupplierWrapper(ldioLdesClientProperties),
					new LatestStateMemberSupplierWrapper(eventStreamProperties, ldioLdesClientProperties),
					new VersionMaterialisedMemberSupplierWrapper(eventStreamProperties, ldioLdesClientProperties)
			));
		}
}
