package com.techelevator.dao;

import com.techelevator.api.model.StockModel;
import com.techelevator.model.Portfolio;
import com.techelevator.model.PortfolioStocks;

import javax.sound.sampled.Port;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public interface PortfolioDao {

    List<Portfolio> getPortfoliosByGameId(int gameId);

    Portfolio getPortfolioByGameId(int gameId);

    Portfolio getPortfolioByUser(String username, int gameId);

    PortfolioStocks getPortfolioStocksById(int id);

    List<PortfolioStocks> getPortfolioStocks(String username, int gameId);

    void buy(PortfolioStocks stock, BigDecimal cost, String username, int gameId);

    void sell(BigDecimal cost, String username, int gameId, int stockId);

    void sellAllStocks(int gameId);

    BigDecimal getCashBalanceByGameAndUser(int gameId, int userId);

    void updateCashBalance(int userId, int gameId, BigDecimal cashBalance);

    int getPlayerCount(int gameId);
}
