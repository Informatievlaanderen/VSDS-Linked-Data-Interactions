package be.vlaanderen.informatievlaanderen.ldes.ldi;

import org.apache.jena.datatypes.BaseDatatype;
import org.apache.jena.graph.impl.LiteralLabel;

// TODO: 22/03/2023 cleanup and test
public class WktLiteral extends BaseDatatype {

    public static final String TypeURI = "http://www.opengis.net/ont/geosparql#wktLiteral";

    private final String crs;

    public WktLiteral(String crs) {
        super(WktLiteral.TypeURI);
        this.crs = crs;
    }

    public WktLiteral() {
        this(null);
    }

    /**
     * Convert a value of this datatype out
     * to lexical form.
     */
    public String unparse(Object value) {
        return value.toString();
    }

    /**
     * Parse a lexical form of this datatype to a value
     */
    public Object parse(String lexicalForm) {
        return crs != null
                ? new TypedValue("%s %s".formatted(crs, lexicalForm), this.getURI())
                : new TypedValue(lexicalForm, this.getURI());
    }

    /**
     * Compares two instances of values of the given datatype.
     * This does not allow rationals to be compared to other number
     * formats, Lang tag is not significant.
     *
     * @param value1 First value to compare
     * @param value2 Second value to compare
     * @return Value to determine whether both are equal.
     */
    public boolean isEqual(LiteralLabel value1, LiteralLabel value2) {
        return value1.getDatatype() == value2.getDatatype()
                && value1.getValue().equals(value2.getValue());
    }

}
