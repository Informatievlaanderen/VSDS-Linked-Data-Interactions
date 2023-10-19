package be.vlaanderen.informatievlaanderen.ldes.ldi.formulaUtils;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.locationtech.jts.geom.Coordinate;

import static be.vlaanderen.informatievlaanderen.ldes.ldi.formulaUtils.CoordinateFinder.findOnSegmentByDistance;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class CoordinateFinderTest {

    Coordinate c1;
    Coordinate c2;
    Coordinate c3;
    Coordinate[] coordinates;

    @BeforeEach
    void init() {
        c1 = new Coordinate(4.4707042959178, 51.1982952024261);
        c2 = new Coordinate(4.47284349591721, 51.1977197024265);
        c3 = new Coordinate(4.47409469591687, 51.1973758024267);
        coordinates = new Coordinate[]{c1, c2, c3};
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
