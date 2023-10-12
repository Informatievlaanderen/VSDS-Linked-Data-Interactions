package be.vlaanderen.informatievlaanderen.ldes.ldi;

import org.apache.jena.sparql.expr.NodeValue;
import org.apache.jena.sparql.function.FunctionBase2;

public class FirstCoordinate extends FunctionBase2 {
	public static final String name = "https://w3id.org/tree#firstCoordinate";

	@Override
	public NodeValue exec(NodeValue wktLiteral, NodeValue index) {
		/*
		if (index == null) {
			index = NodeValue.makeInteger(0);
		}
		 */
		float f1 = wktLiteral.getFloat();
		float f2 = index.getFloat();
		float f = f1 + f2;
		return NodeValue.makeFloat(f);
	}
}
