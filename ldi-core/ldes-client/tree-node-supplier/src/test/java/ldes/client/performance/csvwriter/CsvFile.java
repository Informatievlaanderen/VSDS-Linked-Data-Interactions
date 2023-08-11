package ldes.client.performance.csvwriter;

import ldes.client.performance.TestScenario;
import ldes.client.treenodesupplier.repository.filebased.exception.StateOperationFailedException;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class CsvFile {

    private final Set<TestScenario> headers = new TreeSet<>();
    private final Map<Integer, CsvResultLine> results = new TreeMap<>();
    private final String path;

    public CsvFile(String path) {
        this.path = path;
    }

    public void writeToFile() {
        try (BufferedWriter bufferedWriter = Files.newBufferedWriter(Path.of(path))) {
            writeRecord(bufferedWriter, getCsvHeaders());
            results.forEach((key, value) -> writeRecord(bufferedWriter, key + "," + value.toCsv()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String getCsvHeaders() {
        return "counter," + String.join(",", headers.stream().map(TestScenario::name).toList());
    }

    public void addLine(int count, int msInterval, TestScenario test) {
        CsvResultLine csvResultLine = Objects.requireNonNullElseGet(results.get(count), CsvResultLine::new);
        setValueOnCsvLine(msInterval, test, csvResultLine);
        results.put(count, csvResultLine);
    }

    private void setValueOnCsvLine(int value, TestScenario testScenario, CsvResultLine csvResultLine) {
        String stringValue = String.valueOf(value);
        csvResultLine.addValue(testScenario, stringValue);
        headers.add(testScenario);
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
