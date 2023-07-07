package be.vlaanderen.informatievlaanderen.ldes.ldio;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class MemberFileArchiver {

    private static final DateTimeFormatter fileNameFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd-HH-mm-ss-SSSSSSSSS");

    private final LocalDateTimeConverter localDateTimeConverter;
    private final String basePath;
    private final Property timestampPath;

    public MemberFileArchiver(LocalDateTimeConverter localDateTimeConverter, String basePath, Property timestampPath) {
        this.localDateTimeConverter = localDateTimeConverter;
        this.basePath = basePath;
        this.timestampPath = timestampPath;
    }

    public String createFilePath(Model model) {
        LocalDateTime timestamp = extractTimestamp(model);
        String baseFileName = timestamp.format(fileNameFormatter);
        return buildFilePath(baseFileName);
    }

    private LocalDateTime extractTimestamp(Model model) {
        var timestamp = model
                .listObjectsOfProperty(timestampPath)
                .filterDrop(node -> !node.isLiteral())
                .mapWith(RDFNode::asLiteral)
                .nextOptional()
                .orElseThrow(() -> new IllegalArgumentException("No timestamp as literal found on member"));

        return localDateTimeConverter.getLocalDateTime(timestamp);
    }

    /**
     * Filenames need to be unique.
     * If there are multiple members with the same timestamp we add an index after the name.
     *
     * @return the full filePath
     * Example unique filename:
     * base-path/2023-12-12-05-05-00-000000000.nq
     * <p>
     * Example when three members have this timestamp:
     * base-path/2023-12-12-05-05-00-000000000.nq
     * base-path/2023-12-12-05-05-00-000000000-1.nq
     * base-path/2023-12-12-05-05-00-000000000-2.nq
     *
     */
    private String buildFilePath(String fileName) {
        String filePathString = basePath + "/" + fileName + ".nq";
        if (!Files.isReadable(Paths.get(filePathString))) {
            return filePathString;
        }

        int count = 1;
        while (Files.isReadable(Paths.get(filePathFrom(basePath, fileName, count)))) {
            count++;
        }
        return filePathFrom(basePath, fileName, count);
    }

    private String filePathFrom(String basePath, String fileName, int count) {
        return basePath + "/" + fileName + "-" + count + ".nq";
    }

}
