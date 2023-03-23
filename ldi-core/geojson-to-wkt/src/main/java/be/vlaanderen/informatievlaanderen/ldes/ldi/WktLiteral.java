package be.vlaanderen.informatievlaanderen.ldes.ldi;

import org.apache.jena.datatypes.BaseDatatype;
import org.apache.jena.graph.impl.LiteralLabel;

// TODO: 22/03/2023 test
public class WktLiteral extends BaseDatatype {

	private final String crs;

	public WktLiteral(String crs) {
		super("http://www.opengis.net/ont/geosparql#wktLiteral");
		this.crs = crs;
	}

	public WktLiteral() {
		this(null);
	}

	public String unparse(Object value) {
		return value.toString();
	}

	public Object parse(String lexicalForm) {
		return crs != null
				? new TypedValue("%s %s".formatted(crs, lexicalForm), this.getURI())
				: new TypedValue(lexicalForm, this.getURI());
	}

	public boolean isEqual(LiteralLabel value1, LiteralLabel value2) {
		return value1.getDatatype() == value2.getDatatype()
				&& value1.getValue().equals(value2.getValue());
	}

}
