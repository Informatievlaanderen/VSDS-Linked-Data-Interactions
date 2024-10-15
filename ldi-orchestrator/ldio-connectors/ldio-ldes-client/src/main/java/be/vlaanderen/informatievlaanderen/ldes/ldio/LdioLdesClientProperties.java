package be.vlaanderen.informatievlaanderen.ldes.ldio;

import be.vlaanderen.informatievlaanderen.ldes.ldio.pipeline.creation.valueobjects.ComponentProperties;
import be.vlaanderen.informatievlaanderen.ldes.ldio.pipeline.exception.ConfigPropertyMissingException;
import be.vlaanderen.informatievlaanderen.ldes.ldio.pipeline.exception.InvalidConfigException;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFLanguages;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;

import static be.vlaanderen.informatievlaanderen.ldes.ldio.LdioLdesClientPropertyKeys.*;
import static be.vlaanderen.informatievlaanderen.ldes.ldio.pipeline.persistence.PersistenceProperties.KEEP_STATE;

public class LdioLdesClientProperties {
	private static final Logger log = LoggerFactory.getLogger(LdioLdesClientProperties.class);
	public static final boolean DEFAULT_KEEP_STATE = false;
	public static final boolean DEFAULT_USE_LATEST_STATE_FILTER = true;
	public static final boolean DEFAULT_EXACTLY_ONCE_ENABLED = true;
	private final ComponentProperties properties;

	private LdioLdesClientProperties(ComponentProperties properties) {
		this.properties = properties;
	}

	public List<String> getUrls() {
		final List<String> urls = properties.getPropertyList(URLS);
		if (urls.isEmpty()) {
			throw new ConfigPropertyMissingException(properties.getPipelineName(), properties.getComponentName(), "urls");
		}
		return urls;
	}

	public String getFirstUrl() {
		return getUrls().getFirst();
	}

	public Lang getSourceFormat() {
		return properties.getOptionalProperty(SOURCE_FORMAT)
				.map(RDFLanguages::nameToLang)
				.orElse(DEFAULT_SOURCE_FORMAT);
	}

	public boolean isExactlyOnceEnabled() {
		return properties.getOptionalBoolean(USE_EXACTLY_ONCE_FILTER)
				.or(() -> getOptionalVersionMaterialisationBoolean().map(isEnabled -> !isEnabled))
				.orElse(DEFAULT_EXACTLY_ONCE_ENABLED);
	}

	public boolean isVersionMaterialisationEnabled() {
		return getOptionalVersionMaterialisationBoolean().orElse(false);
	}

	private Optional<Boolean> getOptionalVersionMaterialisationBoolean() {
		return properties.getOptionalBoolean(USE_VERSION_MATERIALISATION);
	}

	public boolean isKeepStateEnabled() {
		return properties.getOptionalBoolean(KEEP_STATE).orElse(DEFAULT_KEEP_STATE);
	}

	public boolean isLatestStateEnabled() {
		return properties.getOptionalBoolean(USE_LATEST_STATE_FILTER).orElse(DEFAULT_USE_LATEST_STATE_FILTER);
	}

	public ComponentProperties getProperties() {
		return properties;
	}

	public static LdioLdesClientProperties fromComponentProperties(ComponentProperties properties) {
		final LdioLdesClientProperties clientProps = new LdioLdesClientProperties(properties);
		warnWhenVersionMaterialisationIsNotEnabled(clientProps);
		checkIfBothVersionMaterialisationAndExactlyOnceAreExplicitlyEnabled(clientProps);
		logIfExactlyOnceFilterMustBeDisabled(clientProps);
		return clientProps;
	}

	private static void warnWhenVersionMaterialisationIsNotEnabled(LdioLdesClientProperties clientProps) {
		if(clientProps.getOptionalVersionMaterialisationBoolean().isEmpty()) {
			log.atWarn().log("""
					Version-materialization in the LDES Client hasn’t been turned on. Please note that in the future, \
					this will be the default output of the LDES Client \
					and having version-objects as output will have to be configured explicitly.""");
		}
	}

	private static void checkIfBothVersionMaterialisationAndExactlyOnceAreExplicitlyEnabled(LdioLdesClientProperties clientProps) {
		if(clientProps.isVersionMaterialisationEnabled() && clientProps.isExactlyOnceEnabled()) {
			throw new InvalidConfigException("The exactly once filter can not be enabled with version materialisation.");
		}
	}

	private static void logIfExactlyOnceFilterMustBeDisabled(LdioLdesClientProperties clientProps) {
		if(clientProps.isExactlyOnceEnabled()) {
			log.warn("The exactly once filter can not be used while version materialisation is active, disabling filter");
		}
	}
}
