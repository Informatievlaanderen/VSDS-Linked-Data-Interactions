package be.vlaanderen.informatievlaanderen.ldes.ldio.converters;

import be.vlaanderen.informatievlaanderen.ldes.ldio.statusmanagement.pipelinestatus.PipelineStatus;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Component
public class PipelineStatusConverter implements HttpMessageConverter<PipelineStatus.Value> {
	@Override
	public boolean canRead(Class<?> clazz, MediaType mediaType) {
		return clazz.equals(PipelineStatus.Value.class);
	}

	@Override
	public boolean canWrite(Class<?> clazz, MediaType mediaType) {
		return clazz.equals(PipelineStatus.Value.class);
	}

	@Override
	public List<MediaType> getSupportedMediaTypes() {
		return List.of(MediaType.ALL);
	}

	@Override
	public PipelineStatus.Value read(Class<? extends PipelineStatus.Value> clazz, HttpInputMessage inputMessage) throws IOException, HttpMessageNotReadableException {
		return PipelineStatus.Value.valueOf(inputMessage.getBody().toString().toUpperCase());
	}

	@Override
	public void write(PipelineStatus.Value pipelineStatus, MediaType contentType, HttpOutputMessage outputMessage) throws IOException, HttpMessageNotWritableException {
		outputMessage.getBody().write(pipelineStatus.toString().getBytes(StandardCharsets.UTF_8));
	}
}
