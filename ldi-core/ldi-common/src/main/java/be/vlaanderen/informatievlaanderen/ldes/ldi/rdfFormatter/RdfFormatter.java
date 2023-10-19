package be.vlaanderen.informatievlaanderen.ldes.ldi.rdfFormatter;

import com.github.jsonldjava.core.JsonLdOptions;
import org.apache.commons.cli.MissingArgumentException;
import org.apache.jena.atlas.json.JsonObject;
import org.apache.jena.atlas.json.io.parser.JSONParser;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.JsonLDWriteContext;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFFormat;
import org.apache.jena.riot.RDFWriter;
import org.apache.jena.riot.writer.JsonLD10Writer;
import org.apache.jena.sparql.util.Context;

import static be.vlaanderen.informatievlaanderen.ldes.ldi.rdfFormatter.PrefixAdder.*;

public class RdfFormatter {
    public static String formatModel(Model model, Lang lang, String frameType) throws IllegalArgumentException {

        if (lang == Lang.JSONLD) {
            if (frameType == null) {
                throw new IllegalArgumentException("No frameType was given");
            }
            return RDFWriter.source(addPrefixesToModel(model))
                    .context(getFramedContext(model, frameType))
                    .format(RDFFormat.JSONLD10_FRAME_PRETTY)
                    .asString();
        }
        else {
            return RDFWriter.source(model)
                    .lang(lang)
                    .asString();
        }
    }

    protected static Context getFramedContext(Model model, String frameType) {
        JsonLDWriteContext jenaCtx = new JsonLDWriteContext();

        JsonObject context = new JsonObject();
        model.getGraph().getPrefixMapping().getNsPrefixMap().forEach(context::put);

        JsonObject frame = new JsonObject();
        frame.put("@context", context);
        frame.put("@type", frameType);
        jenaCtx.set(JsonLD10Writer.JSONLD_FRAME, frame.toString());

        JsonLdOptions jsonLdOptions = new JsonLdOptions();
        jsonLdOptions.setOmitGraph(true);
        jenaCtx.setOptions(jsonLdOptions);

        return jenaCtx;
    }
}
