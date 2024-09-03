package ldes.client.treenodesupplier.filters;

import be.vlaanderen.informatievlaanderen.ldes.ldi.timestampextractor.TimestampExtractor;
import be.vlaanderen.informatievlaanderen.ldes.ldi.timestampextractor.TimestampFromPathExtractor;
import ldes.client.treenodesupplier.domain.entities.MemberVersionRecord;
import ldes.client.treenodesupplier.domain.valueobject.SuppliedMember;
import ldes.client.treenodesupplier.repository.MemberVersionRepository;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.ResourceFactory;

import java.time.LocalDateTime;

import static org.apache.jena.rdf.model.ResourceFactory.createProperty;

/**
 * Filter that makes sure that a member represents only the latest state
 */
public class LatestStateFilter implements MemberFilter {

	private final MemberVersionRepository memberVersionRepository;
	private final boolean keepState;
	private final TimestampExtractor timestampExtractor;
	private final String versionOfPath;


	/**
	 * @param memberVersionRepository repository that keeps track of the latest processed versions of the members
	 * @param keepState               if the state must be kept, or can be reset on restart of the filter
	 * @param timestampPath           URI of the predicate that contains the timestamp
	 * @param versionOfPath           URI of the predicate that contains the version-of
	 */
	public LatestStateFilter(MemberVersionRepository memberVersionRepository, boolean keepState, String timestampPath, String versionOfPath) {
		this.memberVersionRepository = memberVersionRepository;
		this.keepState = keepState;
		this.timestampExtractor = new TimestampFromPathExtractor(createProperty(timestampPath));
		this.versionOfPath = versionOfPath;
	}


	@Override
	public boolean saveMemberIfAllowed(SuppliedMember member) {
		final String versionOf = extractVersionOf(member);
		final LocalDateTime timestamp = extractTimestampWithSubject(member);
		final boolean isAllowed = memberVersionRepository.isVersionAfterTimestamp(new MemberVersionRecord(versionOf, timestamp));
		if (isAllowed) {
			memberVersionRepository.addMemberVersion(new MemberVersionRecord(versionOf, timestamp));
		}
		return isAllowed;
	}

	/**
	 * Clean up the database when the filter is not required anymore and the state must not be kept
	 */
	@Override
	public void destroyState() {
		if (!keepState) {
			memberVersionRepository.destroyState();
		}
	}

	private LocalDateTime extractTimestampWithSubject(SuppliedMember member) {
		return timestampExtractor.extractTimestampWithSubject(
				ResourceFactory.createProperty(member.getId()),
				member.getModel());
	}

	private String extractVersionOf(SuppliedMember member) {
		return member.getModel()
				.listObjectsOfProperty(ResourceFactory.createProperty(member.getId()), ResourceFactory.createProperty(versionOfPath))
				.filterKeep(RDFNode::isResource)
				.mapWith(rdfNode -> rdfNode.asResource().getURI())
				.nextOptional()
				.orElseThrow(() -> new IllegalStateException("Could not find versionOf in supplied member"));
	}
}
