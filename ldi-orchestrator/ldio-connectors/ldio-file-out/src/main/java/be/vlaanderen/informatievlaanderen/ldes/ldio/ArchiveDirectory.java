package be.vlaanderen.informatievlaanderen.ldes.ldio;

import java.time.LocalDateTime;

public class ArchiveDirectory {

    private final String basePath;
    private final LocalDateTime memberTimestamp;

    public ArchiveDirectory(String basePath, LocalDateTime memberTimestamp) {
        this.basePath = basePath;
        this.memberTimestamp = memberTimestamp;
    }

    // TODO: 07/07/23 test
    // TODO: 07/07/23 javadoc
    public String getDirectory() {
        // TODO: 07/07/23 implement
        // TODO: 07/07/23 create new dir when necessary
        https://stackoverflow.com/questions/28947250/create-a-directory-if-it-does-not-exist-and-then-create-the-files-in-that-direct
//        Files.createDirectories(Paths.get("/Your/Path/Here"));
        return "%s/%s/%s/%s/".formatted(basePath, memberTimestamp.getYear(), memberTimestamp.getMonth(), memberTimestamp.getDayOfMonth());
    }

}
