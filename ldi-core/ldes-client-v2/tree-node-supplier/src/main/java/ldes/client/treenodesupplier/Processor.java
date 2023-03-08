package ldes.client.treenodesupplier;

import ldes.client.treenodefetcher.TreeNodeFetcher;
import ldes.client.treenodefetcher.domain.entities.TreeMember;
import ldes.client.treenodefetcher.domain.entities.TreeNode;
import ldes.client.treenodefetcher.domain.valueobjects.TreeNodeRequest;
import ldes.client.treenodesupplier.domain.entities.TreeNodeRecord;
import ldes.client.treenodesupplier.domain.valueobject.TreeNodeStatus;
import org.apache.jena.riot.Lang;

import java.util.Optional;

class Processor {

	// TODO extend with SQLITE implementations for member and treenode repository
	// TODO extend with mutable and immutable fragments (and max-age)
	// TODO extend with etag to create cached request

	private final TreeNodeRecordRepository treeNodeRecordRepository;
	private final MemberRepository memberRepository;
	private final TreeNodeFetcher treeNodeFetcher;

	public Processor(TreeNodeRecord startingNode,
			TreeNodeRecordRepository treeNodeRecordRepository,
			MemberRepository memberRepository,
			TreeNodeFetcher treeNodeFetcher) {
		this.treeNodeRecordRepository = treeNodeRecordRepository;
		this.memberRepository = memberRepository;
		this.treeNodeFetcher = treeNodeFetcher;
		this.treeNodeRecordRepository.saveTreeNodeRecord(startingNode);
	}

	private void processedTreeNode() {
		TreeNodeRecord treeNodeRecord = treeNodeRecordRepository
				.getOneTreeNodeRecordWithStatus(TreeNodeStatus.NOT_VISITED).orElseGet(
						() -> treeNodeRecordRepository.getOneTreeNodeRecordWithStatus(TreeNodeStatus.MUTABLE_AND_ACTIVE)
								.orElseThrow(() -> new RuntimeException(
										"No fragments to mutable or new fragments to process -> LDES ends.")));
		TreeNode treeNodeResponse = treeNodeFetcher
				.fetchTreeNode(new TreeNodeRequest(treeNodeRecord.getTreeNodeUrl(), Lang.JSONLD));
		treeNodeRecord.updateStatus(treeNodeResponse.getMutabilityStatus());
		treeNodeRecordRepository.saveTreeNodeRecord(treeNodeRecord);
		treeNodeResponse.getRelations()
				.stream()
				.filter(treeNodeId -> !treeNodeRecordRepository.existsById(treeNodeId))
				.map(TreeNodeRecord::new)
				.forEach(treeNodeRecordRepository::saveTreeNodeRecord);
		treeNodeResponse.getMembers()
				.stream()
				.filter(member -> !memberRepository.isProcessed(member))
				.forEach(memberRepository::addUnprocessedTreeMember);

	}

	public TreeMember getMember() {
		Optional<TreeMember> unprocessedTreeMember = memberRepository.getUnprocessedTreeMember();
		while (unprocessedTreeMember.isEmpty()) {
			processedTreeNode();
			unprocessedTreeMember = memberRepository.getUnprocessedTreeMember();
		}
		TreeMember treeMember = unprocessedTreeMember.get();
		memberRepository.addProcessedTreeMember(treeMember);
		return treeMember;
	}
}
