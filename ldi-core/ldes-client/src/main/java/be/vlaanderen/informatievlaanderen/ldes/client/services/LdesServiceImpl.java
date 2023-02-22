package be.vlaanderen.informatievlaanderen.ldes.client.services;

import be.vlaanderen.informatievlaanderen.ldes.client.state.LdesStateManager;
import be.vlaanderen.informatievlaanderen.ldes.client.valueobjects.LdesFragment;
import be.vlaanderen.informatievlaanderen.ldes.client.valueobjects.LdesMember;
import be.vlaanderen.informatievlaanderen.ldes.ldi.types.LdiInput;
import org.apache.jena.graph.TripleBoundary;
import org.apache.jena.rdf.model.*;
import org.apache.jena.riot.Lang;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static be.vlaanderen.informatievlaanderen.ldes.client.valueobjects.LdesConstants.*;

public class LdesServiceImpl implements LdesService {

	private static final Logger LOGGER = LoggerFactory.getLogger(LdesServiceImpl.class);

	protected static final Resource ANY_RESOURCE = null;
	protected static final Property ANY_PROPERTY = null;

	protected final LdesStateManager stateManager;
	protected final LdesFragmentFetcher fragmentFetcher;
	private final ModelExtract modelExtract;

	public LdesServiceImpl(final LdesStateManager stateManager, final LdesFragmentFetcher fragmentFetcher) {
		this.stateManager = stateManager;
		this.fragmentFetcher = fragmentFetcher;

		modelExtract = new ModelExtract(new StatementTripleBoundary(TripleBoundary.stopNowhere));
	}

	@Override
	public Lang getDataSourceFormat() {
		return fragmentFetcher.getDataSourceFormat();
	}

	@Override
	public void setDataSourceFormat(Lang dataSourceFormat) {
		fragmentFetcher.setDataSourceFormat(dataSourceFormat);
	}

	@Override
	public Long getFragmentExpirationInterval() {
		return stateManager.getFragmentExpirationInterval();
	}

	@Override
	public void setFragmentExpirationInterval(Long fragmentExpirationInterval) {
		stateManager.setFragmentExpirationInterval(fragmentExpirationInterval);
	}

	@Override
	public LdesStateManager getStateManager() {
		return stateManager;
	}

	@Override
	public void queueFragment(String fragmentId) {
		stateManager.queueFragment(fragmentId);
	}

	@Override
	public boolean hasFragmentsToProcess() {
		return stateManager.hasNext();
	}

	@Override
	public LdesFragment processNextFragment() {
		String fragmentId = stateManager.next();
		LdesFragment fragment = fragmentFetcher.fetchFragment(fragmentId);

		if (!fragment.getFragmentId().equalsIgnoreCase(fragmentId)) {
			stateManager.redirectFragment(fragmentId, fragment.getFragmentId());
		}

		// Extract and process the members and add them to the fragment
		extractMembers(fragment.getModel()).forEach(memberStatement -> {
			if (stateManager.shouldProcessMember(memberStatement.getObject().toString())) {
				LdesMember processedMember = processMember(fragment, memberStatement);
				fragment.addMember(processedMember);
			}
		});

		// Extract relations and add them to the fragment
		extractRelations(fragment.getModel()).forEach(relationStatement -> fragment
				.addRelation(relationStatement.getResource().getProperty(W3ID_TREE_NODE).getResource().toString()));
		// Queue the related fragments
		fragment.getRelations().forEach(stateManager::queueFragment);

		// Inform the StateManager that a fragment has been processed
		stateManager.processedFragment(fragment);

		LOGGER.debug("PROCESSED ({}MUTABLE) fragment {} has {} member(s) and {} tree:relation(s)",
				fragment.isImmutable() ? "IM" : "", fragment.getFragmentId(), fragment.getMembers().size(),
				fragment.getRelations().size());

		return fragment;
	}

	protected Stream<Statement> extractMembers(Model fragmentModel) {
		StmtIterator memberIterator = fragmentModel.listStatements(ANY_RESOURCE, W3ID_TREE_MEMBER, ANY_RESOURCE);

		return Stream.iterate(memberIterator, Iterator::hasNext, UnaryOperator.identity())
				.map(Iterator::next);
	}

	protected LdesMember processMember(LdesFragment fragment, Statement memberStatement) {
		Model fragmentModel = fragment.getModel();
		Model memberModel = modelExtract.extract(memberStatement.getObject().asResource(), fragmentModel);
		String memberId = memberStatement.getObject().toString();

		memberModel.add(memberStatement);

		// Add reverse properties
		Set<Statement> otherLdesMembers = fragmentModel
				.listStatements(memberStatement.getSubject(), W3ID_TREE_MEMBER, ANY_RESOURCE).toSet().stream()
				.filter(statement -> !memberStatement.equals(statement)).collect(Collectors.toSet());

		fragmentModel.listStatements(ANY_RESOURCE, ANY_PROPERTY, memberStatement.getResource())
				.filterKeep(statement -> statement.getSubject().isURIResource()).filterDrop(memberStatement::equals)
				.forEach(statement -> {
					Model reversePropertyModel = modelExtract.extract(statement.getSubject(), fragmentModel);
					List<Statement> otherMembers = reversePropertyModel
							.listStatements(statement.getSubject(), statement.getPredicate(), ANY_RESOURCE).toList();
					otherLdesMembers.forEach(otherLdesMember -> reversePropertyModel
							.listStatements(ANY_RESOURCE, ANY_PROPERTY, otherLdesMember.getResource()).toList());
					otherMembers.forEach(otherMember -> reversePropertyModel
							.remove(modelExtract.extract(otherMember.getResource(), reversePropertyModel)));

					memberModel.add(reversePropertyModel);
				});

		LdesMember member = new LdesMember(memberId, memberModel);

		// Inform the StateManager that a member has been processed
		stateManager.processedMember(member);

		LOGGER.debug("PROCESSED LDES member ({}) on fragment {}", memberId, fragment.getFragmentId());

		return member;
	}

	@Override
	public Stream<Statement> extractRelations(Model fragmentModel) {
		return Stream.iterate(fragmentModel.listStatements(ANY_RESOURCE, W3ID_TREE_RELATION, ANY_RESOURCE),
				Iterator::hasNext, UnaryOperator.identity()).map(Iterator::next);
	}
}
