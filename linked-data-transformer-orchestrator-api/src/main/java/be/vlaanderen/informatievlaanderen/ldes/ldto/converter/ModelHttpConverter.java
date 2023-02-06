package be.vlaanderen.informatievlaanderen.ldes.ldto.converter;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.AbstractHttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;

import java.io.IOException;
import java.io.OutputStream;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

import static be.vlaanderen.informatievlaanderen.ldes.ldto.converter.RdfModelConverter.fromString;
import static be.vlaanderen.informatievlaanderen.ldes.ldto.converter.RdfModelConverter.getLang;
import static org.apache.jena.riot.RDFFormat.NQUADS;

public class ModelHttpConverter extends AbstractHttpMessageConverter<Model> {

	public ModelHttpConverter() {
		super(MediaType.ALL);
	}

	@Override
	protected boolean supports(Class<?> clazz) {
		return clazz.isAssignableFrom(Model.class);
	}

	@Override
	protected Model readInternal(Class<? extends Model> clazz, HttpInputMessage inputMessage)
			throws IOException, HttpMessageNotReadableException {
		Lang lang = getLang(Objects.requireNonNull(inputMessage.getHeaders().getContentType()));
		return fromString(new String(inputMessage.getBody().readAllBytes(), StandardCharsets.UTF_8), lang);
	}

	@Override
	protected void writeInternal(Model model, HttpOutputMessage outputMessage)
			throws UnsupportedOperationException, HttpMessageNotWritableException, IOException {
		StringWriter outputStream = new StringWriter();

		RDFDataMgr.write(outputStream, model, NQUADS);

		OutputStream body = outputMessage.getBody();
		body.write(outputStream.toString().getBytes());
	}
}
