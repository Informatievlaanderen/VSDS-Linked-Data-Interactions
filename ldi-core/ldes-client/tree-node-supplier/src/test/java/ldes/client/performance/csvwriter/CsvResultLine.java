package ldes.client.performance.csvwriter;

import ldes.client.performance.TestScenario;

import java.util.Map;
import java.util.TreeMap;

public class CsvResultLine {

	private final Map<TestScenario, String> line = new TreeMap<>();

	public String toCsv() {
		return String.join(",", line.values());
	}

	public void addValue(TestScenario testScenario, String stringValue) {
		line.put(testScenario, stringValue);
	}

}
