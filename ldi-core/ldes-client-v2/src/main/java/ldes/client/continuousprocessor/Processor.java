package ldes.client.continuousprocessor;

import ldes.client.treenodeprocessor.fragmentfetcher.TreeMember;
import ldes.client.treenodeprocessor.fragmentfetcher.TreeNode;
import ldes.client.treenodeprocessor.fragmentfetcher.TreeNodeProcessor;
import org.apache.jena.rdf.model.Model;

import java.util.Optional;

public class Processor {

    FragmentRepository fragmentRepository = new FragmentRepository();
    MemberRepository memberRepository = new MemberRepository();
    TreeNodeProcessor treeNodeProcessor = new TreeNodeProcessor();

    public Processor(String startingNode) {
        fragmentRepository.addUnProcessedTreeNode(startingNode);
    }

    private void processedTreeNode() {
        String startingNode = fragmentRepository.getUnprocessedTreeNode();
        TreeNode processed = treeNodeProcessor.process(startingNode);
        System.out.println(processed.getTreeNodeId());
        fragmentRepository.processedTreeNode(processed.getTreeNodeId());
        processed.getRelations()
                .stream()
                .filter(relation -> !fragmentRepository.isProcessed(relation))
                .forEach(fragmentRepository::addUnProcessedTreeNode);
        processed.getMembers()
                .stream()
                .filter(member -> !memberRepository.isProcessed(member))
                .forEach(memberRepository::addUnprocessedTreeMember);

    }

    public TreeMember getMember() {
        Optional<TreeMember> unprocessedTreeMember = memberRepository.getUnprocessedTreeMember();
        while (unprocessedTreeMember.isEmpty()) {
            processedTreeNode();
            unprocessedTreeMember = memberRepository.getUnprocessedTreeMember();
        }
        TreeMember treeMember = unprocessedTreeMember.get();
        memberRepository.addProcessedTreeMember(treeMember);
        return treeMember;
    }
}
