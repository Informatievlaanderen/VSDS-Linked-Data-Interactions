package be.vlaanderen.informatievlaanderen.ldes.ldio;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFParser;

import java.io.IOException;

import static org.apache.jena.rdf.model.ResourceFactory.createProperty;

class LdioFileOutTest {

    public static void main(String[] args) throws IOException {

        FileNameManager archiver = new FileNameManager(new LocalDateTimeConverter(),
                "/home/tom/IdeaProjects/VSDS-Linked-Data-Interactions/ldi-orchestrator/ldio-connectors/ldio-file-out/archive",
                createProperty("http://www.w3.org/ns/prov#generatedAtTime"));

        String model = """
                <https://private-api.gipod.beta-vlaanderen.be/api/v1/taxonomies/statuses/0a4ee99b-8b8a-47c8-913f-117220febee0> <http://www.w3.org/2004/02/skos/core#prefLabel> "In opmaak"@nl-be .
                _:B9d973b9a5c2911f1ffbf70024806f47f <http://www.opengis.net/ont/geosparql#asWKT> "<http://www.opengis.net/def/crs/EPSG/9.9.1/31370> POLYGON ((170287.59 200940.75, 170287.57 200945.75, 170292.58 200945.77, 170292.6 200940.77, 170287.59 200940.75))"^^<http://www.opengis.net/ont/geosparql#wktLiteral> .
                _:B9d973b9a5c2911f1ffbf70024806f47f <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://www.w3.org/ns/locn#Geometry> .
                <https://private-api.gipod.beta-vlaanderen.be/api/v1/organisations/a7eadbde-86a6-076c-bd2f-7f5596ba3730> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://www.w3.org/ns/org#Organization> .
                <https://private-api.gipod.beta-vlaanderen.be/api/v1/organisations/a7eadbde-86a6-076c-bd2f-7f5596ba3730> <http://www.w3.org/2004/02/skos/core#prefLabel> "Gemeente Berlaar" .
                <https://private-api.gipod.beta-vlaanderen.be/api/v1/taxonomies/mobility-hindrance/consequencetypes/8eda1611-902b-4c9a-8b3c-4c23a49d7c5d> <http://www.w3.org/2004/02/skos/core#prefLabel> "Beperkte doorgang voor voetgangers"@nl-be .
                <https://private-api.gipod.beta-vlaanderen.be/api/v1/ldes/mobility-hindrances?generatedAtTime=2022-05-20T09:58:15.867Z> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <https://w3id.org/tree#Node> .
                <https://private-api.gipod.beta-vlaanderen.be/api/v1/mobility-hindrances/10810464/zones/2af888b0-de0c-48c2-956b-8b36d01c1232> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <https://data.vlaanderen.be/ns/mobiliteit#Zone> .
                <https://private-api.gipod.beta-vlaanderen.be/api/v1/mobility-hindrances/10810464/zones/2af888b0-de0c-48c2-956b-8b36d01c1232> <http://www.w3.org/ns/locn#geometry> _:B9d973b9a5c2911f1ffbf70024806f47f .
                <https://private-api.gipod.beta-vlaanderen.be/api/v1/mobility-hindrances/10810464/zones/2af888b0-de0c-48c2-956b-8b36d01c1232> <https://data.vlaanderen.be/ns/mobiliteit#Zone.type> <https://private-api.gipod.beta-vlaanderen.be/api/v1/taxonomies/zonetypes/0fb72ef7-6ac9-4a70-b295-a30ea215d250> .
                <https://private-api.gipod.beta-vlaanderen.be/api/v1/mobility-hindrances/10810464/zones/2af888b0-de0c-48c2-956b-8b36d01c1232> <https://data.vlaanderen.be/ns/mobiliteit#gevolg> <https://private-api.gipod.beta-vlaanderen.be/api/v1/taxonomies/mobility-hindrance/consequencetypes/8eda1611-902b-4c9a-8b3c-4c23a49d7c5d> .
                _:Ba6b523ef75c48b5351f80cf6470bd2f2 <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://www.w3.org/ns/adms#Identifier> .
                _:Ba6b523ef75c48b5351f80cf6470bd2f2 <http://www.w3.org/2004/02/skos/core#notation> "10810464"^^<https://gipod.vlaanderen.be/ns/gipod#gipodId> .
                _:Ba6b523ef75c48b5351f80cf6470bd2f2 <http://www.w3.org/ns/adms#schemaAgency> "https://gipod.vlaanderen.be"@nl-be .
                <https://private-api.gipod.beta-vlaanderen.be/api/v1/taxonomies/zonetypes/0fb72ef7-6ac9-4a70-b295-a30ea215d250> <http://www.w3.org/2004/02/skos/core#prefLabel> "HinderZone"@nl-be .
                <https://private-api.gipod.beta-vlaanderen.be/api/v1/works/10810463> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <https://data.vlaanderen.be/ns/mobiliteit#Werk> .
                <https://private-api.gipod.beta-vlaanderen.be/api/v1/works/10810463> <https://data.vlaanderen.be/ns/mobiliteit#Inname.heeftGevolg> <https://private-api.gipod.beta-vlaanderen.be/api/v1/mobility-hindrances/10810464/1> .
                <https://private-api.gipod.beta-vlaanderen.be/api/v1/works/10810463> <https://gipod.vlaanderen.be/ns/gipod#gipodId> "10810463"^^<http://www.w3.org/2001/XMLSchema#integer> .
                <https://private-api.gipod.beta-vlaanderen.be/api/v1/mobility-hindrances/10810464/1> <https://data.vlaanderen.be/ns/mobiliteit#beheerder> <https://private-api.gipod.beta-vlaanderen.be/api/v1/organisations/a7eadbde-86a6-076c-bd2f-7f5596ba3730> .
                <https://private-api.gipod.beta-vlaanderen.be/api/v1/mobility-hindrances/10810464/1> <http://purl.org/dc/terms/created> "2022-05-20T09:58:15.8610896Z"^^<http://www.w3.org/2001/XMLSchema#dateTime> .
                <https://private-api.gipod.beta-vlaanderen.be/api/v1/mobility-hindrances/10810464/1> <http://purl.org/dc/terms/modified> "2022-05-20T09:58:15.8646433Z"^^<http://www.w3.org/2001/XMLSchema#dateTime> .
                <https://private-api.gipod.beta-vlaanderen.be/api/v1/mobility-hindrances/10810464/1> <http://purl.org/dc/terms/isVersionOf> <https://private-api.gipod.beta-vlaanderen.be/api/v1/mobility-hindrances/10810464> .
                <https://private-api.gipod.beta-vlaanderen.be/api/v1/mobility-hindrances/10810464/1> <https://data.vlaanderen.be/ns/mobiliteit#zone> <https://private-api.gipod.beta-vlaanderen.be/api/v1/mobility-hindrances/10810464/zones/2af888b0-de0c-48c2-956b-8b36d01c1232> .
                <https://private-api.gipod.beta-vlaanderen.be/api/v1/mobility-hindrances/10810464/1> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <https://data.vlaanderen.be/ns/mobiliteit#Mobiliteitshinder> .
                <https://private-api.gipod.beta-vlaanderen.be/api/v1/mobility-hindrances/10810464/1> <http://www.w3.org/ns/adms#versionNotes> "MobilityHindranceZoneWasAdded"@nl-be .
                <https://private-api.gipod.beta-vlaanderen.be/api/v1/mobility-hindrances/10810464/1> <https://data.vlaanderen.be/ns/mobiliteit#Inname.status> <https://private-api.gipod.beta-vlaanderen.be/api/v1/taxonomies/statuses/0a4ee99b-8b8a-47c8-913f-117220febee0> .
                <https://private-api.gipod.beta-vlaanderen.be/api/v1/mobility-hindrances/10810464/1> <http://purl.org/dc/terms/description> "omschrijving" .
                <https://private-api.gipod.beta-vlaanderen.be/api/v1/mobility-hindrances/10810464/1> <http://purl.org/dc/elements/1.1/creator> <https://private-api.gipod.beta-vlaanderen.be/api/v1/organisations/a7eadbde-86a6-076c-bd2f-7f5596ba3730> .
                <https://private-api.gipod.beta-vlaanderen.be/api/v1/mobility-hindrances/10810464/1> <http://www.w3.org/ns/prov#generatedAtTime> "2022-05-20T09:58:15.867Z"^^<http://www.w3.org/2001/XMLSchema#dateTime> .
                <https://private-api.gipod.beta-vlaanderen.be/api/v1/mobility-hindrances/10810464/1> <http://purl.org/dc/elements/1.1/contributor> <https://private-api.gipod.beta-vlaanderen.be/api/v1/organisations/a7eadbde-86a6-076c-bd2f-7f5596ba3730> .
                <https://private-api.gipod.beta-vlaanderen.be/api/v1/mobility-hindrances/10810464/1> <http://www.w3.org/ns/adms#identifier> _:Ba6b523ef75c48b5351f80cf6470bd2f2 .
                <https://private-api.gipod.beta-vlaanderen.be/api/v1/mobility-hindrances/10810464/1> <https://data.vlaanderen.be/ns/mobiliteit#periode> _:B292e5628e8711e26b8812c62967d6e9c .
                <https://private-api.gipod.beta-vlaanderen.be/api/v1/mobility-hindrances/10810464/1> <https://gipod.vlaanderen.be/ns/gipod#gipodId> "10810464"^^<http://www.w3.org/2001/XMLSchema#integer> .
                _:B292e5628e8711e26b8812c62967d6e9c <http://data.europa.eu/m8g/endTime> "2022-05-27T17:00:00Z"^^<http://www.w3.org/2001/XMLSchema#dateTime> .
                _:B292e5628e8711e26b8812c62967d6e9c <http://data.europa.eu/m8g/startTime> "2022-05-27T07:00:00Z"^^<http://www.w3.org/2001/XMLSchema#dateTime> .
                _:B292e5628e8711e26b8812c62967d6e9c <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://data.europa.eu/m8g/PeriodOfTime> .
                """;

        Model model1 = RDFParser.fromString(model).lang(Lang.NQUADS).build().toModel();

        new LdioFileOut(archiver, directoryManager, timestampExtractor).accept(model1);


//        String str = "Hello v2";
//
//        String pwd = "/home/tom/IdeaProjects/VSDS-Linked-Data-Interactions/ldi-orchestrator/ldio-connectors/ldio-file-out";
//
//        String fileName = pwd + "/foo";
//
////        String fileName = "ldi-orchestrator/ldio-connectors/ldio-file-out/fileName2";
//        Path path = Paths.get(fileName);
////        byte[] strToBytes = str.getBytes();
//
//        System.out.println(Files.isReadable(path));
//        Files.write(path, str.getBytes());
//        System.out.println(Files.isReadable(path));
//
//
//
//
//        String now = LocalDateTime.now().toString();
//        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd-HH-mm-ss-SSSSSSSSS");
//        System.out.println(LocalDateTime.now().format(formatter));
//        System.out.println(LocalDateTime.of(2023,12,12,5,5).format(formatter));

    }

}