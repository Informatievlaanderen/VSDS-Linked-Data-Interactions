package be.vlaanderen.informatievlaanderen.ldes.ldi;

import org.hibernate.engine.jdbc.connections.spi.ConnectionProvider;
import org.hibernate.service.UnknownUnwrapTypeException;
import org.hibernate.service.spi.Stoppable;

import java.sql.Connection;
import java.sql.SQLException;

public class CustomConnectionProvider implements ConnectionProvider, Stoppable {
	private final Connection connection;

	public CustomConnectionProvider(Connection connection) {
		this.connection = connection;
	}

	@Override
	public Connection getConnection() throws SQLException {
		return connection;
	}

	@Override
	public void closeConnection(Connection conn) throws SQLException {
		// Do not close the provided connection
		conn.close();
	}

	@Override
	public boolean supportsAggressiveRelease() {
		return false;
	}

	@Override
	public void stop() {
		// Implement any cleanup if necessary
	}

	@Override
	public <T> T unwrap(Class<T> unwrapType) {
		if (isUnwrappableAs(unwrapType)) {
			return unwrapType.cast(connection);
		} else {
			throw new UnknownUnwrapTypeException(unwrapType);
		}
	}

	@Override
	public boolean isUnwrappableAs(Class unwrapType) {
		return Connection.class.equals(unwrapType) || CustomConnectionProvider.class.isAssignableFrom(unwrapType);
	}
}
