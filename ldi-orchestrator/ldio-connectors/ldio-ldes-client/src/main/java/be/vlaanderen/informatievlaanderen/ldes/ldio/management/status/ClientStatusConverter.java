package be.vlaanderen.informatievlaanderen.ldes.ldio.management.status;

import ldes.client.treenodesupplier.domain.valueobject.ClientStatus;
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
public class ClientStatusConverter implements HttpMessageConverter<ClientStatus> {
	@Override
	public boolean canRead(Class<?> clazz, MediaType mediaType) {
		return clazz.equals(ClientStatus.class);
	}

	@Override
	public boolean canWrite(Class<?> clazz, MediaType mediaType) {
		return clazz.equals(ClientStatus.class);
	}

	@Override
	public List<MediaType> getSupportedMediaTypes() {
		return List.of(MediaType.ALL);
	}

	@Override
	public ClientStatus read(Class<? extends ClientStatus> clazz, HttpInputMessage inputMessage) throws IOException, HttpMessageNotReadableException {
		return ClientStatus.valueOf(inputMessage.getBody().toString().toUpperCase());
	}

	@Override
	public void write(ClientStatus clientStatus, MediaType contentType, HttpOutputMessage outputMessage) throws IOException, HttpMessageNotWritableException {
		outputMessage.getBody().write(clientStatus.toString().getBytes(StandardCharsets.UTF_8));
	}
}
