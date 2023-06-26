package be.vlaanderen.informatievlaanderen.ldes;

import be.vlaanderen.informatievlaanderen.ldes.ldi.JsonToLdAdapter;
import be.vlaanderen.informatievlaanderen.ldes.ldi.types.LdiComponent;
import be.vlaanderen.informatievlaanderen.ldes.ldio.configurator.LdioConfigurator;
import be.vlaanderen.informatievlaanderen.ldes.ldio.valueobjects.ComponentProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LdioJsonToLdAdapterAutoConfig {

	@Bean("be.vlaanderen.informatievlaanderen.ldes.ldi.JsonToLdAdapter")
	public LdioConfigurator ldiHttpOutConfigurator() {
		return new LdioSparqlConstructConfigurator();
	}

	public static class LdioSparqlConstructConfigurator implements LdioConfigurator {
		@Override
		public LdiComponent configure(ComponentProperties config) {
			String coreContext = config.getProperty("coreContext");
			String ldContext = config.getOptionalProperty("ldContext").orElse(null);
			return new JsonToLdAdapter(coreContext, ldContext);
		}
	}
}
