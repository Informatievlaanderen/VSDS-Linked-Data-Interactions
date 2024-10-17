package be.vlaanderen.informatievlaanderen.ldes.ldi.processors.wrappers;

import be.vlaanderen.informatievlaanderen.ldes.ldi.VersionMaterialiser;
import be.vlaanderen.informatievlaanderen.ldes.ldi.processors.config.LdesProcessorProperties;
import ldes.client.treenodesupplier.domain.services.MemberSupplierWrapper;
import ldes.client.treenodesupplier.membersuppliers.MemberSupplier;
import ldes.client.treenodesupplier.membersuppliers.VersionMaterialisedMemberSupplier;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.nifi.processor.ProcessContext;

public class VersionMaterialisedMemberSupplierWrapper extends MemberSupplierWrapper {
	private final ProcessContext context;
	private final String versionOfPath;

	public VersionMaterialisedMemberSupplierWrapper(ProcessContext context, String versionOfPath) {
		this.context = context;
		this.versionOfPath = versionOfPath;
	}

	@Override
	protected boolean shouldBeWrapped() {
		return LdesProcessorProperties.useVersionMaterialisation(context);
	}

	@Override
	protected MemberSupplier createWrappedMemberSupplier(MemberSupplier memberSupplier) {
		return new VersionMaterialisedMemberSupplier(memberSupplier, createVersionMaterialiser());
	}

	private VersionMaterialiser createVersionMaterialiser() {
		final Property versionOfPredicate = ResourceFactory.createProperty(versionOfPath);
		final boolean restrictToMembers = LdesProcessorProperties.restrictToMembers(context);
		return new VersionMaterialiser(versionOfPredicate, restrictToMembers);
	}
}
