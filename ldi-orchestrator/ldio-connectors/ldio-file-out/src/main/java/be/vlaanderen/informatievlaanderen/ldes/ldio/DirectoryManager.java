package be.vlaanderen.informatievlaanderen.ldes.ldio;

import org.apache.jena.rdf.model.Model;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;

public class DirectoryManager {

    private final String basePath;

    public DirectoryManager(String basePath) {
        this.basePath = basePath;
    }

    public String createDirectoryForArchiving(LocalDateTime memberTimestamp) {
        // TODO: 07/07/23
        https://stackoverflow.com/questions/28947250/create-a-directory-if-it-does-not-exist-and-then-create-the-files-in-that-direct
//        Files.createDirectories(Paths.get("/Your/Path/Here"));
        return "%s/%s/%s/%s/".formatted(basePath, memberTimestamp.getYear(), memberTimestamp.getMonth(), memberTimestamp.getDayOfMonth());
    }

}
