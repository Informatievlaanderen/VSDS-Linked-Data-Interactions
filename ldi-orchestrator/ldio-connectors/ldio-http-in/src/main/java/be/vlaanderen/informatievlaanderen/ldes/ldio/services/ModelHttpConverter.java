package be.vlaanderen.informatievlaanderen.ldes.ldio.services;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.riot.RDFParserBuilder;
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

import static java.util.Optional.ofNullable;
import static org.apache.jena.riot.Lang.NQUADS;
import static org.apache.jena.riot.Lang.TURTLE;
import static org.apache.jena.riot.RDFLanguages.nameToLang;

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

	public static Lang getLang(MediaType contentType) {
		if (contentType.equals(MediaType.TEXT_HTML))
			return TURTLE;
		return ofNullable(nameToLang(contentType.getType() + "/" + contentType.getSubtype()))
				.orElseGet(() -> ofNullable(nameToLang(contentType.getSubtype()))
						.orElseThrow());
	}

	public static Model fromString(final String content, final Lang lang) {
		return RDFParserBuilder.create().fromString(content).lang(lang).toModel();
	}

	public static String toString(final Model model, final Lang lang) {
		StringWriter stringWriter = new StringWriter();
		RDFDataMgr.write(stringWriter, model, lang);
		return stringWriter.toString();
	}
}
