package ldes.client.treenodesupplier;

import be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.executor.RequestExecutor;
import be.vlaanderen.informatievlaanderen.ldes.ldi.timestampextractor.TimestampExtractor;
import ldes.client.treenodefetcher.TreeNodeFetcher;
import ldes.client.treenodefetcher.domain.entities.TreeMember;
import ldes.client.treenodefetcher.domain.valueobjects.TreeNodeResponse;
import ldes.client.treenodesupplier.domain.entities.MemberRecord;
import ldes.client.treenodesupplier.domain.entities.TreeNodeRecord;
import ldes.client.treenodesupplier.domain.valueobject.*;
import ldes.client.treenodesupplier.repository.MemberRepository;
import ldes.client.treenodesupplier.repository.TreeNodeRecordRepository;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

import static java.lang.Thread.sleep;

public class TreeNodeProcessor {

	private final TreeNodeRecordRepository treeNodeRecordRepository;
	private final MemberRepository memberRepository;
	private final TreeNodeFetcher treeNodeFetcher;
	private final LdesMetaData ldesMetaData;
	private final RequestExecutor requestExecutor;
	private MemberRecord memberRecord;

	public TreeNodeProcessor(LdesMetaData ldesMetaData, StatePersistence statePersistence,
	                         RequestExecutor requestExecutor, TimestampExtractor timestampExtractor) {
		this.treeNodeRecordRepository = statePersistence.getTreeNodeRecordRepository();
		this.memberRepository = statePersistence.getMemberRepository();
		this.requestExecutor = requestExecutor;
		this.treeNodeFetcher = new TreeNodeFetcher(requestExecutor, timestampExtractor);
		this.ldesMetaData = ldesMetaData;
	}

	public void init() {
		if (!treeNodeRecordRepository.containsTreeNodeRecords()) {
			initializeTreeNodeRecordRepository();
		}
	}

	public SuppliedMember getMember() {
		removeLastMember();

		Optional<MemberRecord> unprocessedTreeMember = memberRepository.getTreeMember();
		while (unprocessedTreeMember.isEmpty()) {
			processTreeNode();
			unprocessedTreeMember = memberRepository.getTreeMember();
		}
		MemberRecord treeMember = unprocessedTreeMember.get();
		SuppliedMember suppliedMember = treeMember.createSuppliedMember();
		memberRecord = treeMember;
		return suppliedMember;
	}

	public LdesMetaData getLdesMetaData() {
		return ldesMetaData;
	}

	private void processTreeNode() {
		TreeNodeRecord treeNodeRecord = treeNodeRecordRepository
				.getOneTreeNodeRecordWithStatus(TreeNodeStatus.NOT_VISITED).orElseGet(
						() -> treeNodeRecordRepository.getOneTreeNodeRecordWithStatus(TreeNodeStatus.MUTABLE_AND_ACTIVE)
								.orElseThrow(() -> new EndOfLdesException(
										"No fragments to mutable or new fragments to process -> LDES ends.")));
		waitUntilNextVisit(treeNodeRecord);
		TreeNodeResponse treeNodeResponse = treeNodeFetcher
				.fetchTreeNode(ldesMetaData.createRequest(treeNodeRecord.getTreeNodeUrl()));
		treeNodeRecord.updateStatus(treeNodeResponse.getMutabilityStatus());
		treeNodeRecordRepository.saveTreeNodeRecord(treeNodeRecord);
		treeNodeResponse.getRelations()
				.stream()
				.filter(treeNodeId -> !treeNodeRecordRepository.existsById(treeNodeId))
				.map(TreeNodeRecord::new)
				.forEach(treeNodeRecordRepository::saveTreeNodeRecord);
		List<TreeMember> newMembers = treeNodeResponse.getMembers().stream().filter(member -> !treeNodeRecord.hasRecieved(member.getMemberId())).toList();
		memberRepository.saveTreeMembers(newMembers
				.stream()
				.map(treeMember -> new MemberRecord(treeMember.getMemberId(), treeMember.getModel(), treeMember.getCreatedAt())));
		treeNodeRecord.addToReceived(newMembers.stream().map(TreeMember::getMemberId).toList());
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

	private void initializeTreeNodeRecordRepository() {
		ldesMetaData.getStartingNodeUrls()
				.stream()
				.map(startingNode -> new StartingTreeNodeSupplier(requestExecutor)
						.getStart(startingNode, ldesMetaData.getLang()))
				.map(start -> new TreeNodeRecord(start.getStartingNodeUrl()))
				.forEach(treeNodeRecordRepository::saveTreeNodeRecord);
	}

	private void removeLastMember() {
		if (memberRecord != null) {
			memberRepository.deleteMember(memberRecord);
		}
	}

	public void destroyState() {
		memberRepository.destroyState();
		treeNodeRecordRepository.destroyState();
	}
}
