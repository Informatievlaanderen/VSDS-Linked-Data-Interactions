// package be.vlaanderen.informatievlaanderen.ldes.ldi.processors;
//
// import org.apache.nifi.util.TestRunner;
// import org.apache.nifi.util.TestRunners;
// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.Test;
//
// import com.github.tomakehurst.wiremock.junit5.WireMockTest;
//
// import static org.junit.jupiter.api.Assertions.assertEquals;
//
// @WireMockTest(httpPort = 10101)
// class LdesClientStateTest {
//
// private final String fragment3 =
// "http://localhost:10101/exampleData?generatedAtTime=2022-05-03T00:00:00.000Z";
// private final String fragment4 =
// "http://localhost:10101/exampleData?generatedAtTime=2022-05-04T00:00:00.000Z";
// private final String fragment5 =
// "http://localhost:10101/exampleData?generatedAtTime=2022-05-05T00:00:00.000Z";
//
// private TestRunner testRunner;
//
// @BeforeEach
// void setup() {
// testRunner = TestRunners.newTestRunner(LdesClient.class);
//
// testRunner.setThreadCount(1);
// testRunner.setProperty("DATA_SOURCE_URL", fragment3);
// }
//
// @Test
// void whenRunningOnTriggerOnce_thenExactlyOneImmutableFragmentIsPersisted() {
// testRunner.run(1);
// assertEquals(1, ((LdesClient)
// testRunner.getProcessor()).ldesService.getStateManager()
// .countProcessedImmutableFragments());
// assertEquals(2, ((LdesClient)
// testRunner.getProcessor()).ldesService.getStateManager().countProcessedMembers());
//
// testRunner.run(1);
// assertEquals(2, ((LdesClient)
// testRunner.getProcessor()).ldesService.getStateManager()
// .countProcessedImmutableFragments());
// assertEquals(4, ((LdesClient)
// testRunner.getProcessor()).ldesService.getStateManager().countProcessedMembers());
//
// testRunner.run(1);
// assertEquals(3, ((LdesClient)
// testRunner.getProcessor()).ldesService.getStateManager()
// .countProcessedImmutableFragments());
// assertEquals(6, ((LdesClient)
// testRunner.getProcessor()).ldesService.getStateManager().countProcessedMembers());
//
// ((LdesClient)
// testRunner.getProcessor()).ldesService.getStateManager().destroyState();
// }
// }
