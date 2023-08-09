package be.vlaanderen.informatievlaanderen.ldes.ldio.services;

import be.vlaanderen.informatievlaanderen.ldes.ldi.services.ComponentExecutor;
import be.vlaanderen.informatievlaanderen.ldes.ldi.types.LdiTransformer;
import jakarta.annotation.PostConstruct;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFParser;
import org.apache.jena.riot.RDFWriter;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.connection.ReactiveSubscription;
import org.springframework.data.redis.core.ReactiveRedisOperations;
import org.springframework.data.redis.listener.ChannelTopic;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ComponentExecutorImpl implements ComponentExecutor, MessageListener {

	private final ExecutorService executorService;
	private final List<LdiTransformer> ldiTransformers;
	private final LdiSender ldiSender;
	private final ReactiveRedisOperations<String, String> redisTemplate;
	private final String topic = "pipeline";

	public ComponentExecutorImpl(List<LdiTransformer> ldiTransformers, LdiSender ldiSender,
			ReactiveRedisOperations<String, String> redisTemplate) {
		this.redisTemplate = redisTemplate;
		this.executorService = Executors.newSingleThreadExecutor();
		this.ldiTransformers = ldiTransformers;
		this.ldiSender = ldiSender;
	}

	@Override
	public void transformLinkedData(final Model linkedDataModel) {
		redisTemplate.convertAndSend(topic, RDFWriter.source(linkedDataModel).lang(Lang.TTL).asString());
	}

	@PostConstruct
	void listen() {
		this.redisTemplate
				.listenTo(ChannelTopic.of(topic))
				.map(ReactiveSubscription.Message::getMessage)
				.subscribe(s -> {
					Model transformedLinkedDataModel = RDFParser.fromString(s).lang(Lang.TTL).toModel();
					for (LdiTransformer component : ldiTransformers) {
						transformedLinkedDataModel = component.apply(transformedLinkedDataModel);
					}

					ldiSender.accept(transformedLinkedDataModel);
				});
	}

	public List<LdiTransformer> getLdiTransformers() {
		return ldiTransformers;
	}

	public LdiSender getLdiSender() {
		return ldiSender;
	}

	@Override
	public void onMessage(Message message, byte[] pattern) {

	}
}
