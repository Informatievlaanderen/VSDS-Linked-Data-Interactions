package be.vlaanderen.informatievlaanderen.ldes.ldio.config;

public class KafkaInConfigKeys {

	private KafkaInConfigKeys() {
	}

	public static final String TOPICS = "topics";
	public static final String BOOTSTRAP_SERVERS = "bootstrap-servers";
	public static final String CONTENT_TYPE = "content-type";
	public static final String GROUP_ID = "group-id";
	public static final String AUTO_OFFSET_RESET = "auto-offset-reset";
	public static final String SECURITY_PROTOCOL = "security-protocol";

	public static final String SASL_JAAS_USER = "sasl-jaas-user";
	public static final String SASL_JAAS_PASSWORD = "sasl-jaas-password";

}
