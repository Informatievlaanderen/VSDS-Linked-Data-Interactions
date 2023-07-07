package be.vlaanderen.informatievlaanderen.ldes.ldio;

import be.vlaanderen.informatievlaanderen.ldes.ldi.types.LdiOutput;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFWriter;

public class LdioFileOut implements LdiOutput {

    private final MemberFileArchiver memberFileArchiver;

    public LdioFileOut(MemberFileArchiver memberFileArchiver) {
        this.memberFileArchiver = memberFileArchiver;
    }

    @Override
    public void accept(Model model) {
        RDFWriter.source(model).lang(Lang.NQUADS).output(memberFileArchiver.createFilePath(model));
    }

}
