package be.vlaanderen.informatievlaanderen.ldes.ldio;

import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.AbstractJackson2HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

/**
 * Configurer that adds the right HttpRequest and HttpResponse body converters to the application
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {
	@Override
	public void configureMessageConverters(List<HttpMessageConverter<?>> messageConverters) {
		messageConverters.add(new YamlJackson2HttpMessageConverter());
		messageConverters.add(new MappingJackson2HttpMessageConverter());
	}

	static final class YamlJackson2HttpMessageConverter extends AbstractJackson2HttpMessageConverter {
		YamlJackson2HttpMessageConverter() {
			super(new YAMLMapper(), MediaType.parseMediaType("application/yaml"));
		}
	}
}


