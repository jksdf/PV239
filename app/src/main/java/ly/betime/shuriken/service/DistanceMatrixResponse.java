package ly.betime.shuriken.service;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class DistanceMatrixResponse {
    @Expose
    @SerializedName("rows")
    private List<Row> rows;

    @Expose
    @SerializedName("status")
    private String status;

    public List<Row> getRows() {
        return rows;
    }

    public void setRows(List<Row> rows) {
        this.rows = rows;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public static class Row {
        @Expose
        @SerializedName("elements")
        private List<Element> elements;

        public List<Element> getElements() {
            return elements;
        }

        public void setElements(List<Element> elements) {
            this.elements = elements;
        }
    }

    public static class Element {

        @Expose
        @SerializedName("duration")
        private KeyValue duration;

        @Expose
        @SerializedName("status")
        private String status;

        public KeyValue getDuration() {
            return duration;
        }

        public void setDuration(KeyValue duration) {
            this.duration = duration;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }
    }

    public static class KeyValue {
        @Expose
        @SerializedName("text")
        private String text;

        @Expose
        @SerializedName("value")
        private int value;

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }

        public int getValue() {
            return value;
        }

        public void setValue(int value) {
            this.value = value;
        }
    }
}
