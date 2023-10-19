package be.vlaanderen.informatievlaanderen.ldes.ldi.formulaUtils;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.locationtech.jts.geom.Coordinate;

import static be.vlaanderen.informatievlaanderen.ldes.ldi.formulaUtils.DistanceCalculator.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class DistanceCalculatorTest {

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
    void getLineLengthsTest() {

        double[] lengths = getLineLengths(coordinates);

        assertEquals(2, lengths.length);
        assertEquals(162.65, lengths[0], 0.02d);
        assertEquals(95.46, lengths[1], 0.02d);
    }

    @Test
    void getTotalLineLengthTest() {

        double length = getTotalLineLength(coordinates);

        assertEquals(258.12, length, 0.02d);
    }

    @Test
    void calculateDistanceTest() {

        double distance = calculateDistance(c1.y, c1.x, c2.y, c2.x);

        assertEquals(162.65, distance, 0.02d);
    }
}
