package be.vlaanderen.informatievlaanderen.ldes.ldi;

import java.util.List;
import java.util.stream.Collectors;

public class WktConverter {

    //https://www.npmjs.com/package/wellknown?activeTab=code
    String stringify(Geometry gj) {

        return switch (gj.type) {
            case POINT -> "POINT (" + pairWKT(gj.coordinates) + ")";
//            case LINESTRING -> "LINESTRING (" + ringWKT(gj.coordinates) + ")";
//            case POLYGON -> "POLYGON (" + ringsWKT(gj.coordinates) + ")";
//            case MULTIPOINT -> "MULTIPOINT (" + ringWKT(gj.coordinates) + ")";
//            case MULTIPOLYGON -> "MULTIPOLYGON (" + multiRingsWKT(gj.coordinates) + ")";
//            case MULTILINESTRING -> "MULTILINESTRING (" + ringsWKT(gj.coordinates) + ")";
            case GEOMETRYCOLLECTION ->
                    "GEOMETRYCOLLECTION (" + gj.geometries.stream().map(this::stringify).collect(Collectors.joining(", ")) + ")";
            default -> null;
        };
    }

    String pairWKT(List<Double> c) {
        return c.stream().map(String::valueOf).collect(Collectors.joining(" "));
    }

    String ringWKT(List<List<Double>> r) {
        return r.stream().map(this::pairWKT).collect(Collectors.joining(", "));
    }

//    String ringsWKT(List<Double> r) {
//        return r.map(ringWKT).map(wrapParens).join(", ");
//    }

//    String multiRingsWKT(List<Double> r) {
//        return r.map(ringsWKT).map(wrapParens).join(", ");
//    }

    String wrapParens(List<Double> s) {
        return "(" + s + ")";
    }

}
