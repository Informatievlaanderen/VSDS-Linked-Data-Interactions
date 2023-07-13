package ldes.client.treenodesupplier.repository.filebased;

import ldes.client.treenodesupplier.repository.filebased.exception.StateOperationFailedException;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.stream.Stream;

import static ldes.client.treenodesupplier.repository.filebased.FileManagerFactory.STATE_DIRECTORY;

public class FileManager {

	public Stream<String> getRecords(String file) {
		if (Path.of(STATE_DIRECTORY, file).toFile().exists()) {
			try {
				return Files.lines(Path.of(STATE_DIRECTORY, file));
			} catch (IOException e) {
				throw new StateOperationFailedException(e);
			}
		}
		return Stream.of();
	}

	public void createNewRecords(String file, Stream<String> records) {
		try {
			if (Path.of(STATE_DIRECTORY, file).toFile().exists()) {
				Files.delete(Path.of(STATE_DIRECTORY, file));
			}
			BufferedWriter bufferedWriter = Files.newBufferedWriter(Path.of(STATE_DIRECTORY, file),
					StandardOpenOption.CREATE_NEW);
			records.forEach(recordToWrite -> writeRecord(bufferedWriter, recordToWrite));
			bufferedWriter.close();
		} catch (IOException e) {
			throw new StateOperationFailedException(e);
		}
	}

	private static void writeRecord(BufferedWriter bufferedWriter, String recordToWrite) {
		try {
			bufferedWriter.write(recordToWrite);
			bufferedWriter.newLine();
		} catch (IOException e) {
			throw new StateOperationFailedException(e);
		}
	}

	public void appendRecord(String file, String recordToAppend) {
		if (Path.of(STATE_DIRECTORY, file).toFile().exists()) {
			try {
				BufferedWriter bufferedWriter = Files.newBufferedWriter(Path.of(STATE_DIRECTORY, file),
						StandardOpenOption.APPEND);
				writeRecord(bufferedWriter, recordToAppend);
				bufferedWriter.close();
			} catch (IOException e) {
				throw new StateOperationFailedException(e);
			}
		} else {
			createNewRecords(file, Stream.of(recordToAppend));
		}
	}
}
