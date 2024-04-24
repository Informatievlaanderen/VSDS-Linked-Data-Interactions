package ldes.client.treenodesupplier.filters;

import be.vlaanderen.informatievlaanderen.ldes.ldi.extractor.PropertyPathExtractor;
import ldes.client.treenodesupplier.domain.entities.MemberVersionRecord;
import ldes.client.treenodesupplier.domain.valueobject.SuppliedMember;
import ldes.client.treenodesupplier.repository.MemberVersionRepository;
import org.apache.jena.rdf.model.Model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class LatestStateFilter implements MemberFilter {
	private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssXXX");

	private final MemberVersionRepository memberVersionRepository;
	private final boolean keepState;
	private final PropertyPathExtractor timestampExtractor;
	private final PropertyPathExtractor versionOfExtractor;


	public LatestStateFilter(MemberVersionRepository memberVersionRepository, boolean keepState, PropertyPathExtractor timestampExtractor, PropertyPathExtractor versionOfExtractor) {
		this.memberVersionRepository = memberVersionRepository;
		this.keepState = keepState;
		this.timestampExtractor = timestampExtractor;
		this.versionOfExtractor = versionOfExtractor;
	}

	@Override
	public boolean isAllowed(SuppliedMember member) {
		final String versionOf = versionOfExtractor.getProperties(member.getModel()).stream()
				.findFirst()
				.map(node -> node.asResource().getURI())
				.orElseThrow(() -> new IllegalStateException("Could not find versionOf in supplied member"));
		final LocalDateTime timestamp = extractTimestamp(member.getModel());
		return memberVersionRepository.isVersionAfterTimestamp(new MemberVersionRecord(versionOf, timestamp));
	}

	@Override
	public void saveAllowedMember(SuppliedMember member) {
		final String versionOf = versionOfExtractor.getProperties(member.getModel()).stream()
				.findFirst()
				.map(node -> node.asResource().getURI())
				.orElseThrow(() -> new IllegalStateException("Could not find versionOf in supplied member"));
		final LocalDateTime timestamp = extractTimestamp(member.getModel());
		memberVersionRepository.addMemberVersion(new MemberVersionRecord(versionOf, timestamp));
	}

	@Override
	public void destroyState() {
		if(!keepState) {
			memberVersionRepository.destroyState();
		}
	}

	private LocalDateTime extractTimestamp(Model model) {
		return timestampExtractor.getProperties(model).stream()
				.findFirst()
				.map(node -> node.asLiteral().getString())
				.map(timestamp -> LocalDateTime.from(formatter.parse(timestamp)))
				.orElseThrow(() -> new IllegalStateException("Could not find timestamp in supplied member"));
	}
}
