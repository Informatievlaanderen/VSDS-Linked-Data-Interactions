package ldes.client.treenodeprocessor.fragmentfetcher;

import org.apache.jena.rdf.model.Model;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TreeNodeFetcherTest {

    @Test
    void test(){
        TreeNodeFetcher treeNodeFetcher = new TreeNodeFetcher();
        Model model = treeNodeFetcher.fetchFragment("https://private-api.gipod.beta-vlaanderen.be/api/v1/ldes/mobility-hindrances?generatedAtTime=2021-10-20T12:05:32.563Z");
        System.out.println();
    }

}