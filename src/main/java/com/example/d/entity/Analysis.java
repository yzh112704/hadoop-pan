package com.example.d.entity;

import org.springframework.stereotype.Repository;

@Repository("analysis")
public class Analysis {
    private String rowKey;
    private String count;

    public String getRowKey() { return rowKey; }
    public void setRowKey(String rowKey) { this.rowKey = rowKey; }
    public String getCount() { return count; }
    public void setCount(String count) { this.count = count; }

    @Override
    public String toString() {
        return "Analysis{" +
                "rowKey='" + rowKey + '\'' +
                ", count='" + count + '\'' +
                '}';
    }
}
