package com.techelevator.api.model;

import java.math.BigDecimal;

public class ResultsModel {

    private BigDecimal close;
    private long volume;
    private String status;
    private String symbol;

    public ResultsModel() {

    }

    public ResultsModel(BigDecimal close, long volume, String status, String symbol) {
        this.close = close;
        this.volume = volume;
        this.status = status;
        this.symbol = symbol;
    }

    public BigDecimal getClose() {
        return close;
    }

    public void setClose(BigDecimal close) {
        this.close = close;
    }

    public long getVolume() {
        return volume;
    }

    public void setVolume(long volume) {
        this.volume = volume;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public void setTicker(String asText) {
    }

    public void setClosePrice(double c) {
    }

    public void setTransactions(int n) {
    }
}
