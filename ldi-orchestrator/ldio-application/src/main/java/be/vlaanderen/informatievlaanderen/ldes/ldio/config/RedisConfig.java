package be.vlaanderen.informatievlaanderen.ldes.ldio.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class RedisConfig {
	@Bean
	JedisConnectionFactory jedisConnectionFactory() {
		return new JedisConnectionFactory();
	}

	@Bean
	public RedisTemplate<String, Object> redisTemplate() {
		RedisTemplate<String, Object> template = new RedisTemplate<>();
		template.setConnectionFactory(jedisConnectionFactory());
		return template;
	}

	@Bean
	MessageListenerAdapter messageListener() {
		return new MessageListenerAdapter(new RedisMessageSubscriber());
	}

	@Service
	public class RedisMessageSubscriber implements MessageListener {

		public static List<String> messageList = new ArrayList<String>();

		public void onMessage(final Message message, final byte[] pattern) {
			messageList.add(message.toString());
			System.out.println("Message received: " + new String(message.getBody()));
		}
	}

	@Bean
	RedisMessageListenerContainer redisContainer() {
		RedisMessageListenerContainer container
				= new RedisMessageListenerContainer();
		container.setConnectionFactory(jedisConnectionFactory());
		container.addMessageListener(messageListener(), topic());
		return container;
	}

	@Bean
	ChannelTopic topic() {
		return new ChannelTopic("messageQueue");
	}

	public interface MessagePublisher {
		void publish(String message);
	}

	public class RedisMessagePublisher implements MessagePublisher {

		@Autowired
		private RedisTemplate<String, Object> redisTemplate;
		@Autowired
		private ChannelTopic topic;

		public RedisMessagePublisher() {
		}

		public RedisMessagePublisher(
				RedisTemplate<String, Object> redisTemplate, ChannelTopic topic) {
			this.redisTemplate = redisTemplate;
			this.topic = topic;
		}

		public void publish(String message) {
			redisTemplate.convertAndSend(topic.getTopic(), message);
		}
	}

	@Bean
	MessagePublisher redisPublisher() {
		return new RedisMessagePublisher(redisTemplate(), topic());
	}
}
