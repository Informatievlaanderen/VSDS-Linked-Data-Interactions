package ldes.client.treenodesupplier.domain.valueobject;

import be.vlaanderen.informatievlaanderen.ldes.ldi.HibernateProperties;
import be.vlaanderen.informatievlaanderen.ldes.ldi.h2.H2EntityManager;
import ldes.client.treenodesupplier.repository.MemberIdRepository;
import ldes.client.treenodesupplier.repository.MemberRepository;
import ldes.client.treenodesupplier.repository.MemberVersionRepository;
import ldes.client.treenodesupplier.repository.TreeNodeRecordRepository;
import ldes.client.treenodesupplier.repository.sql.SqlMemberIdRepository;
import ldes.client.treenodesupplier.repository.sql.SqlMemberRepository;
import ldes.client.treenodesupplier.repository.sql.SqlMemberVersionRepository;
import ldes.client.treenodesupplier.repository.sql.SqlTreeNodeRepository;

public record StatePersistence(MemberRepository memberRepository, MemberIdRepository memberIdRepository,
                               TreeNodeRecordRepository treeNodeRecordRepository,
                               MemberVersionRepository memberVersionRepository) {

	public static StatePersistence from(HibernateProperties properties, String pipelineName) {
		var h2EntityManager = H2EntityManager.getInstance(pipelineName, properties.getProperties());
		return new StatePersistence(
				new SqlMemberRepository(pipelineName, h2EntityManager),
				new SqlMemberIdRepository(pipelineName, h2EntityManager),
				new SqlTreeNodeRepository(pipelineName, h2EntityManager),
				new SqlMemberVersionRepository(pipelineName, h2EntityManager));
	}


}
