package com.techelevator.dao;

import com.techelevator.model.Game;
import com.techelevator.model.Portfolio;
import com.techelevator.model.User;

import java.security.Principal;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;

public interface GameDao {
    List<Game> list();
    Game get(int gameId);

    Game create(String gameName, String organizerName, String endDate);

    Game update(Game game, int gameId);

    List<Game> searchByUsername(String username);
    String getUsername (Principal principal);

    int delete(int id);

    void endGame(int gameId);

    boolean isGameEnded(int gameId);

    LocalDate getEndDate(int gameId);

    Portfolio getWinner(int gameId);

    void addUserToGame(int gameId, String username);

}
