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
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;

import static java.lang.Thread.sleep;
import static ldes.client.treenodesupplier.domain.valueobject.ClientStatus.*;

public class TreeNodeProcessor {

	private final TreeNodeRecordRepository treeNodeRecordRepository;
	private final MemberRepository memberRepository;
	private final TreeNodeFetcher treeNodeFetcher;
	private final LdesMetaData ldesMetaData;
	private final RequestExecutor requestExecutor;
	private final Consumer<ClientStatus> clientStatusConsumer;
	private MemberRecord memberRecord;

	public TreeNodeProcessor(LdesMetaData ldesMetaData, StatePersistence statePersistence,
	                         RequestExecutor requestExecutor, TimestampExtractor timestampExtractor,
	                         Consumer<ClientStatus> clientStatusConsumer) {
		this.treeNodeRecordRepository = statePersistence.getTreeNodeRecordRepository();
		this.memberRepository = statePersistence.getMemberRepository();
		this.requestExecutor = requestExecutor;
		this.clientStatusConsumer = clientStatusConsumer;
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

	private void processTreeNode() {
		TreeNodeRecord treeNodeRecord = getNextTreeNode();

		if (TreeNodeStatus.IMMUTABLE_WITH_UNPROCESSED_MEMBERS.equals(treeNodeRecord.getTreeNodeStatus())) {
			treeNodeRecord.markImmutableWithoutUnprocessedMembers();
			treeNodeRecordRepository.saveTreeNodeRecord(treeNodeRecord);
		} else {
			waitUntilNextVisit(treeNodeRecord);
			TreeNodeResponse treeNodeResponse = treeNodeFetcher
					.fetchTreeNode(ldesMetaData.createRequest(treeNodeRecord.getTreeNodeUrl()));
			treeNodeRecord.updateStatus(treeNodeResponse.getMutabilityStatus());
			saveNewRelations(treeNodeResponse);
			List<TreeMember> newMembers = getNewMembersFromResponse(treeNodeResponse, treeNodeRecord);
			saveNewMembers(newMembers);
			treeNodeRecord.addToReceived(newMembers.stream().map(TreeMember::getMemberId).toList());
			treeNodeRecordRepository.saveTreeNodeRecord(treeNodeRecord);
			treeNodeRecordRepository.resetContext();
		}
	}

	private void saveNewMembers(List<TreeMember> newMembers) {
		memberRepository.saveTreeMembers(newMembers
				.stream()
				.map(treeMember -> new MemberRecord(treeMember.getMemberId(), treeMember.getModel(), treeMember.getCreatedAt())));
	}

	private static List<TreeMember> getNewMembersFromResponse(TreeNodeResponse treeNodeResponse, TreeNodeRecord treeNodeRecord) {
		return treeNodeResponse
				.getMembers()
				.stream()
				.filter(member -> !treeNodeRecord.hasReceived(member.getMemberId()))
				.toList();
	}

	private void saveNewRelations(TreeNodeResponse treeNodeResponse) {
		treeNodeResponse.getRelations()
				.stream()
				.filter(treeNodeId -> !treeNodeRecordRepository.existsById(treeNodeId))
				.map(TreeNodeRecord::new)
				.forEach(treeNodeRecordRepository::saveTreeNodeRecord);
	}

	private TreeNodeRecord getNextTreeNode() {
		TreeNodeRecord treeNodeRecord = treeNodeRecordRepository
				.getTreeNodeRecordWithStatusAndEarliestNextVisit(TreeNodeStatus.IMMUTABLE_WITH_UNPROCESSED_MEMBERS)
				.orElseGet(() -> treeNodeRecordRepository.getTreeNodeRecordWithStatusAndEarliestNextVisit(TreeNodeStatus.NOT_VISITED)
						.orElseGet(() -> treeNodeRecordRepository.getTreeNodeRecordWithStatusAndEarliestNextVisit(TreeNodeStatus.MUTABLE_AND_ACTIVE)
								.orElseThrow(() -> {
									clientStatusConsumer.accept(COMPLETED);
									return new EndOfLdesException("No fragments to mutable or new fragments to process -> LDES ends.");
								})));

		if (Objects.requireNonNull(treeNodeRecord.getTreeNodeStatus()) == TreeNodeStatus.IMMUTABLE_WITH_UNPROCESSED_MEMBERS ||
		    treeNodeRecord.getTreeNodeStatus() == TreeNodeStatus.IMMUTABLE_WITHOUT_UNPROCESSED_MEMBERS ||
		    treeNodeRecord.getTreeNodeStatus() == TreeNodeStatus.NOT_VISITED) {
			clientStatusConsumer.accept(REPLICATING);
		} else if (treeNodeRecord.getTreeNodeStatus() == TreeNodeStatus.MUTABLE_AND_ACTIVE) {
			clientStatusConsumer.accept(SYNCHRONISING);
		}

		return treeNodeRecord;
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
