package be.vlaanderen.informatievlaanderen.ldes.client.state;

import be.vlaanderen.informatievlaanderen.ldes.client.config.LdesClientConfig;
import be.vlaanderen.informatievlaanderen.ldes.client.exceptions.LdesPersistenceException;
import be.vlaanderen.informatievlaanderen.ldes.client.exceptions.LdesPersistenceQueryException;
import be.vlaanderen.informatievlaanderen.ldes.client.member.MemberRepository;
import be.vlaanderen.informatievlaanderen.ldes.client.member.sqlite.SqliteMemberRepository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.*;
import java.time.LocalDateTime;

public class SqlitePersistedLdesStateManager extends LdesStateManager {

	private static final String FRAGMENTS_TO_PROCESS = "fragmentsToProcess";
	private static final String PROCESSED_IMMUTABLE_FRAGMENTS = "processedImmutableFragments";
	private static final String PROCESSED_MUTABLE_FRAGMENTS = "processedMutableFragments";

	private static final String CONNECTION_PREFIX = "jdbc:sqlite:";
	private static final String FRAGMENT = "fragment";

	private static final String SELECT_FRAGMENT = "SELECT fragment FROM ";
	private static final String SELECT_COUNT = "SELECT COUNT(0) FROM ";

	private static final String SQL_IS_QUEUED_FRAGMENT = countForKey(FRAGMENTS_TO_PROCESS, FRAGMENT);
	private static final String SQL_IS_PROCESSED_IMMUTABLE_FRAGMENT = countForKey(PROCESSED_IMMUTABLE_FRAGMENTS,
			FRAGMENT);
	private static final String SQL_IS_PROCESSED_MUTABLE_FRAGMENT = countForKey(PROCESSED_MUTABLE_FRAGMENTS, FRAGMENT);
	private static final String SQL_INSERT_FRAGMENT_TO_PROCESS = insert(FRAGMENTS_TO_PROCESS);
	private static final String SQL_SELECT_NEXT_FRAGMENT_TO_PROCESS = selectFirst(FRAGMENTS_TO_PROCESS);
	private static final String SQL_REMOVE_FRAGMENT_TO_PROCESS = delete(FRAGMENTS_TO_PROCESS);
	private static final String SQL_RENAME_FRAGMENT_TO_PROCESS = "UPDATE " + FRAGMENTS_TO_PROCESS
			+ " SET fragment = ? WHERE fragment = ?";
	private static final String SQL_INSERT_PROCESSED_IMMUTABLE_FRAGMENT = insertIfNotExists(
			PROCESSED_IMMUTABLE_FRAGMENTS, FRAGMENT);
	private static final String SQL_REMOVE_PROCESSED_IMMUTABLE_FRAGMENT = delete(PROCESSED_IMMUTABLE_FRAGMENTS);
	private static final String SQL_INSERT_PROCESSED_MUTABLE_FRAGMENT = "INSERT OR REPLACE INTO "
			+ PROCESSED_MUTABLE_FRAGMENTS + " (fragment, expiration) VALUES (?, ?)";
	private static final String SQL_SELECT_PROCESSED_MUTABLE_FRAGMENT_EXPIRATION_DATE = "SELECT expiration FROM "
			+ PROCESSED_MUTABLE_FRAGMENTS + " WHERE fragment = ?";
	private static final String SQL_SELECT_EXPIRED_MUTABLE_FRAGMENT = selectExpired();
	private static final String SQL_REMOVE_PROCESSED_MUTABLE_FRAGMENT = delete(PROCESSED_MUTABLE_FRAGMENTS);
	private static final String SQL_COUNT_FRAGMENTS_TO_PROCESS = count(FRAGMENTS_TO_PROCESS);
	private static final String SQL_COUNT_PROCESSED_IMMUTABLE_FRAGMENTS = count(PROCESSED_IMMUTABLE_FRAGMENTS);
	private static final String SQL_COUNT_PROCESSED_MUTABLE_FRAGMENTS = count(PROCESSED_MUTABLE_FRAGMENTS);

	private final LdesClientConfig config;

	private boolean dbInitialised = false;
	private String dbName;
	private final MemberRepository memberRepository;

	public SqlitePersistedLdesStateManager(LdesClientConfig config) {
		this.config = config;
		memberRepository = new SqliteMemberRepository();
	}

	private Connection connect() {
		try {
			dbName = config.getPersistenceDbName().replace(CONNECTION_PREFIX, "");
			String connectionUrl = dbName.startsWith(CONNECTION_PREFIX) ? dbName : CONNECTION_PREFIX + dbName;

			Class.forName(config.getPersistenceDbDriver());
			Connection connection = DriverManager.getConnection(connectionUrl);

			if (!dbInitialised) {
				dbInitialised = setupDb(connection);

				return connect();
			}

			return connection;
		} catch (ClassNotFoundException | SQLException e) {
			throw new LdesPersistenceException("Unable to connect to persistence database " + dbName, e);
		}
	}

	private boolean setupDb(Connection connection) {
		try {
			try (Statement stmt = connection.createStatement()) {
				stmt.execute(createTable(FRAGMENTS_TO_PROCESS, FRAGMENT));
			}
			try (Statement stmt = connection.createStatement()) {
				stmt.execute(createTable(PROCESSED_IMMUTABLE_FRAGMENTS, FRAGMENT));
			}
			try (Statement stmt = connection.createStatement()) {
				stmt.execute("CREATE TABLE IF NOT EXISTS " + PROCESSED_MUTABLE_FRAGMENTS
						+ " (id ROWID, fragment TEXT UNIQUE, expiration TEXT)");
			}

			return true;
		} catch (SQLException e) {
			throw new LdesPersistenceQueryException("Unable to setup persistence database", e);
		} finally {
			try {
				connection.close();
			} catch (SQLException s) {
				//
			}
		}
	}

	@Override
	public boolean destroyState() {
		memberRepository.clearState();
		Connection connection = connect();
		try {
			connection.close();
			Files.delete(Path.of(dbName));

			dbInitialised = false;

			return true;
		} catch (SQLException s) {
			throw new LdesPersistenceException("Unable to close db connection while attempting to destroy state", s);
		} catch (IOException ioe) {
			throw new LdesPersistenceException("Unable to delete database file " + dbName, ioe);
		}
	}

	@Override
	public boolean hasQueuedFragments() {
		return countQueuedFragments() > 0;
	}

	@Override
	public boolean isQueuedFragment(String fragmentId) {
		return executeCount(SQL_IS_QUEUED_FRAGMENT, fragmentId) > 0L;
	}

	@Override
	public boolean isProcessedImmutableFragment(String fragmentId) {
		return executeCount(SQL_IS_PROCESSED_IMMUTABLE_FRAGMENT, fragmentId) > 0L;
	}

	@Override
	public boolean isProcessedMutableFragment(String fragmentId) {
		return executeCount(SQL_IS_PROCESSED_MUTABLE_FRAGMENT, fragmentId) > 0L;
	}

	@Override
	public boolean isProcessedMember(String memberId) {
		return memberRepository.isProcessedMember(memberId);
	}

	@Override
	public String nextQueuedFragment() {
		return executeSelectString(SQL_SELECT_NEXT_FRAGMENT_TO_PROCESS);
	}

	@Override
	public String nextExpiredMutableFragment() {
		return executeSelectString(SQL_SELECT_EXPIRED_MUTABLE_FRAGMENT, LocalDateTime.now().toString());
	}

	@Override
	protected void addFragmentToProcess(String fragmentId) {
		executeInsert(SQL_INSERT_FRAGMENT_TO_PROCESS, fragmentId);
	}

	@Override
	protected void removeFragmentToProcess(String fragmentId) {
		executeDelete(SQL_REMOVE_FRAGMENT_TO_PROCESS, fragmentId);
	}

	@Override
	protected void redirectFragmentToProcess(String fragmentId, String redirectedFragmentId) {
		executeUpdate(SQL_RENAME_FRAGMENT_TO_PROCESS, redirectedFragmentId, fragmentId);
	}

	@Override
	protected void addProcessedImmutableFragment(String fragmentId) {
		executeInsert(SQL_INSERT_PROCESSED_IMMUTABLE_FRAGMENT, fragmentId);
	}

	@Override
	protected void removeProcessedImmutableFragment(String fragmentId) {
		executeDelete(SQL_REMOVE_PROCESSED_IMMUTABLE_FRAGMENT, fragmentId);
	}

	@Override
	protected void addProcessedMutableFragment(String fragmentId, LocalDateTime fragmentExpirationDate) {
		executeInsert(SQL_INSERT_PROCESSED_MUTABLE_FRAGMENT, fragmentId, fragmentExpirationDate.toString());
	}

	@Override
	protected LocalDateTime getProcessedMutableFragmentExpirationDate(String fragmentId) {
		String expirationDate = executeSelectString(SQL_SELECT_PROCESSED_MUTABLE_FRAGMENT_EXPIRATION_DATE, fragmentId);

		if (expirationDate != null) {
			return LocalDateTime.parse(expirationDate);
		}

		return null;
	}

	@Override
	protected void removeProcessedMutableFragment(String fragmentId) {
		executeDelete(SQL_REMOVE_PROCESSED_MUTABLE_FRAGMENT, fragmentId);
	}

	@Override
	protected void addProcessedMember(String memberId) {
		memberRepository.addProcessedMember(memberId);
	}

	@Override
	protected void removeProcessedMembers() {
		memberRepository.removeProcessedMembers();
	}

	@Override
	public long countQueuedFragments() {
		return executeCount(SQL_COUNT_FRAGMENTS_TO_PROCESS);
	}

	@Override
	public long countProcessedImmutableFragments() {
		return executeCount(SQL_COUNT_PROCESSED_IMMUTABLE_FRAGMENTS);
	}

	@Override
	public long countProcessedMutableFragments() {
		return executeCount(SQL_COUNT_PROCESSED_MUTABLE_FRAGMENTS);
	}

	@Override
	public long countProcessedMembers() {
		return memberRepository.countProcessedMembers();
	}

	@Override
	public void clearState() {
		executeDeleteAll(FRAGMENTS_TO_PROCESS);
		executeDeleteAll(PROCESSED_IMMUTABLE_FRAGMENTS);
		executeDeleteAll(PROCESSED_MUTABLE_FRAGMENTS);
		removeProcessedMembers();
	}

	private static String createTable(String table, String key) {
		return "CREATE TABLE IF NOT EXISTS " + table + " (" + key + " TEXT UNIQUE)";
	}

	private static String selectFirst(String table) {
		return SELECT_FRAGMENT + table + " ORDER BY ROWID LIMIT 1";
	}

	private static String selectExpired() {
		return SELECT_FRAGMENT + PROCESSED_MUTABLE_FRAGMENTS
				+ " WHERE expiration IS NULL OR expiration < ? ORDER BY ROWID LIMIT 1";
	}

	private static String insert(String table) {
		return "INSERT INTO " + table + " (fragment) VALUES (?)";
	}

	private static String insertIfNotExists(String table, String key) {
		return "INSERT OR IGNORE INTO " + table + " (" + key + ") VALUES (?)";
	}

	private static String delete(String table) {
		return "DELETE FROM " + table + " WHERE fragment = ?";
	}

	private static String deleteAll(String table) {
		return "DELETE FROM " + table;
	}

	private static String count(String table) {
		return SELECT_COUNT + table;
	}

	private static String countForKey(String table, String key) {
		return SELECT_COUNT + table + " WHERE " + key + " = ?";
	}

	private String executeSelectString(String sql) {
		return executeSelectString(sql, null);
	}

	private String executeSelectString(String sql, String key) {
		Connection conn = connect();
		try (PreparedStatement stmt = conn.prepareStatement(sql)) {

			if (key != null) {
				stmt.setString(1, key);
			}

			ResultSet rs = stmt.executeQuery();

			if (rs.next()) {
				return rs.getString(1);
			}

			return null;
		} catch (SQLException e) {
			throw new LdesPersistenceQueryException("Unable to query for string (" + sql + ")", e);
		} finally {
			try {
				conn.close();
			} catch (SQLException e) {
				//
			}
		}
	}

	private void executeInsert(String sql, String key1) {
		executeInsert(sql, key1, null);
	}

	private void executeInsert(String sql, String key1, String key2) {
		executeUpdate(sql, key1, key2);
	}

	private Long executeCount(String sql) {
		return executeCount(sql, null);
	}

	private Long executeCount(String sql, String key) {
		Connection conn = connect();
		try (PreparedStatement stmt = conn.prepareStatement(sql)) {

			if (key != null) {
				stmt.setString(1, key);
			}

			ResultSet rs = stmt.executeQuery();

			if (rs.next()) {
				return rs.getLong(1);
			}

			return 0L;
		} catch (SQLException e) {
			throw new LdesPersistenceQueryException("Unable to count (" + sql + ")", e);
		} finally {
			try {
				conn.close();
			} catch (SQLException e) {
				//
			}
		}
	}

	private void executeDelete(String sql, String key) {
		executeUpdate(sql, key);
	}

	private void executeUpdate(String sql, String key1) {
		executeUpdate(sql, key1, null);
	}

	private void executeUpdate(String sql, String key1, String key2) {
		Connection conn = connect();
		try (PreparedStatement stmt = conn.prepareStatement(sql)) {

			if (key1 != null) {
				stmt.setString(1, key1);
			}

			if (key2 != null) {
				stmt.setString(2, key2);
			}

			stmt.executeUpdate();
		} catch (SQLException e) {
			throw new LdesPersistenceQueryException("Unable to update table (" + sql + ")", e);
		} finally {
			try {
				conn.close();
			} catch (SQLException e) {
				//
			}
		}
	}

	private void executeDeleteAll(String table) {
		Connection conn = connect();
		try (PreparedStatement stmt = conn.prepareStatement(deleteAll(table))) {
			stmt.executeUpdate();
		} catch (SQLException e) {
			throw new LdesPersistenceQueryException("Unable to delete all rows from table " + table, e);
		} finally {
			try {
				conn.close();
			} catch (SQLException e) {
				//
			}
		}
	}
}
