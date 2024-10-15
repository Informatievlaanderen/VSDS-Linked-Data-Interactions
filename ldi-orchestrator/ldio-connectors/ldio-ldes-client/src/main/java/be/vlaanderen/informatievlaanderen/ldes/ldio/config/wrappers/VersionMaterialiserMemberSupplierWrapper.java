package be.vlaanderen.informatievlaanderen.ldes.ldio.config.wrappers;

import be.vlaanderen.informatievlaanderen.ldes.ldi.VersionMaterialiser;
import be.vlaanderen.informatievlaanderen.ldes.ldio.LdioLdesClientProperties;
import ldes.client.eventstreamproperties.valueobjects.EventStreamProperties;
import ldes.client.treenodesupplier.membersuppliers.MemberSupplier;
import ldes.client.treenodesupplier.membersuppliers.VersionMaterialisedMemberSupplier;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.ResourceFactory;

public class VersionMaterialiserMemberSupplierWrapper extends MemberSupplierWrapper {
	private final EventStreamProperties eventStreamProperties;
	private final LdioLdesClientProperties clientProperties;

	public VersionMaterialiserMemberSupplierWrapper(EventStreamProperties eventStreamProperties, LdioLdesClientProperties clientProperties) {
		this.eventStreamProperties = eventStreamProperties;
		this.clientProperties = clientProperties;
	}

	@Override
	public boolean shouldBeWrapped() {
		return clientProperties.isVersionMaterialisationEnabled();
	}

	@Override
	protected MemberSupplier createWrappedMemberSupplier(MemberSupplier memberSupplier) {
		return new VersionMaterialisedMemberSupplier(memberSupplier, createVersionMaterialiser());
	}

	private VersionMaterialiser createVersionMaterialiser() {
		final Property versionOfPath = ResourceFactory.createProperty(eventStreamProperties.getVersionOfPath());
		return new VersionMaterialiser(versionOfPath, false);
	}
}
