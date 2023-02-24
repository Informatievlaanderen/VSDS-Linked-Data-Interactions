package ldes.client.continuousprocessor;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MemberSupplierTest {

    @Test
    void test(){
        MemberSupplier memberSupplier = new MemberSupplier("https://private-api.gipod.beta-vlaanderen.be/api/v1/ldes/mobility-hindrances?generatedAtTime=2020-12-28T09:36:09.72Z");
        while(true){
            memberSupplier.get();
        }

    }

}