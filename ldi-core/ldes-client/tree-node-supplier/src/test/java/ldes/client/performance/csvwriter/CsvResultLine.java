package ldes.client.performance.csvwriter;

import java.util.List;

public class CsvResultLine {

    private String sqlite10, sqlite100;

    public void setSqlite10(int sqlite10) {
        this.sqlite10 = String.valueOf(sqlite10);
    }

    public void setSqlite100(int sqlite100) {
        this.sqlite100 = String.valueOf(sqlite100);
    }

    public static String getCsvHeaders() {
        return "sqlite10,sqlite100";
    }

    public String toCsv() {
        return String.join(",", List.of(sqlite10, sqlite100));
    }

}
