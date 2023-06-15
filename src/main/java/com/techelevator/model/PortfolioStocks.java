package com.techelevator.model;

import java.math.BigDecimal;

public class PortfolioStocks {
    private int id;
    private int portfolioId;
    private String ticker;
    private int quantity;
    private BigDecimal value;

    public PortfolioStocks() {
    }

    public PortfolioStocks(int id, int portfolioId, String ticker, int quantity, BigDecimal value) {
        this.id = id;
        this.portfolioId = portfolioId;
        this.ticker = ticker;
        this.quantity = quantity;
        this.value = value;
    }

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public int getPortfolioId() {
        return portfolioId;
    }
    public void setPortfolioId(int portfolioId) {
        this.portfolioId = portfolioId;
    }
    public String getTicker() {
        return ticker;
    }
    public void setTicker(String ticker) {
        this.ticker = ticker;
    }
    public int getQuantity() {
        return quantity;
    }
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
    public BigDecimal getValue() {
        return value;
    }
    public void setValue(BigDecimal value) {
        this.value = value;
    }
}
