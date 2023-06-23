package be.vlaanderen.informatievlaanderen.ldes.ldi.rmlFunctions;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class WktFunctionsTest {
	WktFunctions wktFunctions = new WktFunctions();

	@Test
	void testPointWkt() {
		String wkt = wktFunctions.toWktFunction("4,289731136 51,18460764", "POINT");
		String expected = "<http://www.opengis.net/def/crs/OGC/1.3/CRS84>POINT(4.289731136 51.18460764)";

		assertEquals(expected, wkt);
	}

	@Test
	void testPointWkt_ShouldBeOnlyTwoCoordinates() {
		assertThrows(IllegalArgumentException.class, () -> {
			wktFunctions.toWktFunction("4,289731136 51,18460764 4,289731436 55,18460764", "POINT");
		});
	}

	@Test
	void testLinestringWkt() {
		String wkt = wktFunctions.toWktFunction("4,289731136 51,18460764 4,289731436 55,18460764",
				"LINESTRING");
		String expected = "<http://www.opengis.net/def/crs/OGC/1.3/CRS84>LINESTRING(4.289731136 51.18460764,4.289731436 55.18460764)";

		assertEquals(expected, wkt);
	}

	@Test
	void testMultipointWkt() {
		String wkt = wktFunctions.toWktFunction("4,289731136 51,18460764 4,289731436 55,18460764",
				"MULTIPOINT");
		String expected = "<http://www.opengis.net/def/crs/OGC/1.3/CRS84>MULTIPOINT(4.289731136 51.18460764,4.289731436 55.18460764)";

		assertEquals(expected, wkt);
	}

	@Test
	void testPolygonWkt() {
		String wkt = wktFunctions.toWktFunction("4,289731136 51,18460764 4,289731436 55,18460764",
				"POLYGON");
		String expected = "<http://www.opengis.net/def/crs/OGC/1.3/CRS84>POLYGON((4.289731136 51.18460764,4.289731436 55.18460764))";

		assertEquals(expected, wkt);
	}

	@Test
	void testLinestringWkt_CoordinatesShouldBeInPairs() {
		assertThrows(IllegalArgumentException.class,
				() -> wktFunctions.toWktFunction("4,289731136 51,18460764 4,289731436", "LINESTRING"));

		assertThrows(IllegalArgumentException.class,
				() -> wktFunctions.toWktFunction("4,289731136 51,18460764 4,289731436", "MULTIPOINT"));

		assertThrows(IllegalArgumentException.class,
				() -> wktFunctions.toWktFunction("4,289731136 51,18460764 4,289731436", "POLYGON"));
	}
}
