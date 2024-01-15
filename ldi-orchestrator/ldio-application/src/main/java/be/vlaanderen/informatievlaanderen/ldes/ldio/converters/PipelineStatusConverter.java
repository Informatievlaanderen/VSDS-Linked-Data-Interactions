package be.vlaanderen.informatievlaanderen.ldes.ldio.converters;

import be.vlaanderen.informatievlaanderen.ldes.ldio.valueobjects.PipelineStatus;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class PipelineStatusConverter implements HttpMessageConverter<PipelineStatus> {
	@Override
	public boolean canRead(Class<?> clazz, MediaType mediaType) {
		return false;
	}

	@Override
	public boolean canWrite(Class<?> clazz, MediaType mediaType) {
		return true;
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
