package be.vlaanderen.informatievlaanderen.ldes.ldi.valueobjects;

import java.util.Arrays;
import java.util.Optional;

public enum GeoType {

	// TODO TVB: 15/09/23 verify with ranko these uri's are OK as predicate
	// TODO TVB: 15/09/23 IF NOT, we have to add urls with http://www.opengis.net/ont/sf#
	//  DO NOT REMOVE CURRENT uri, but add second uri
	// @formatter:off
    POINT("https://purl.org/geojson/vocab#Point"),
    LINESTRING("https://purl.org/geojson/vocab#LineString"),
    POLYGON("https://purl.org/geojson/vocab#Polygon"),
    MULTIPOINT("https://purl.org/geojson/vocab#MultiPoint"),
    MULTIPOLYGON("https://purl.org/geojson/vocab#MultiPolygon"),
    MULTILINESTRING("https://purl.org/geojson/vocab#MultiLineString"),
    GEOMETRYCOLLECTION("https://purl.org/geojson/vocab#GeometryCollection");
	// @formatter:on

	final String uri;

	GeoType(String uri) {
		this.uri = uri;
	}

	public static Optional<GeoType> fromUri(String type) {
		return Arrays.stream(values()).filter(value -> value.uri.equals(type)).findFirst();
	}

	// TODO TVB: 15/09/23 test
	public static Optional<GeoType> fromName(String name) {
		if (name == null) {
			return Optional.empty();
		}

		return Arrays.stream(values()).filter(value -> value.name().equalsIgnoreCase(name)).findFirst();
	}

	public String getUri() {
		return uri;
	}
}
