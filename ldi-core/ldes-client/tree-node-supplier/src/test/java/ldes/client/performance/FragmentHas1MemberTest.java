package ldes.client.performance;

import be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.executor.RequestExecutor;
import be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.executor.noauth.DefaultConfig;
import be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.valueobjects.Request;
import be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.valueobjects.RequestHeaders;
import be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.valueobjects.Response;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import com.github.tomakehurst.wiremock.extension.responsetemplating.ResponseTemplateTransformer;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import com.github.tomakehurst.wiremock.junit5.WireMockRuntimeInfo;
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
import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFParser;
import org.apache.jena.riot.RDFWriter;
import org.apache.jena.vocabulary.RDF;
import org.junit.Rule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.testcontainers.containers.PostgreSQLContainer;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Map;

import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.apache.jena.util.ResourceUtils.renameResource;
import static org.junit.jupiter.api.Assertions.assertEquals;

//@WireMockTest(httpPort = 10101)
public class FragmentHas1MemberTest {

    /**
     * Wat willen we:
     * - verschillende fragmentgrootte
     * - verschillende persistence methodes
     *
     * grafiek van members/seconde op Y as en members processed op X -as
     */


//    private static final String URL = "https://onboarding1.smartdataspace.beta-vlaanderen.be/building-units";
//    private static final String URL = "http://localhost:10101/performance?pageNumber=1";
    private static final String URL = "http://localhost:10101/mobility-hindrances/pagination10?pageNumber=2";
    private static final Lang LANG = Lang.TURTLE;
    private TreeNodeProcessor treeNodeProcessor;


//    @RegisterExtension
//    static WireMockExtension wm = WireMockExtension.newInstance()
//            .options(
//                    wireMockConfig()
//                            .port(10101)
//                            .extensions(new ResponseTemplateTransformer(false))
//            )
//            .build();

//    @Rule
//    public WireMockRule wm = new WireMockRule(options()
//            .extensions(new ResponseTemplateTransformer(false))
//    );


    @Test
    void temp() {
        new WireMockServer(
                WireMockConfiguration.options().extensions(new ResponseTemplateTransformer(false)).port(10101)).start();
//                WireMockConfiguration.options().extensions(new CustomResponseTransformer()).port(10101)).start();
//        new WireMockServer(
//                WireMockConfiguration.options().port(10101)).start();


        LocalDateTime start = LocalDateTime.now();
        for (int i = 0; i < 999_999; i++) {
            SuppliedMember member = treeNodeProcessor.getMember();
            if (i % 1000 == 0) {
                System.out.println(i);
            }
//            String string = RDFWriter.source(member.getModel()).lang(Lang.TURTLE).asString();
//            System.out.println(string);
        }
        LocalDateTime end = LocalDateTime.now();
        System.out.println("This took: " + ChronoUnit.SECONDS.between(start, end));


//        RequestExecutor requestExecutor = new DefaultConfig().createRequestExecutor();
//        Response response = requestExecutor.execute(new Request("http://localhost:10101/mobility-hindrances/pagination10?pageNumber=1", RequestHeaders.empty()));
//        System.out.println(response.getBody());
        //        Response response = requestExecutor.execute(new Request(URL, RequestHeaders.empty()));
//        Model model = RDFParser.fromString(response.getBody().get()).lang(Lang.TURTLE).toModel();
//        model.listStatements(null, RDF.type, model.createProperty("https://data.vlaanderen.be/ns/mobiliteit#Mobiliteitshinder"))
//                .toList()
//                .forEach(statement -> renameResource(statement.getSubject(), statement.getSubject().getURI() + "/{{request.query.pageNumber}}"));
//        assertEquals(200, response.getHttpStatus());
//        System.out.println(response.getBody());
    }

    @BeforeEach
    void setUp() {
        final LdesMetaData ldesMetaData = new LdesMetaData(URL, LANG);
        final PostgreSQLContainer postgreSQLContainer = startPostgresContainer();
        final StatePersistence statePersistence = createInMemoryStatePersistence();
//        final StatePersistence statePersistence = createStatePersistence(postgreSQLContainer);
//        final StatePersistence statePersistence = aSqliteStatePersistenceStrategy();
//        final StatePersistence statePersistence = aFileStatePersistenceStrategy();
        final RequestExecutor requestExecutor = new DefaultConfig().createRequestExecutor();
        treeNodeProcessor = new TreeNodeProcessor(ldesMetaData, statePersistence, requestExecutor);
    }

    @Test
    void name() {
        for (int i = 0; i < 999_999; i++) {
            SuppliedMember member = treeNodeProcessor.getMember();
            System.out.println(i);
//            String string = RDFWriter.source(member.getModel()).lang(Lang.TURTLE).asString();
//            System.out.println(string);
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

    public StatePersistence aFileStatePersistenceStrategy() {
        MemberRepository memberRepository = MemberRepositoryFactory.getMemberRepository(StatePersistenceStrategy.FILE, Map.of());
        TreeNodeRecordRepository treeNodeRecordRepository = TreeNodeRecordRepositoryFactory
                .getTreeNodeRecordRepository(StatePersistenceStrategy.FILE, Map.of());
        return new StatePersistence(memberRepository, treeNodeRecordRepository);
    }

    public StatePersistence aSqliteStatePersistenceStrategy() {
        MemberRepository memberRepository = MemberRepositoryFactory.getMemberRepository(StatePersistenceStrategy.SQLITE, Map.of());
        TreeNodeRecordRepository treeNodeRecordRepository = TreeNodeRecordRepositoryFactory
                .getTreeNodeRecordRepository(StatePersistenceStrategy.SQLITE, Map.of());
        return new StatePersistence(memberRepository, treeNodeRecordRepository);
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

    private StatePersistence createInMemoryStatePersistence() {
        MemberRepository memberRepository = MemberRepositoryFactory.getMemberRepository(StatePersistenceStrategy.MEMORY, Map.of());
        TreeNodeRecordRepository treeNodeRecordRepository = TreeNodeRecordRepositoryFactory
                .getTreeNodeRecordRepository(StatePersistenceStrategy.MEMORY, Map.of());
        return new StatePersistence(memberRepository, treeNodeRecordRepository);
    }
}
