package ldes.client.continuousprocessor;

import be.vlaanderen.informatievlaanderen.ldes.ldi.types.LdiInput;
import ldes.client.treenodeprocessor.fragmentfetcher.TreeMember;
import org.apache.jena.rdf.model.Model;

import java.util.function.Supplier;

public class MemberSupplier implements Supplier<TreeMember>, LdiInput {

    private final Processor processor;

    public MemberSupplier(String string) {
        this.processor = new Processor(string);
    }

    @Override
    public TreeMember get() {
        TreeMember member = processor.getMember();
        System.out.println("\t"+member.getMemberId());
        return member;
    }
}
