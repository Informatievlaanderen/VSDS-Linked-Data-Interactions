package be.vlaanderen.informatievlaanderen.ldes.ldio;

import be.vlaanderen.informatievlaanderen.ldes.ldio.config.JmsConfig;
import org.apache.qpid.jms.JmsConnectionFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.config.JmsListenerEndpointRegistry;
import org.springframework.jms.config.SimpleJmsListenerEndpoint;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class LdioAmqpInRegistrator {

	private final JmsListenerEndpointRegistry jmsListenerEndpointRegistry;

	public LdioAmqpInRegistrator(ApplicationContext applicationContext) {
		this.jmsListenerEndpointRegistry = new JmsListenerEndpointRegistry();
		this.jmsListenerEndpointRegistry.setApplicationContext(applicationContext);
	}

	public void registerListener(JmsConfig jmsConfig, SimpleJmsListenerEndpoint endpoint) {
		jmsListenerEndpointRegistry.registerListenerContainer(endpoint,
				createListenerContainerFactory(jmsConfig.username(), jmsConfig.password(), jmsConfig.remoteUrl()));
		startListener(endpoint.getId());
	}

	public void startListener(String id) {
		Objects.requireNonNull(jmsListenerEndpointRegistry.getListenerContainer(id)).start();
	}

	public String stopListener(String id) {
		Objects.requireNonNull(jmsListenerEndpointRegistry.getListenerContainer(id)).stop();
		return id;
	}

	private DefaultJmsListenerContainerFactory createListenerContainerFactory(String userName, String password, String remoteUrl) {
		DefaultJmsListenerContainerFactory listenerContainerFactory = new DefaultJmsListenerContainerFactory();
		listenerContainerFactory.setConnectionFactory(new JmsConnectionFactory(userName, password, remoteUrl));

		return listenerContainerFactory;
	}
}
