package be.vlaanderen.informatievlaanderen.ldes.ldi.datasetsplitter;

import java.util.List;

public class DatasetSplitters {
	private DatasetSplitters() {
	}

	public static DatasetSplitter preventSplitting() {
		return dataset -> List.of(dataset.getUnionModel());
	}

	public static DatasetSplitter splitByNamedGraph() {
		return new NamedGraphSplitter();
	}
}
