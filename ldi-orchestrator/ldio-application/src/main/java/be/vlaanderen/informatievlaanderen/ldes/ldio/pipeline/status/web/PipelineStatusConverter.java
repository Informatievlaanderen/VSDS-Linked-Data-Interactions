package be.vlaanderen.informatievlaanderen.ldes.ldio.pipeline.status.web;

import be.vlaanderen.informatievlaanderen.ldes.ldio.pipeline.status.PipelineStatus;
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
public class PipelineStatusConverter implements HttpMessageConverter<PipelineStatus> {
	@Override
	public boolean canRead(Class<?> clazz, MediaType mediaType) {
		return clazz.equals(PipelineStatus.class);
	}

	@Override
	public boolean canWrite(Class<?> clazz, MediaType mediaType) {
		return clazz.equals(PipelineStatus.class);
	}

	@Override
	public List<MediaType> getSupportedMediaTypes() {
		return List.of(MediaType.ALL);
	}

	@Override
	public PipelineStatus read(Class<? extends PipelineStatus> clazz, HttpInputMessage inputMessage) throws IOException, HttpMessageNotReadableException {
		return PipelineStatus.valueOf(inputMessage.getBody().toString().toUpperCase());
	}

	@Override
	public void write(PipelineStatus pipelineStatus, MediaType contentType, HttpOutputMessage outputMessage) throws IOException, HttpMessageNotWritableException {
		outputMessage.getBody().write(pipelineStatus.toString().getBytes(StandardCharsets.UTF_8));
	}
}
