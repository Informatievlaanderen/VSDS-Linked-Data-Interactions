package be.vlaanderen.informatievlaanderen.ldes.ldi;

import be.vlaanderen.informatievlaanderen.ldes.ldi.types.LdiOneToOneTransformer;
import com.apicatalog.jsonld.http.media.MediaType;
import com.apicatalog.rdf.Rdf;
import com.apicatalog.rdf.RdfDataset;
import com.apicatalog.rdf.io.error.RdfReaderException;
import com.apicatalog.rdf.io.error.UnsupportedContentException;
import com.apicatalog.rdf.io.nquad.NQuadsWriter;
import io.setl.rdf.normalization.RdfNormalize;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFWriter;

import java.io.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

public class ChangeDetectionFilter implements LdiOneToOneTransformer {
	@Override
	public Model transform(Model model) {
		final Resource subject = getSingleNamedNodeFromStateObject(model);
		final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		canonicalizeInputModel(model, outputStream);
		String hashedModel = hashModelBytes(outputStream.toByteArray());
		return null;
	}


	private Resource getSingleNamedNodeFromStateObject(Model model) {
		final List<Resource> namedNodes = model.listSubjects().filterDrop(RDFNode::isAnon).toList();
		if(namedNodes.size() != 1) {
			throw new IllegalStateException("State object must contain exactly one named node");
		}
		return namedNodes.getFirst();
	}

	private void canonicalizeInputModel(Model model, OutputStream outputStream) {
		final RdfDataset normalisedDataset = RdfNormalize.normalize(readDatasetFromJenaModel(model));
		final Writer writer = new BufferedWriter(new OutputStreamWriter(outputStream));
		try {
			new NQuadsWriter(writer).write(normalisedDataset);
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}

	}

	private RdfDataset readDatasetFromJenaModel(Model model) {
		final ByteArrayOutputStream receivedModelOutputStream = new ByteArrayOutputStream();
		RDFWriter.source(model).lang(Lang.NQUADS).output(receivedModelOutputStream);
		final InputStream inputStream = new ByteArrayInputStream(receivedModelOutputStream.toByteArray());
		try {
			return Rdf.createReader(MediaType.N_QUADS, inputStream).readDataset();
		} catch (UnsupportedContentException | IOException | RdfReaderException e) {
			throw new IllegalStateException("Unable to read the received model", e);
		}
	}


	private String hashModelBytes(byte[] modelBytes) {
		try {
			final StringBuilder hashStringBuilder = new StringBuilder();
			byte[] hashedBytes = MessageDigest.getInstance("SHA-256").digest(modelBytes);

			for (byte b : hashedBytes) {
				String hex = Integer.toHexString(b & 0xFF);
				if(isLeadingZeroRequired(hex)) {
					hashStringBuilder.append("0");
				}
				hashStringBuilder.append(hex);
			}

			return hashStringBuilder.toString();
		} catch (NoSuchAlgorithmException e) {
			throw new IllegalArgumentException(e);
		}
	}

	private boolean isLeadingZeroRequired(String hex) {
		return hex.length() == 1;
	}
}
