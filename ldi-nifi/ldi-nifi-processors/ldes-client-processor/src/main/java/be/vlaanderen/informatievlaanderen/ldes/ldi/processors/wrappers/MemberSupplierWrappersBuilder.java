package be.vlaanderen.informatievlaanderen.ldes.ldi.processors.wrappers;

import ldes.client.eventstreamproperties.valueobjects.EventStreamProperties;
import ldes.client.treenodesupplier.domain.services.MemberSupplierWrappers;
import org.apache.nifi.processor.ProcessContext;

import java.util.List;

public class MemberSupplierWrappersBuilder implements MemberSupplierWrappers.Builder {
	private ProcessContext context;
	private EventStreamProperties eventStreamProperties;

	public MemberSupplierWrappersBuilder withContext(ProcessContext context) {
		this.context = context;
		return this;
	}

	public MemberSupplierWrappersBuilder withEventStreamProperties(EventStreamProperties eventStreamProperties) {
		this.eventStreamProperties = eventStreamProperties;
		return this;
	}

	@Override
	public MemberSupplierWrappers build() {
		return new MemberSupplierWrappers(List.of(
				new ExactlyOnceMemberSupplierWrapper(context),
				new LatestStateMemberSupplierWrapper(context, eventStreamProperties),
				new VersionMaterialisedMemberSupplierWrapper(context, eventStreamProperties.getVersionOfPath())
		));
	}
}
