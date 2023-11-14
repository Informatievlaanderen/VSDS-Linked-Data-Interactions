package be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.executor.edc;

import org.apache.jena.atlas.json.JSON;
import org.apache.jena.atlas.json.JsonObject;

public class EdcTransfer {

    private final JsonObject transfer;

    public EdcTransfer(JsonObject transfer) {
        this.transfer = transfer;
    }

    public static EdcTransfer fromJsonString(String transfer) {
        return new EdcTransfer(JSON.parse(transfer));
    }

}
