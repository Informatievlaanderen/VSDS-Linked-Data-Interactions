package be.vlaanderen.informatievlaanderen.ldes.ldi.processors.services;

import org.apache.nifi.dbcp.DBCPService;

import javax.sql.DataSource;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Logger;

public class NiFiDBCPDataSource implements DataSource {
	private static final String NOT_SUPPORTED = "Not supported";

	private final DBCPService dbcpService;

	public NiFiDBCPDataSource(DBCPService dbcpService) {
		this.dbcpService = dbcpService;
	}

	@Override
	public Connection getConnection() {
		return dbcpService.getConnection();
	}

	@Override
	public Connection getConnection(String username, String password) {
		throw new UnsupportedOperationException("DBCPService does not support username/password connections");
	}

	@Override
	public PrintWriter getLogWriter() {
		throw new UnsupportedOperationException(NOT_SUPPORTED);
	}

	@Override
	public void setLogWriter(PrintWriter out) {
		throw new UnsupportedOperationException(NOT_SUPPORTED);
	}

	@Override
	public int getLoginTimeout() {
		throw new UnsupportedOperationException(NOT_SUPPORTED);
	}

	@Override
	public void setLoginTimeout(int seconds) {
		throw new UnsupportedOperationException(NOT_SUPPORTED);
	}

	@Override
	public Logger getParentLogger() {
		throw new UnsupportedOperationException(NOT_SUPPORTED);
	}

	@Override
	public <T> T unwrap(Class<T> iface) throws SQLException {
		throw new SQLException("Not a wrapper");
	}

	@Override
	public boolean isWrapperFor(Class<?> iface) {
		return false;
	}
}