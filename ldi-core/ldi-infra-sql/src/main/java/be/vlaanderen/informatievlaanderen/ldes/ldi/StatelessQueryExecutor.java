package be.vlaanderen.informatievlaanderen.ldes.ldi;

import org.hibernate.StatelessSession;

@FunctionalInterface
public interface StatelessQueryExecutor {
	int execute(StatelessSession session);
}
