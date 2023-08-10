package ldes.client.performance.csvwriter;

import ldes.client.performance.PerformanceTest;
import ldes.client.treenodesupplier.repository.filebased.exception.StateOperationFailedException;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;

public class CsvFile {

    private final Map<Integer, CsvResultLine> results = new TreeMap<>();

    public void writeToFile(String path) {
        try (BufferedWriter bufferedWriter = Files.newBufferedWriter(Path.of(path))) {
            writeRecord(bufferedWriter, CsvResultLine.getCsvHeaders());
            results.forEach((key, value) -> writeRecord(bufferedWriter, key + "," + value.toCsv()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void addLine(int count, int msInterval, PerformanceTest test) {
        CsvResultLine csvResultLine = Objects.requireNonNullElseGet(results.get(count), CsvResultLine::new);
        setValueOnCsvLine(msInterval, test, csvResultLine);
        results.put(count, csvResultLine);
    }

    private static void setValueOnCsvLine(int value, PerformanceTest testType, CsvResultLine csvResultLine) {
        switch (testType) {
            case SQLITE10 -> csvResultLine.setSqlite10(value);
            case SQLITE100 -> csvResultLine.setSqlite100(value);
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


}
