package be.vlaanderen.informatievlaanderen.ldes.ldi.rmlFunctions;

import io.carml.engine.function.FnoFunction;
import io.carml.engine.function.FnoParam;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class WktFunctions {
	@FnoFunction(LDI.toWktFunction)
	public String toWktFunction(@FnoParam(LDI.coordinates) String coordinates,
			@FnoParam(LDI.wktType) String wktType) {
		List<String> coordinateList = Arrays.stream(coordinates.split("\\s+")).toList();
		StringBuilder wktString = new StringBuilder("<http://www.opengis.net/def/crs/OGC/1.3/CRS84>");

		coordinateList = coordinateList.stream()
				.map(s -> s.replace(',', '.'))
				.collect(Collectors.toList());

		switch (wktType) {
			case ("POINT") -> {
				if (coordinateList.size() != 2) {
					throw new IllegalArgumentException(
							"Expected 2 coordinates. Got %d elements".formatted(coordinateList.size()));
				}
				wktString.append("%s(%s %s)".formatted(wktType, coordinateList.get(0), coordinateList.get(1)));
			}
			case "LINESTRING", "MULTIPOINT" -> {
				if (coordinateList.size() % 2 != 0) {
					throw new IllegalArgumentException(
							"Expected pairs coordinates. Got %d elements".formatted(coordinateList.size()));
				}
				wktString.append("%s(".formatted(wktType));
				wktString.append(createPointGroupString(coordinateList));
				wktString.append(")");
			}
			case "POLYGON" -> {
				if (coordinateList.size() % 2 != 0) {
					throw new IllegalArgumentException(
							"Expected pairs coordinates. Got %d elements".formatted(coordinateList.size()));
				}
				wktString.append("%s((".formatted(wktType));
				wktString.append(createPointGroupString(coordinateList));
				wktString.append("))");
			}
		}

		return wktString.toString();
	}

	private String createPointGroupString(List<String> coordinates) {
		StringBuilder wktString = new StringBuilder();
		for (int i = 0; i < coordinates.size(); i += 2) {
			wktString.append("%s %s,".formatted(coordinates.get(i), coordinates.get(i + 1)));
		}
		wktString.deleteCharAt(wktString.length() - 1);
		return wktString.toString();
	}

}
