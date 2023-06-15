package com.techelevator.model;

import com.techelevator.api.model.StockModel;

import java.math.BigDecimal;
import java.util.List;

public class Portfolio {
    private int portfolioId;
    private int gameId;
    private int userId;
    private List<StockModel> stocks;
    private BigDecimal cashBalance;

    public Portfolio(){

    }
    public Portfolio(int gameId, int userId, List<StockModel> stocks, BigDecimal cashBalance){
        this.gameId = gameId;
        this.userId = userId;
        this.stocks = stocks;
        this.cashBalance = cashBalance;
    }
    public Portfolio(int portfolioId,int gameId, int userId, List<StockModel> stocks, BigDecimal cashBalance){
        this.portfolioId = portfolioId;
        this.gameId = gameId;
        this.userId = userId;
        this.stocks = stocks;
        this.cashBalance = cashBalance;
    }

    public int getPortfolioId() {
        return portfolioId;
    }

    public void setPortfolioId(int portfolioId) {
        this.portfolioId = portfolioId;
    }

    public int getGameId() {
        return gameId;
    }

    public void setGameId(int gameId) {
        this.gameId = gameId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public List<StockModel> getStocks() {
        return stocks;
    }

    public void setStocks(List<StockModel> stocks) {
        this.stocks = stocks;
    }

    public BigDecimal getCashBalance() {
        return cashBalance;
    }

    public void setCashBalance(BigDecimal cashBalance) {
        this.cashBalance = cashBalance;
    }

}
