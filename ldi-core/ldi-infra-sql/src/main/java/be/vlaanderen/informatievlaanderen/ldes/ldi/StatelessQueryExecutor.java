package be.vlaanderen.informatievlaanderen.ldes.ldi;

import org.hibernate.StatelessSession;

/**
 * Interface to execute any Hibernate query via a stateless session
 */
@FunctionalInterface
public interface StatelessQueryExecutor {
	/**
	 * @param session StatelessSession where on the Hibernate query must be executed
	 * @return Number of rows affected
	 */
	int execute(StatelessSession session);
}
