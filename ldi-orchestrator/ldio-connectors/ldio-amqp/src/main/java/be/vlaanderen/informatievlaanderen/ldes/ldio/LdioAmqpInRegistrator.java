package be.vlaanderen.informatievlaanderen.ldes.ldio;

import org.apache.qpid.jms.JmsConnectionFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.config.JmsListenerContainerFactory;
import org.springframework.jms.config.JmsListenerEndpointRegistry;
import org.springframework.jms.config.SimpleJmsListenerEndpoint;

import java.util.Objects;

@Configuration
public class LdioAmqpInRegistrator {

	private final JmsListenerEndpointRegistry jmsListenerEndpointRegistry;

	public LdioAmqpInRegistrator(ApplicationContext applicationContext) {
		this.jmsListenerEndpointRegistry = new JmsListenerEndpointRegistry();
		this.jmsListenerEndpointRegistry.setApplicationContext(applicationContext);
	}

	public String registerListener(String userName, String password, String remoteUrl, SimpleJmsListenerEndpoint endpoint) {
		jmsListenerEndpointRegistry.registerListenerContainer(endpoint, listenerContainerFactory(userName, password, remoteUrl));
		return startListener(endpoint.getId());
	}

	public String registerListener(String remoteUrl, SimpleJmsListenerEndpoint endpoint) {
		jmsListenerEndpointRegistry.registerListenerContainer(endpoint, listenerContainerFactory(remoteUrl));
		return startListener(endpoint.getId());
	}

	public String startListener(String id) {
		Objects.requireNonNull(jmsListenerEndpointRegistry.getListenerContainer(id)).start();
		return id;
	}

	public String stopListener(String id) {
		Objects.requireNonNull(jmsListenerEndpointRegistry.getListenerContainer(id)).stop();
		return id;
	}

	public JmsListenerContainerFactory listenerContainerFactory(String remoteUrl) {
		DefaultJmsListenerContainerFactory listenerContainerFactory = new DefaultJmsListenerContainerFactory();
		listenerContainerFactory.setConnectionFactory(new JmsConnectionFactory(remoteUrl));
		return listenerContainerFactory;
	}

	public JmsListenerContainerFactory listenerContainerFactory(String userName, String password, String remoteUrl) {
		DefaultJmsListenerContainerFactory listenerContainerFactory = new DefaultJmsListenerContainerFactory();
		listenerContainerFactory.setConnectionFactory(new JmsConnectionFactory(userName, password, remoteUrl));

		return listenerContainerFactory;
	}
}
