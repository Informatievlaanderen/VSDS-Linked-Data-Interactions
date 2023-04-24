package be.vlaanderen.informatievlaanderen.ldes.ldio.types;

import java.util.List;
import java.util.Queue;

public record LdiOrder(Queue<String> transformations, List<String> outputs) {
}
