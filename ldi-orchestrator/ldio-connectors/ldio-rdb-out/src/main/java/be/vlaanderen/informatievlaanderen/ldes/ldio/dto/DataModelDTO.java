package be.vlaanderen.informatievlaanderen.ldes.ldio.dto;

public class DataModelDTO {
    private ColumnsDTO columns;
    private ValuesDTO data;

    public DataModelDTO(ColumnsDTO columns) {
        this.columns = columns;
    }

    public ColumnsDTO getColumns() {
        return columns;
    }

    public void setColumns(ColumnsDTO columns) {
        this.columns = columns;
    }

    public ValuesDTO getData() {
        return data;
    }

    public void setData(ValuesDTO data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "DataModelDTO{" +
                "columns=" + columns +
                ", data=" + data +
                '}';
    }
}
