package be.vlaanderen.informatievlaanderen.ldes.ldio.config;

import be.vlaanderen.informatievlaanderen.ldes.ldi.h2.H2Properties;
import be.vlaanderen.informatievlaanderen.ldes.ldio.pipeline.creation.valueobjects.ComponentProperties;
import ldes.client.treenodesupplier.domain.valueobject.StatePersistence;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class StatePersistenceFactory {
	private final String url;
	private final String username;
	private final String password;

	public StatePersistenceFactory(@Value("${spring.datasource.url}") String url,
	                               @Value("${spring.datasource.username}") String username,
	                               @Value("${spring.datasource.password}") String password) {
		this.url = url;
		this.username = username;
		this.password = password;
	}

	public StatePersistence getStatePersistence(ComponentProperties properties) {
		return StatePersistence.from(createH2Properties(properties), properties.getPipelineName());
	}

	private H2Properties createH2Properties(ComponentProperties properties) {
		return new H2Properties(username, password, url, properties.getPipelineName());
	}

}
