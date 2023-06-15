package com.techelevator.dao;

import com.techelevator.model.Game;
import com.techelevator.model.Portfolio;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.security.Principal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Component
public class JdbcGameDao implements GameDao {

    private JdbcTemplate jdbcTemplate;
    private PortfolioDao portfolioDao;

    public JdbcGameDao(JdbcTemplate jdbcTemplate, PortfolioDao portfolioDao) {
        this.jdbcTemplate = jdbcTemplate;
        this.portfolioDao = portfolioDao;
    }


    private final BigDecimal STARTING_BALANCE = new BigDecimal("100000.00");

    private Game mapRowToGame(SqlRowSet rs){
        Game game = new Game();
        game.setGameId(rs.getInt("game_id"));
        game.setGameName(rs.getString("game_name"));
        game.setEndDate(rs.getString("end_date"));
        game.setOrganizerName(rs.getString("organizer_name"));
        return game;
    }

    @Override
    public List<Game> list() {
        List<Game> games = new ArrayList<>();

        String sql = "SELECT * " +
                "FROM game g ";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql);
        while(results.next()){
            Game game = mapRowToGame(results);
            games.add(game);
        }
        return games;
    }

    @Override
    public Game get(int gameId) {
      String sql = "SELECT * " +
              "FROM game g " +
              "WHERE g.game_id = ?";
      SqlRowSet result = jdbcTemplate.queryForRowSet(sql, gameId);
      if(result.next()){
          return mapRowToGame(result);
      }else {
          return null;
      }
    }

    @Override
    public Game create(String gameName, String organizerName, String endDate) {
        String sql = "INSERT INTO game (game_name, organizer_name, end_date) " +
                "VALUES (?, ?, ?) " +
                "RETURNING game_id, game_name, organizer_name, end_date";
        SqlRowSet result = jdbcTemplate.queryForRowSet(sql, gameName, organizerName, endDate);

        if (result.next()) {
            Game game = mapRowToGame(result);
            int gameId = game.getGameId();

            String userGameSql = "INSERT INTO user_game (game_id, user_id) " +
                    "VALUES (?, (SELECT user_id FROM users WHERE username = ?))";
            jdbcTemplate.update(userGameSql, gameId, organizerName);

            String portfolioSql = "INSERT INTO portfolio (game_id, user_id, cash_balance) " +
                    "VALUES (?, (SELECT user_id FROM users WHERE username = ?), ?)";
            jdbcTemplate.update(portfolioSql, gameId, organizerName, STARTING_BALANCE);

            return game;
        } else {
            return null;
        }
    }


    @Override
    public Game update(Game game, int gameId) {
        String sql = "UPDATE game " +
                "SET game_name = ?, organizer_name = ?, end_date = ? " +
                "WHERE game_id = ?";

        int rowsAffected = jdbcTemplate.update(sql, game.getGameName(),
                game.getOrganizerName(), game.getEndDate(), gameId);

        if (rowsAffected > 0) {
            Game updatedGame = get(gameId);
            if (updatedGame != null) {
                updatedGame.setUsers(game.getUsers());
                return updatedGame;
            }
        }
        return null;
    }

    public String getUsername(Principal principal){
        return principal.getName();
    }


    @Override
    public List<Game> searchByUsername(String username) {
        List<Game> games = new ArrayList<>();

        String sql = "SELECT * " +
                "FROM game g " +
                "JOIN user_game ON g.game_id = user_game.game_id " +
                "JOIN users ON users.user_id = user_game.user_id " + // Add a space here
                "WHERE username = ?";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql, username);
        while(results.next()){
            Game game = mapRowToGame(results);
            games.add(game);
        }
        return games;
    }

    @Override
    public int delete(int gameId) {
        int numRowsDeleted = 0;
        String sql0 = "DELETE FROM user_game WHERE game_id = ?";
        String sql1 = "DELETE FROM games WHERE game_id = ?";

        jdbcTemplate.update(sql0, gameId);
        numRowsDeleted = jdbcTemplate.update(sql1, gameId);

        return numRowsDeleted;
    }

    @Override
    public void endGame(int gameId) {
        // Sell all outstanding stock balances for all players in the game
        portfolioDao.sellAllStocks(gameId);

        // Update the game end date to the current date
        String sql = "UPDATE game SET end_date = ? WHERE game_id = ?";
        LocalDate currentDate = LocalDate.now();
        jdbcTemplate.update(sql, currentDate.toString(), gameId);
    }

    @Override
    public boolean isGameEnded(int gameId) {
        String sql = "SELECT end_date FROM game WHERE game_id = ?";
        String endDateStr = jdbcTemplate.queryForObject(sql, String.class, gameId);
        LocalDate endDate = LocalDate.parse(endDateStr);

        LocalDate currentDate = LocalDate.now();
        return currentDate.isAfter(endDate) || currentDate.isEqual(endDate);
    }

    @Override
    public LocalDate getEndDate(int gameId) {
        String sql = "SELECT end_date FROM game WHERE game_id = ?";
        String endDateStr = jdbcTemplate.queryForObject(sql, String.class, gameId);
        return LocalDate.parse(endDateStr);
    }


    @Override
    public Portfolio getWinner(int gameId) {

        // Check the number of players in the game
        int playerCount = portfolioDao.getPlayerCount(gameId);
        if (playerCount == 1) {
            // Only one player, directly retrieve the portfolio by game ID
            return portfolioDao.getPortfolioByGameId(gameId);
        } else {
            // Multiple players, execute the query to find the winner
            String sql = "SELECT p.user_id, p.cash_balance " +
                    "FROM portfolio p " +
                    "WHERE p.game_id = ? " +
                    "ORDER BY p.cash_balance DESC " +
                    "LIMIT 1";

            // Execute the query and retrieve the result
            SqlRowSet result = jdbcTemplate.queryForRowSet(sql, gameId);

            if (result.next()) {
                // Retrieve the winner's user ID and cash balance
                int userId = result.getInt("user_id");
                BigDecimal cashBalance = result.getBigDecimal("cash_balance");

                // Create Portfolio object for the winner
                Portfolio winner = new Portfolio();
                winner.setUserId(userId);
                winner.setGameId(gameId);
                winner.setCashBalance(cashBalance);

                // Update cash balance for the winner
                portfolioDao.updateCashBalance(userId, gameId, cashBalance);

                return winner;
            } else {
                // No winner found
                return null;
            }
        }
    }


    // code below edit ddove
    @Override
    public void addUserToGame(int gameId, String username) {
        String userExistSql = "SELECT COUNT(*) FROM users WHERE username = ?";
        String gameExistSql = "SELECT COUNT(*) FROM game WHERE game_id = ?";
        String userGameExistSql = "SELECT COUNT(*) FROM user_game WHERE game_id = ? AND user_id = (SELECT user_id FROM users WHERE username = ?)";

        // Check if the user exists
        Integer userCount = jdbcTemplate.queryForObject(userExistSql, new Object[]{username}, Integer.class);
        if(userCount == null || userCount <= 0){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }

        // Check if the game exists
        Integer gameCount = jdbcTemplate.queryForObject(gameExistSql, new Object[]{gameId}, Integer.class);
        if(gameCount == null || gameCount <= 0){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Game not found");
        }

        // Check if the user is already added to the game
        Integer userGameCount = jdbcTemplate.queryForObject(userGameExistSql, new Object[]{gameId, username}, Integer.class);
        if(userGameCount != null && userGameCount > 0){
            throw new ResponseStatusException(HttpStatus.CONFLICT, "User already added to the game");
        }

        String sql = "INSERT INTO user_game (game_id, user_id) " +
                "VALUES (?, (SELECT user_id FROM users WHERE username = ?))";
        jdbcTemplate.update(sql, gameId, username);

        String portfolioSql = "INSERT INTO portfolio (game_id, user_id, cash_balance) " +
                "VALUES (?, (SELECT user_id FROM users WHERE username = ?), ?)";
        jdbcTemplate.update(portfolioSql, gameId, username, STARTING_BALANCE);

    }

}
