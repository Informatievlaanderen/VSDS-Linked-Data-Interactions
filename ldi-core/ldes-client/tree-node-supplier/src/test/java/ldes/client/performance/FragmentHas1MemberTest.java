package ldes.client.performance;

import be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.executor.RequestExecutor;
import be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.executor.noauth.DefaultConfig;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import ldes.client.treenodesupplier.TreeNodeProcessor;
import ldes.client.treenodesupplier.domain.services.MemberRepositoryFactory;
import ldes.client.treenodesupplier.domain.services.TreeNodeRecordRepositoryFactory;
import ldes.client.treenodesupplier.domain.valueobject.LdesMetaData;
import ldes.client.treenodesupplier.domain.valueobject.StatePersistence;
import ldes.client.treenodesupplier.domain.valueobject.StatePersistenceStrategy;
import ldes.client.treenodesupplier.domain.valueobject.SuppliedMember;
import ldes.client.treenodesupplier.repository.MemberRepository;
import ldes.client.treenodesupplier.repository.TreeNodeRecordRepository;
import ldes.client.treenodesupplier.repository.sql.postgres.PostgresProperties;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFWriter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.PostgreSQLContainer;

@WireMockTest
public class FragmentHas1MemberTest {

    private static final String URL = "https://onboarding1.smartdataspace.beta-vlaanderen.be/building-units";
    private static final Lang LANG = Lang.TURTLE;
    private TreeNodeProcessor treeNodeProcessor;

    @BeforeEach
    void setUp() {
        final LdesMetaData ldesMetaData = new LdesMetaData(URL, LANG);
        final PostgreSQLContainer postgreSQLContainer = startPostgresContainer();
        final StatePersistence statePersistence = createStatePersistence(postgreSQLContainer);
        final RequestExecutor requestExecutor = new DefaultConfig().createRequestExecutor();
        treeNodeProcessor = new TreeNodeProcessor(ldesMetaData, statePersistence, requestExecutor);
    }

    @Test
    void name() {
        for (int i = 0; i < 10; i++) {
            SuppliedMember member = treeNodeProcessor.getMember();
            String string = RDFWriter.source(member.getModel()).lang(Lang.TURTLE).asString();
            System.out.println(string);
        }
    }

    private PostgreSQLContainer startPostgresContainer() {
        PostgreSQLContainer postgreSQLContainer = new PostgreSQLContainer("postgres:11.1")
                .withDatabaseName("integration-test-client-persistence")
                .withUsername("sa")
                .withPassword("sa");
        postgreSQLContainer.start();
        return postgreSQLContainer;
    }

    private StatePersistence createStatePersistence(PostgreSQLContainer postgreSQLContainer) {
        PostgresProperties postgresProperties = new PostgresProperties(postgreSQLContainer.getJdbcUrl(),
                postgreSQLContainer.getUsername(), postgreSQLContainer.getPassword(), false);
        MemberRepository memberRepository = MemberRepositoryFactory.getMemberRepository(StatePersistenceStrategy.POSTGRES,
                postgresProperties.getProperties());
        TreeNodeRecordRepository treeNodeRecordRepository = TreeNodeRecordRepositoryFactory
                .getTreeNodeRecordRepository(StatePersistenceStrategy.POSTGRES, postgresProperties.getProperties());

        return new StatePersistence(memberRepository, treeNodeRecordRepository);
    }
}
