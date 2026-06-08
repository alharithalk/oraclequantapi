package com.oraclequantapi.oraclequantapi.model;

import java.time.LocalDateTime;

public class HistoryRecord {

    private Long id;
    private String symbol;
    private double value;
    private LocalDateTime timestamp;

    public HistoryRecord() {
    }

    public HistoryRecord(Long id, String symbol, double value, LocalDateTime timestamp) {
        this.id = id;
        this.symbol = symbol;
        this.value = value;
        this.timestamp = timestamp;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}
