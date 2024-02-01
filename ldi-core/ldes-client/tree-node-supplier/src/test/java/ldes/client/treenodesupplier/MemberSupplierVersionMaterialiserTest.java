package ldes.client.treenodesupplier;

import be.vlaanderen.informatievlaanderen.ldes.ldi.VersionMaterialiser;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.riot.RDFParser;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

class MemberSupplierVersionMaterialiserTest {

    @Test
    void testVersionMaterialiseMemberOnly() {
        Model initModel = ModelFactory.createDefaultModel();

        VersionMaterialiser versionMaterialiser = new VersionMaterialiser(
                initModel.createProperty("http://purl.org/dc/terms/isVersionOf"), true);

        Model versionedMember = RDFParser.source("__files/ldes-member-versioned.ttl").toModel();

        Model output = versionMaterialiser.transform(versionedMember);

        Model comparisonModel =
                RDFParser.source("__files/ldes-member-unversioned.ttl").toModel();

        assertTrue(output.isIsomorphicWith(comparisonModel));
    }

    @Test
    void testVersionMaterialiseWithContext() {
        Model initModel = ModelFactory.createDefaultModel();

        VersionMaterialiser versionMaterialiser = new VersionMaterialiser(
                initModel.createProperty("http://purl.org/dc/terms/isVersionOf"), false);

        Model versionedMember = RDFParser.source("__files/ldes-member-versioned.ttl").toModel();

        Model output = versionMaterialiser.transform(versionedMember);

        Model comparisonModel =
                RDFParser.source("__files/ldes-member-unversioned-context-included.ttl").toModel();

        assertTrue(output.isIsomorphicWith(comparisonModel));
    }

}