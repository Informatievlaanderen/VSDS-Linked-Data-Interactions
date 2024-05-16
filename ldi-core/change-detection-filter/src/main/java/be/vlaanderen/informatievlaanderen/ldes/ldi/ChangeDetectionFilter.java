package be.vlaanderen.informatievlaanderen.ldes.ldi;

import be.vlaanderen.informatievlaanderen.ldes.ldi.entities.HashedStateMember;
import be.vlaanderen.informatievlaanderen.ldes.ldi.repositories.HashedStateMemberRepository;
import be.vlaanderen.informatievlaanderen.ldes.ldi.types.LdiOneToOneTransformer;
import com.apicatalog.jsonld.http.media.MediaType;
import com.apicatalog.rdf.Rdf;
import com.apicatalog.rdf.RdfDataset;
import com.apicatalog.rdf.io.error.RdfReaderException;
import com.apicatalog.rdf.io.error.UnsupportedContentException;
import com.apicatalog.rdf.io.nquad.NQuadsWriter;
import io.setl.rdf.normalization.RdfNormalize;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFWriter;

import java.io.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

public class ChangeDetectionFilter implements LdiOneToOneTransformer {
	public static final String HASHING_ALGORIHTM = "SHA-256";
	private static final Lang NORMALIZING_LANG = Lang.NQUADS;
	private static final MediaType NORMALIZING_MEDIA_TYPE = MediaType.N_QUADS;
	private final HashedStateMemberRepository hashedStateMemberRepository;
	private final boolean keepState;

	public ChangeDetectionFilter(HashedStateMemberRepository hashedStateMemberRepository, boolean keepState) {
		this.hashedStateMemberRepository = hashedStateMemberRepository;
		this.keepState = keepState;
	}

	/**
	 * Filters out the model by returning an empty model when the model's hash has already been processed
	 * @param model The model to be filtered
	 * @return Either the same model if not processed yet, otherwise an empty model
	 */
	@Override
	public Model transform(Model model) {
		final Resource subject = getSingleNamedNodeFromStateObject(model);
		final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		canonicalizeInputModel(model, outputStream);
		String hashedModel = hashModelBytes(outputStream.toByteArray());
		final HashedStateMember hashedStateMember = new HashedStateMember(subject.getURI(), hashedModel);
		if(hashedStateMemberRepository.containsHashedStateMember(hashedStateMember)) {
			return ModelFactory.createDefaultModel();
		}
		hashedStateMemberRepository.saveHashedStateMember(hashedStateMember);
		return model;
	}

	public void destroyState() {
		if(!keepState) {
			hashedStateMemberRepository.destroyState();
		}
	}


	private Resource getSingleNamedNodeFromStateObject(Model model) {
		final List<Resource> namedNodes = model.listSubjects().filterDrop(RDFNode::isAnon).toList();
		if (namedNodes.size() != 1) {
			throw new IllegalStateException("State object must contain exactly one named node");
		}
		return namedNodes.getFirst();
	}

	private void canonicalizeInputModel(Model model, OutputStream outputStream) {
		final RdfDataset normalisedDataset = RdfNormalize.normalize(readDatasetFromJenaModel(model));
		writeToOutputStream(normalisedDataset, outputStream);
	}

	private RdfDataset readDatasetFromJenaModel(Model model) {
		final ByteArrayOutputStream receivedModelOutputStream = new ByteArrayOutputStream();
		RDFWriter.source(model).lang(NORMALIZING_LANG).output(receivedModelOutputStream);
		final InputStream inputStream = new ByteArrayInputStream(receivedModelOutputStream.toByteArray());
		try {
			return Rdf.createReader(NORMALIZING_MEDIA_TYPE, inputStream).readDataset();
		} catch (UnsupportedContentException | IOException | RdfReaderException e) {
			throw new IllegalStateException("Unable to read the received model", e);
		}
	}

	private void writeToOutputStream(RdfDataset dataset, OutputStream outputStream) {
		final Writer writer = new BufferedWriter(new OutputStreamWriter(outputStream));
		try {
			new NQuadsWriter(writer).write(dataset);
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}

	private String hashModelBytes(byte[] modelBytes) {
		byte[] hashedBytes = getMessageDigest().digest(modelBytes);
		return convertHashedBytesToString(hashedBytes);
	}

	private MessageDigest getMessageDigest() {
		try {
			return MessageDigest.getInstance(HASHING_ALGORIHTM);
		} catch (NoSuchAlgorithmException e) {
			throw new IllegalArgumentException(e);
		}
	}

	private String convertHashedBytesToString(byte[] hashedBytes) {
		final StringBuilder hashStringBuilder = new StringBuilder();
		for (byte b : hashedBytes) {
			String hex = Integer.toHexString(b & 0xFF);
			if (isLeadingZeroRequired(hex)) {
				hashStringBuilder.append("0");
			}
			hashStringBuilder.append(hex);
		}
		return hashStringBuilder.toString();
	}

	private boolean isLeadingZeroRequired(String hex) {
		return hex.length() == 1;
	}
}
