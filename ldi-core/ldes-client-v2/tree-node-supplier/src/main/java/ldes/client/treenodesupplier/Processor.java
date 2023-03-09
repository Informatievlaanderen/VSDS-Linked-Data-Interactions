package ldes.client.treenodesupplier;

import ldes.client.treenodefetcher.TreeNodeFetcher;
import ldes.client.treenodefetcher.domain.entities.TreeNode;
import ldes.client.treenodefetcher.domain.valueobjects.TreeNodeRequest;
import ldes.client.treenodesupplier.domain.entities.MemberRecord;
import ldes.client.treenodesupplier.domain.entities.TreeNodeRecord;
import ldes.client.treenodesupplier.domain.valueobject.TreeNodeStatus;
import ldes.client.treenodesupplier.repository.MemberRepository;
import ldes.client.treenodesupplier.repository.TreeNodeRecordRepository;
import org.apache.jena.riot.Lang;

import java.util.Optional;

class Processor {

	// TODO extend with etag to create cached request

	private final TreeNodeRecordRepository treeNodeRecordRepository;
	private final MemberRepository memberRepository;
	private final TreeNodeFetcher treeNodeFetcher;
	private final boolean keepstate;

	public Processor(TreeNodeRecord startingNode,
			TreeNodeRecordRepository treeNodeRecordRepository,
			MemberRepository memberRepository,
			TreeNodeFetcher treeNodeFetcher, boolean keepstate) {
		this.treeNodeRecordRepository = treeNodeRecordRepository;
		this.memberRepository = memberRepository;
		this.treeNodeFetcher = treeNodeFetcher;
		this.keepstate = keepstate;
		this.treeNodeRecordRepository.saveTreeNodeRecord(startingNode);
		Runtime.getRuntime().addShutdownHook(new Thread(this::destoryState));
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
				.map(treeMember -> new MemberRecord(treeMember.getMemberId(), treeMember.getModel()))
				.filter(member -> !memberRepository.isProcessed(member))
				.forEach(memberRepository::saveTreeMember);

	}

	public MemberRecord getMember() {
		Optional<MemberRecord> unprocessedTreeMember = memberRepository.getUnprocessedTreeMember();
		while (unprocessedTreeMember.isEmpty()) {
			processedTreeNode();
			unprocessedTreeMember = memberRepository.getUnprocessedTreeMember();
		}
		MemberRecord treeMember = unprocessedTreeMember.get();
		treeMember.processedMemberRecord();
		memberRepository.saveTreeMember(treeMember);
		return treeMember;
	}

	public void destoryState() {
		if (!keepstate) {
			memberRepository.destroyState();
			treeNodeRecordRepository.destroyState();
		}
	}
}
