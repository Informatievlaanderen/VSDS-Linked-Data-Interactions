package ldes.client.treenoderelationsfetcher.services;

import ldes.client.treenoderelationsfetcher.domain.valueobjects.LdesStructure;
import ldes.client.treenoderelationsfetcher.domain.valueobjects.TreeRelation;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class LdesRelationWriterTest {

	@Test
	void test_AsString() throws IOException {
		final LdesStructure ldesStructure = initLdesStructure();
		final ClassLoader classLoader = getClass().getClassLoader();
		final File file = new File(Objects.requireNonNull(classLoader.getResource("ldes-structure.txt")).getFile());
		final String expectedOutput = FileUtils.readFileToString(file, StandardCharsets.UTF_8);

		final String actualOutput = ldesStructure.asString();

		assertThat(actualOutput).containsIgnoringNewLines(expectedOutput);
	}

	private LdesStructure initLdesStructure() {
		final LdesStructure ldesStructure = new LdesStructure("https://brugge-ldes.geomobility.eu/observations");
		final TreeRelation byPageRelation = new TreeRelation("https://brugge-ldes.geomobility.eu/observations/by-page", false);
		ldesStructure.addRelation(initByLocationTreeRelation());
		ldesStructure.addRelation(initByTimeTreeRelation());
		ldesStructure.addRelation(byPageRelation);

		return ldesStructure;
	}

	private TreeRelation initByLocationTreeRelation() {
		final TreeRelation byLocationTreeRelation = new TreeRelation("https://brugge-ldes.geomobility.eu/observations/by-location", true);
		final TreeRelation tile000TreeRelation = new TreeRelation("https://brugge-ldes.geomobility.eu/observations/by-location?tile=0/0/0", false);
		final TreeRelation tile1314205TreeRelation = new TreeRelation("https://brugge-ldes.geomobility.eu/observations/by-location?tile=13/14/205", true);
		final TreeRelation tile1316215TreeRelation = new TreeRelation("https://brugge-ldes.geomobility.eu/observations/by-location?tile=13/16/215", true);

		byLocationTreeRelation.addRelation(tile000TreeRelation);
		tile000TreeRelation.addRelation(tile1314205TreeRelation);
		tile000TreeRelation.addRelation(tile1316215TreeRelation);

		return byLocationTreeRelation;
	}

	private TreeRelation initByTimeTreeRelation() {
		final TreeRelation byTimeTreeRelation = new TreeRelation("https://brugge-ldes.geomobility.eu/observations/by-time", true);
		final TreeRelation year2022TreeRelation = new TreeRelation("https://brugge-ldes.geomobility.eu/observations/by-time?year=2022", true);
		byTimeTreeRelation.addRelation(year2022TreeRelation);
		final TreeRelation year2023TreeRelation = new TreeRelation("https://brugge-ldes.geomobility.eu/observations/by-time?year=2023", true);
		byTimeTreeRelation.addRelation(year2023TreeRelation);

		final TreeRelation month01TreeRelation = new TreeRelation("https://brugge-ldes.geomobility.eu/observations/by-time?year=2023&month=01", true);
		year2023TreeRelation.addRelation(month01TreeRelation);
		addDaysToMonthTreeRelation(month01TreeRelation);

		Stream.of("https://brugge-ldes.geomobility.eu/observations/by-time?year=2023&month=05",
						"https://brugge-ldes.geomobility.eu/observations/by-time?year=2023&month=04",
						"https://brugge-ldes.geomobility.eu/observations/by-time?year=2023&month=02"
				)
				.map(uri -> new TreeRelation(uri, false))
				.forEach(year2023TreeRelation::addRelation);

		return byTimeTreeRelation;
	}

	private void addDaysToMonthTreeRelation(TreeRelation monthTreeRelation) {
		final String baseUri = monthTreeRelation.getUri();
		for (int i = 1; i < 25; i += 5) {
			final String dayRelationUri = "%s&day=%02d".formatted(baseUri, i);
			final TreeRelation dayRelation = new TreeRelation(dayRelationUri, false);
			monthTreeRelation.addRelation(dayRelation);
		}
	}

}