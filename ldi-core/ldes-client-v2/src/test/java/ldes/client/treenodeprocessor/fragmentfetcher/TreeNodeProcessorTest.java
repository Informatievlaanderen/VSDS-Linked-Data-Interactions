package ldes.client.treenodeprocessor.fragmentfetcher;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TreeNodeProcessorTest {

    @Test
    void test(){
        TreeNodeProcessor treeNodeProcessor = new TreeNodeProcessor();
        TreeNode process = treeNodeProcessor.process("https://private-api.gipod.beta-vlaanderen.be/api/v1/ldes/mobility-hindrances?generatedAtTime=2021-10-20T12:05:32.563Z");
        System.out.println();
    }

}