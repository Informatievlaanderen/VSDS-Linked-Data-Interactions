package be.vlaanderen.informatievlaanderen.ldes.ldi.processors;

import be.vlaanderen.informatievlaanderen.ldes.ldio.ArchiveFileCrawler;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFParser;
import org.apache.nifi.annotation.documentation.CapabilityDescription;
import org.apache.nifi.annotation.documentation.Tags;
import org.apache.nifi.annotation.lifecycle.OnScheduled;
import org.apache.nifi.components.PropertyDescriptor;
import org.apache.nifi.flowfile.FlowFile;
import org.apache.nifi.processor.AbstractProcessor;
import org.apache.nifi.processor.ProcessContext;
import org.apache.nifi.processor.ProcessSession;
import org.apache.nifi.processor.Relationship;
import org.apache.nifi.processor.exception.ProcessException;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Set;

import static be.vlaanderen.informatievlaanderen.ldes.ldi.processors.config.ArchiveFileInProperties.*;
import static be.vlaanderen.informatievlaanderen.ldes.ldi.processors.services.FlowManager.*;

@SuppressWarnings("java:S2160") // nifi handles equals/hashcode of processors
@Tags({ "ldes, vsds, archive, file" })
@CapabilityDescription("Writes members to a file archive.")
public class ArchiveFileInProcessor extends AbstractProcessor {

	private ArchiveFileCrawler archiveFileCrawler;

	@Override
	public Set<Relationship> getRelationships() {
		return Set.of(SUCCESS, FAILURE);
	}

	@Override
	public final List<PropertyDescriptor> getSupportedPropertyDescriptors() {
		return List.of(DATA_SOURCE_FORMAT, ARCHIVE_ROOT_DIR);
	}

	@OnScheduled
	public void onScheduled(final ProcessContext context) {
		final Path archiveDir = Paths.get(getArchiveRootDirectory(context));
		archiveFileCrawler = new ArchiveFileCrawler(archiveDir);
	}

	@Override
	public void onTrigger(ProcessContext context, ProcessSession session) throws ProcessException {
		try {
			final Lang dataSourceFormat = getDataSourceFormat(context);
			archiveFileCrawler.streamArchiveFilePaths().forEach(file -> {
				Model model = RDFParser.source(file).lang(dataSourceFormat).toModel();
				sendRDFToRelation(session, session.create(), model, SUCCESS, dataSourceFormat);
			});
		} catch (Exception e) {
			getLogger().error("Failed to write model to file in archive directory: {}", e.getMessage());
			sendRDFToRelation(session, session.create(), FAILURE);
		}
	}

}
