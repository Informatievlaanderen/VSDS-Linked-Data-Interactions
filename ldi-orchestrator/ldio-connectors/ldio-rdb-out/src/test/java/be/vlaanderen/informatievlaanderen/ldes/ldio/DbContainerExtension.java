package be.vlaanderen.informatievlaanderen.ldes.ldio;

import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.testcontainers.containers.MSSQLServerContainer;
import org.testcontainers.containers.startupcheck.MinimumDurationRunningStartupCheckStrategy;

import java.time.Duration;

public class DbContainerExtension implements BeforeAllCallback, AfterAllCallback {
    private MSSQLServerContainer<?> mssqlContainer;

    @Override
    public void beforeAll(ExtensionContext context) throws Exception {
        mssqlContainer = new MSSQLServerContainer<>("mcr.microsoft.com/mssql/server:2022-latest")
                .withPassword("yourStrong(!)Password")
                .withEnv("MSSQL_PID", "Developer")
                .acceptLicense()
                .withStartupCheckStrategy(new MinimumDurationRunningStartupCheckStrategy(Duration.ofSeconds(5)));
        mssqlContainer.start();

        System.setProperty("spring.datasource.url", mssqlContainer.getJdbcUrl());
        System.setProperty("spring.datasource.username", mssqlContainer.getUsername());
        System.setProperty("spring.datasource.password", mssqlContainer.getPassword());
        System.setProperty("spring.datasource.driver-class-name", mssqlContainer.getDriverClassName());
    }


    @Override
    public void afterAll(ExtensionContext context) throws Exception {
    }
}
