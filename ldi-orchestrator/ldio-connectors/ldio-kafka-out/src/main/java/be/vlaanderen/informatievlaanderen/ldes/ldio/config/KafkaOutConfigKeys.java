package be.vlaanderen.informatievlaanderen.ldes.ldio.config;

public final class KafkaOutConfigKeys {

	private KafkaOutConfigKeys() {
	}

	public static final String TOPIC = "topic";
	public static final String BOOTSTRAP_SERVERS = "bootstrap-servers";
	public static final String CONTENT_TYPE = "content-type";
	public static final String SECURITY_PROTOCOL = "security-protocol";

	public static final String SASL_JAAS_USER = "sasl-jaas-user";
	public static final String SASL_JAAS_PASSWORD = "sasl-jaas-password";

}
