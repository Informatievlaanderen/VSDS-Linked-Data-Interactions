package be.vlaanderen.informatievlaanderen.ldes.ldio.config;

public class AmqpInConfigKeys {

	public static final String CONTENT_TYPE = "content-type";
	public static final String USERNAME = "username";
	public static final String PASSWORD = "password";
	public static final String TOPIC = "topic";
	// Remote url
	public static final String REMOTE_URL = "remote-url";
	public static final String REMOTE_URL_REGEX = "^(amqp|amqps|amqpws|amqpwss)://[a-zA-Z0-9.-]+:\\d+(.*)?$";
	public static final String REMOTE_URL_FORMAT = "amqp[s]://hostname:port[?option=value[&option2=value...]]";
	public static final String REMOTE_URL_WEBSOCKET_FORMAT = "amqpws[s]://hostname:port[/path][?option=value[&option2=value...]]";
	public static final String REMOTE_URL_REGEX_ERROR = "Property %s is not in format of either '%s' or '%s".formatted(REMOTE_URL, REMOTE_URL_FORMAT, REMOTE_URL_WEBSOCKET_FORMAT);
	private AmqpInConfigKeys() {
	}

}
