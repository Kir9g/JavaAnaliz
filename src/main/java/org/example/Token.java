package org.example;

public class Token {
    private String tableid;
    private String value;

    public Token(int first, int second) {
        this.tableid = String.valueOf(first);
        this.value = String.valueOf(second);
    }

    public String getTableid() {
        return tableid;
    }

    public void setTableid(String tableid) {
        this.tableid = tableid;
    }

    public Token(String tableid, String value) {
        this.tableid = tableid;
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "(" + tableid +
                "," + value + ")";
    }
}
