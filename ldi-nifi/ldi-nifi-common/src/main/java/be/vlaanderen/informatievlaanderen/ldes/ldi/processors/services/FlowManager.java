package be.vlaanderen.informatievlaanderen.ldes.ldi.processors.services;

import be.vlaanderen.informatievlaanderen.ldes.ldi.processors.exceptions.ContentRetrievalException;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.riot.RDFParser;
import org.apache.nifi.flowfile.FlowFile;
import org.apache.nifi.flowfile.attributes.CoreAttributes;
import org.apache.nifi.processor.ProcessSession;
import org.apache.nifi.processor.Relationship;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.StringWriter;

public class FlowManager {

	private static final Logger LOGGER = LoggerFactory.getLogger(FlowManager.class);

	private static Long counter = -1L;

	private FlowManager() {
	}

	public static String receiveData(ProcessSession session, FlowFile flowFile) {
		return receiveData(session, flowFile, new ByteArrayOutputStream());
	}

	public static String receiveData(ProcessSession session, FlowFile flowFile, ByteArrayOutputStream baos) {
		if (flowFile == null) {
			return null;
		}

		try {
			session.exportTo(flowFile, baos);
			baos.close();

			return baos.toString();
		} catch (Exception e) {
			throw new ContentRetrievalException(flowFile.getId(), e);
		}
	}

	public static Model receiveDataAsModel(ProcessSession session, FlowFile flowFile, Lang sourceFormat) {
		ByteArrayOutputStream rdfStream = new ByteArrayOutputStream();
		session.exportTo(flowFile, rdfStream);

		return RDFParser
				.source(new ByteArrayInputStream(rdfStream.toByteArray()))
				.lang(sourceFormat)
				.build()
				.toModel();
	}

	public static void sendRDFToRelation(ProcessSession session, String data, Relationship relationship, Lang lang) {
		sendRDFToRelation(session, session.create(), data, relationship, lang.getContentType().toHeaderString());
	}

	public static void sendRDFToRelation(ProcessSession session, FlowFile flowFile, String data,
			Relationship relationship, Lang lang) {
		sendRDFToRelation(session, flowFile, data, relationship, lang.getContentType().toHeaderString());
	}

	public static void sendRDFToRelation(ProcessSession session, String data, Relationship relationship,
			String contentType) {
		sendRDFToRelation(session, session.create(), data, relationship, contentType);
	}

	public static void sendRDFToRelation(ProcessSession session, FlowFile flowFile, Relationship relationship) {
		sendRDFToRelation(session, flowFile, null, relationship, (String) null);
	}

	public static void sendRDFToRelation(ProcessSession session, FlowFile flowFile, String data,
			Relationship relationship, String contentType) {

		if (data != null) {
			session.write(flowFile, out -> out.write(data.getBytes()));
			session.putAttribute(flowFile, CoreAttributes.MIME_TYPE.key(), contentType);
		}

		session.transfer(flowFile, relationship);

		counter++;

		LOGGER.debug("TRANSFER: sent member #{} (content-type: {})", counter, contentType);
	}

	public static void sendRDFToRelation(ProcessSession session, FlowFile flowFile, Model model,
			Relationship relationship, Lang dataDestinationFormat) {
		StringWriter out = new StringWriter();
		RDFDataMgr.write(out, model, dataDestinationFormat);

		sendRDFToRelation(session, flowFile, out.toString(), relationship, dataDestinationFormat);
	}

	public static final Relationship SUCCESS = new Relationship.Builder()
			.name("success")
			.description("Success relationship")
			.build();

	public static final Relationship FAILURE = new Relationship.Builder()
			.name("failure")
			.description("Failure relationship")
			.build();
}
