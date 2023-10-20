package be.vlaanderen.informatievlaanderen.ldes.ldi.utils;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.locationtech.jts.geom.Coordinate;

import static be.vlaanderen.informatievlaanderen.ldes.ldi.utils.SparqlFunctionsUtils.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class SparqlFunctionsUtilsTest {

	public static final double FIRST_LENGTH = 162.65;
	public static final double SECOND_LENGTH = 95.46;
	public static final int NUMBER_OF_LENGTHS = 2;
	public static final double TOTAL_OF_LENGTHS = 258.12;
	public static final double PRECISION_2_CM = 0.02d;
	Coordinate c1;
	Coordinate c2;
	Coordinate c3;
	Coordinate[] coordinates;

	@BeforeEach
	void init() {
		c1 = new Coordinate(4.4707042959178, 51.1982952024261);
		c2 = new Coordinate(4.47284349591721, 51.1977197024265);
		c3 = new Coordinate(4.47409469591687, 51.1973758024267);
		coordinates = new Coordinate[] { c1, c2, c3 };
	}

	@Test
	void getLineLengthsTest() {

		double[] lengths = getLineLengths(coordinates);

		assertEquals(NUMBER_OF_LENGTHS, lengths.length);
		assertEquals(FIRST_LENGTH, lengths[0], PRECISION_2_CM);
		assertEquals(SECOND_LENGTH, lengths[1], PRECISION_2_CM);
	}

	@Test
	void getTotalLineLengthTest() {

		double length = getTotalLineLength(coordinates);

		assertEquals(TOTAL_OF_LENGTHS, length, PRECISION_2_CM);
	}

	@Test
	void calculateDistanceTest() {

		double distance = calculateDistance(c1.y, c1.x, c2.y, c2.x);

		assertEquals(FIRST_LENGTH, distance, PRECISION_2_CM);
	}
	@Test
	void findOnSegmentByDistanceTest() {

		double distance = 129.059021377d;

		Coordinate expected = new Coordinate(4.472400420184437, 51.197838901231705);

		Coordinate result = findOnSegmentByDistance(c1, c2, distance);

		assertEquals(expected.x, result.x, 0.00004d);
		assertEquals(expected.y, result.y, 0.00004d);
	}
}
