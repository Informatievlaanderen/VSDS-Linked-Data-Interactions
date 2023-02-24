package ldes.client.continuousprocessor;

import org.junit.jupiter.api.Test;

class ProcessorTest {

    @Test
    void test(){
        Processor processor = new Processor("https://private-api.gipod.beta-vlaanderen.be/api/v1/ldes/mobility-hindrances?generatedAtTime=2020-12-28T09:36:09.72Z");
processor.getMember();    }

}