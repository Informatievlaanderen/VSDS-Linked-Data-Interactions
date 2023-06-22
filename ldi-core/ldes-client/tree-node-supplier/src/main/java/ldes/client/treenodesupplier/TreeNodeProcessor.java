package ldes.client.treenodesupplier;

import be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.executor.RequestExecutor;
import ldes.client.treenodefetcher.TreeNodeFetcher;
import ldes.client.treenodefetcher.domain.valueobjects.TreeNodeResponse;
import ldes.client.treenodesupplier.domain.entities.MemberRecord;
import ldes.client.treenodesupplier.domain.entities.TreeNodeRecord;
import ldes.client.treenodesupplier.domain.valueobject.*;
import ldes.client.treenodesupplier.repository.MemberRepository;
import ldes.client.treenodesupplier.repository.TreeNodeRecordRepository;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

import static java.lang.Thread.sleep;

public class TreeNodeProcessor {

	private final TreeNodeRecordRepository treeNodeRecordRepository;
	private final MemberRepository memberRepository;
	private final TreeNodeFetcher treeNodeFetcher;
	private final LdesDescription ldesDescription;
	private final RequestExecutor requestExecutor;
	private MemberRecord memberRecord;

	public TreeNodeProcessor(LdesDescription ldesDescription, StatePersistence statePersistence,
			RequestExecutor requestExecutor) {
		this.treeNodeRecordRepository = statePersistence.getTreeNodeRecordRepository();
		this.memberRepository = statePersistence.getMemberRepository();
		this.requestExecutor = requestExecutor;
		this.treeNodeFetcher = new TreeNodeFetcher(requestExecutor);
		this.ldesDescription = ldesDescription;
	}

	private void processTreeNode() {
		TreeNodeRecord treeNodeRecord = treeNodeRecordRepository
				.getOneTreeNodeRecordWithStatus(TreeNodeStatus.NOT_VISITED).orElseGet(
						() -> treeNodeRecordRepository.getOneTreeNodeRecordWithStatus(TreeNodeStatus.MUTABLE_AND_ACTIVE)
								.orElseThrow(() -> new RuntimeException(
										"No fragments to mutable or new fragments to process -> LDES ends.")));
		waitUntilNextVisit(treeNodeRecord);
		TreeNodeResponse treeNodeResponse = treeNodeFetcher
				.fetchTreeNode(ldesDescription.createRequest(treeNodeRecord.getTreeNodeUrl()));
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

	private void waitUntilNextVisit(TreeNodeRecord treeNodeRecord) {
		try {
			LocalDateTime earliestNextVisit = treeNodeRecord.getEarliestNextVisit();
			if (earliestNextVisit.isAfter(LocalDateTime.now())) {
				long sleepDuration = LocalDateTime.now().until(earliestNextVisit, ChronoUnit.MILLIS);
				sleep(sleepDuration);
			}
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}
	}

	public SuppliedMember getMember() {
		savePreviousState();
		if (!treeNodeRecordRepository.containsTreeNodeRecords()) {
			initializeTreeNodeRecordRepository();
		}
		Optional<MemberRecord> unprocessedTreeMember = memberRepository.getUnprocessedTreeMember();
		while (unprocessedTreeMember.isEmpty()) {
			processTreeNode();
			unprocessedTreeMember = memberRepository.getUnprocessedTreeMember();
		}
		MemberRecord treeMember = unprocessedTreeMember.get();
		SuppliedMember suppliedMember = treeMember.createSuppliedMember();
		treeMember.processedMemberRecord();
		memberRecord = treeMember;
		return suppliedMember;
	}

	private void initializeTreeNodeRecordRepository() {
		StartingTreeNode start = new StartingTreeNodeSupplier(requestExecutor)
				.getStart(ldesDescription.getStartingNodeUrl(), ldesDescription.getLang());
		treeNodeRecordRepository.saveTreeNodeRecord(new TreeNodeRecord(start.getStartingNodeUrl()));
	}

	private void savePreviousState() {
		if (memberRecord != null) {
			memberRepository.saveTreeMember(memberRecord);
		}
	}

	public void destroyState() {
		memberRepository.destroyState();
		treeNodeRecordRepository.destroyState();
	}
}
