package com.techelevator.dao;

import com.techelevator.model.Portfolio;
import com.techelevator.model.PortfolioStocks;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Component
public class JdbcPortfolioDao implements PortfolioDao {

    private JdbcTemplate jdbcTemplate;
    public JdbcPortfolioDao(DataSource dataSource){
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Override
    public List<Portfolio> getPortfoliosByGameId(int gameId) {
        List<Portfolio> portfolios = new ArrayList<>();
        String sql = "SELECT * FROM portfolio WHERE game_id = ? ORDER BY user_id";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql, gameId);
        while(results.next()){
            Portfolio portfolio = mapRowToPortfolio(results);
            portfolios.add(portfolio);
        }
        return portfolios;
    }

    @Override
    public Portfolio getPortfolioByUser(String username, int gameId) {
        Portfolio portfolio = null;
        String sql = "SELECT * FROM portfolio AS p\n" +
                "JOIN users AS u ON p.user_id = u.user_id\n" +
                "WHERE u.username = ? AND p.game_id = ?; ";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql, username, gameId);
        if(results.next()) {
            portfolio = mapRowToPortfolio(results);
        }
        return portfolio;
    }

    @Override
    public PortfolioStocks getPortfolioStocksById(int id) {
        PortfolioStocks pS = null;
        String sql = "SELECT * FROM portfolio_stocks WHERE portfolio_stock_id = ?";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql, id);
        if(results.next()) {
            pS = mapRowToPortfolioStocks(results);
        }
        return pS;
    }

    @Override
    public List<PortfolioStocks> getPortfolioStocks(String username, int gameId) {
        List<PortfolioStocks> stockList = new ArrayList<>();
        String sql = "SELECT * FROM portfolio_stocks AS ps\n" +
                "JOIN portfolio AS p ON ps.portfolio_id = p.portfolio_id\n" +
                "JOIN users AS u ON p.user_id = u.user_id\n" +
                "WHERE u.username = ? AND p.game_id = ?";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql, username, gameId);
        while (results.next()) {
            PortfolioStocks stock = mapRowToPortfolioStocks(results);
            stockList.add(stock);
        }
        return stockList;
    }

    @Override
    public void buy(PortfolioStocks stock, BigDecimal cost, String username, int gameId) {
        String portfolioStockSql = "INSERT INTO portfolio_stocks (portfolio_id, ticker, quantity) \n" +
                "VALUES ((SELECT portfolio_id FROM portfolio AS p\n" +
                "JOIN users AS u ON p.user_id = u.user_id\n" +
                "WHERE u.username = ? AND p.game_id = ?), ?, ?);";
        jdbcTemplate.update(portfolioStockSql, username, gameId, stock.getTicker(), stock.getQuantity());

        BigDecimal currentBalance = getPortfolioByUser(username, gameId).getCashBalance();
        cost = cost.multiply(new BigDecimal(stock.getQuantity()));
        BigDecimal newBalance = currentBalance.subtract(cost);
        String portfolioSql = "UPDATE portfolio SET cash_balance = ?\n" +
                "WHERE game_id = ? AND user_id = (SELECT user_id FROM users WHERE username = ?)";
        jdbcTemplate.update(portfolioSql, newBalance, gameId, username);
    }

    @Override
    public void sell(BigDecimal cost, String username, int gameId, int stockId) {
        int quantity = getPortfolioStocksById(stockId).getQuantity();

        String portfolioStockSql = "DELETE FROM portfolio_stocks WHERE portfolio_stock_id = ?";
        jdbcTemplate.update(portfolioStockSql, stockId);

        BigDecimal currentBalance = getPortfolioByUser(username, gameId).getCashBalance();
        cost = cost.multiply(new BigDecimal(quantity));
        BigDecimal newBalance = currentBalance.add(cost);
        String portfolioSql = "UPDATE portfolio SET cash_balance = ?\n" +
                "WHERE game_id = ? AND user_id = (SELECT user_id FROM users WHERE username = ?)";
        jdbcTemplate.update(portfolioSql, newBalance, gameId, username);
    }

    @Override
    public void sellAllStocks(int gameId) {
        String sql = "DELETE FROM portfolio_stocks WHERE portfolio_id IN " +
                "(SELECT portfolio_id FROM portfolio WHERE game_id = ?)";
        jdbcTemplate.update(sql, gameId);
    }

    @Override
    public BigDecimal getCashBalanceByGameAndUser(int gameId, int userId) {
        String sql = "SELECT cash_balance FROM portfolio WHERE game_id = ? AND user_id = ?";
        return jdbcTemplate.queryForObject(sql, BigDecimal.class, gameId, userId);
    }

    @Override
    public void updateCashBalance(int userId, int gameId, BigDecimal cashBalance) {
        String sql = "UPDATE portfolio SET cash_balance = ? " +
                "WHERE user_id = ? AND game_id = ?";
        jdbcTemplate.update(sql, cashBalance, userId, gameId);
    }

    @Override
    public Portfolio getPortfolioByGameId(int gameId) {
        String sql = "SELECT * FROM portfolio WHERE game_id = ?";
        SqlRowSet result = jdbcTemplate.queryForRowSet(sql, gameId);
        if (result.next()) {
            return mapRowToPortfolio(result);
        } else {
            return null;
        }
    }

    @Override
    public int getPlayerCount(int gameId) {
        String sql = "SELECT COUNT(DISTINCT user_id) FROM portfolio WHERE game_id = ?";
        return jdbcTemplate.queryForObject(sql, Integer.class, gameId);
    }

    private Portfolio mapRowToPortfolio(SqlRowSet rs){
        Portfolio portfolio = new Portfolio();
        portfolio.setPortfolioId(rs.getInt("portfolio_id"));
        portfolio.setGameId(rs.getInt("game_id"));
        portfolio.setUserId(rs.getInt("user_id"));
        portfolio.setCashBalance(rs.getBigDecimal("cash_balance"));
        return portfolio;
    }

    PortfolioStocks mapRowToPortfolioStocks(SqlRowSet rowSet) {
        PortfolioStocks stock = new PortfolioStocks();
        stock.setId(rowSet.getInt("portfolio_stock_id"));
        stock.setPortfolioId(rowSet.getInt("portfolio_id"));
        stock.setTicker(rowSet.getString("ticker"));
        stock.setQuantity(rowSet.getInt("quantity"));
        return stock;
    }
}
