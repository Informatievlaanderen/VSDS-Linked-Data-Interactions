package be.vlaanderen.informatievlaanderen.ldes.ldio;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ArchiveFileTest {

    private FileName fileName;
    private ArchiveDirectory archiveDirectory;

    @BeforeEach
    void setUp() {
        fileName = mock(FileName.class);
        archiveDirectory = mock(ArchiveDirectory.class);
    }

    @Test
    void test_getFilePath() {
        String filePath = "filePath";
        when(fileName.getFilePath()).thenReturn(filePath);

        String result = new ArchiveFile(fileName, archiveDirectory).getFilePath();

        assertEquals(filePath, result);
    }

    @Test
    void test_getDirectoryPath() {
        String directoryPath = "directoryPath";
        when(archiveDirectory.getDirectory()).thenReturn(directoryPath);

        Path result = new ArchiveFile(fileName, archiveDirectory).getDirectoryPath();

        assertEquals(Paths.get(directoryPath), result);
    }

}