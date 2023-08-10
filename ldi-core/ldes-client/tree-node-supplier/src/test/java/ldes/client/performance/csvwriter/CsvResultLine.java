package ldes.client.performance.csvwriter;

import java.util.List;

public class CsvResultLine {

    private String sqlite10 = "0";
    private String memory10 = "0";
    private String file10 = "0";
    private String postgres10 = "0";

    public static String getCsvHeaders() {
        return "count,sqlite10,memory10,file10,postgres10";
    }

    public String toCsv() {
        return String.join(",", List.of(sqlite10,memory10,file10,postgres10));
    }

    public void setSqlite10(String sqlite10) {
        this.sqlite10 = sqlite10;
    }

    public void setMemory10(String memory10) {
        this.memory10 = memory10;
    }

    public void setFile10(String file10) {
        this.file10 = file10;
    }

    public void setPostgres10(String postgres10) {
        this.postgres10 = postgres10;
    }
}
