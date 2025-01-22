package be.vlaanderen.informatievlaanderen.ldes.ldi.processors.services;

import org.apache.nifi.dbcp.DBCPService;

import javax.sql.DataSource;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Logger;

public class NiFiDBCPDataSource implements DataSource {

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
		throw new UnsupportedOperationException("Not supported");
	}

	@Override
	public void setLogWriter(PrintWriter out) {
		throw new UnsupportedOperationException("Not supported");
	}

	@Override
	public int getLoginTimeout() {
		throw new UnsupportedOperationException("Not supported");
	}

	@Override
	public void setLoginTimeout(int seconds) {
		throw new UnsupportedOperationException("Not supported");
	}

	@Override
	public Logger getParentLogger() {
		throw new UnsupportedOperationException("Not supported");
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