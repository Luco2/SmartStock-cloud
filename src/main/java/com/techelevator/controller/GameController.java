package com.techelevator.controller;


import com.techelevator.dao.GameDao;
import com.techelevator.dao.PortfolioDao;
import com.techelevator.model.Game;
import com.techelevator.model.Portfolio;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.security.Principal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin
@RequestMapping("/games")
@PreAuthorize("isAuthenticated()")
public class GameController {

    private GameDao gameDao;
    private PortfolioDao portfolioDao;

    public GameController(GameDao gameDao, PortfolioDao portfolioDao) {
        this.gameDao = gameDao;
        this.portfolioDao = portfolioDao;
    }

    @RequestMapping(path = "", method = RequestMethod.GET)
    public List<Game> getAllGames(){
        return gameDao.list();
    }

    @RequestMapping(path = "/user", method = RequestMethod.GET)
    public List<Game> showMyGames(Principal principal){
        List<Game> myGames = gameDao.searchByUsername(principal.getName());
        if(myGames == null){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        } else {
            return myGames;
        }
    }

    @RequestMapping(path = "/{id}", method = RequestMethod.GET)
    public Game get(@PathVariable int id){
        Game game = gameDao.get(id);
        if (game == null){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Game Not Found");
        } else {
            return game;
        }
    }

    @ResponseStatus(HttpStatus.CREATED)
    @RequestMapping(path = "/new", method = RequestMethod.POST)
    public Game create(@Valid @RequestBody Game game, Principal principal){
        String gameName = game.getGameName();
        String organizerName = principal.getName();
        String endDate = game.getEndDate();
        return gameDao.create(gameName, organizerName, endDate);
    }

    @PostMapping("/{gameId}/users")
    public ResponseEntity<String> addUserToGame(@PathVariable int gameId, @RequestBody Map<String, String> requestPayload) {
        String username = requestPayload.get("username");

        Game game = gameDao.get(gameId);
        if (game == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Game Not Found");
        }
        gameDao.addUserToGame(gameId, username);

        return ResponseEntity.ok("User added to the game successfully.");
    }


    @PostMapping("/{id}/end")
    public ResponseEntity<?> endGame(@PathVariable int id) {
        Game game = gameDao.get(id);
        if (game == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Game Not Found");
        }
        if (gameDao.isGameEnded(id)) {
            return ResponseEntity.badRequest().body("Game has already ended");
        }
        LocalDate currentDate = LocalDate.now();
        LocalDate endDate = gameDao.getEndDate(id);
        if (currentDate.isBefore(endDate)) {
            return ResponseEntity.badRequest().body("Game end date has not been reached");
        }
        // Sell all outstanding stock balances for all players in the game
        portfolioDao.sellAllStocks(id);

        // Update the game status to mark it as ended
        gameDao.endGame(id);

        return ResponseEntity.ok("Game ended successfully");
    }

    @GetMapping("/{gameId}/winner")
    public ResponseEntity<Portfolio> getWinner(@PathVariable int gameId) {
        // Verify the existence of the game
        Game game = gameDao.get(gameId);
        if (game == null) {
            return ResponseEntity.notFound().header("message", "Game not found").build();
        }
        Portfolio winner = gameDao.getWinner(gameId);
        if (winner != null) {
            return ResponseEntity.ok(winner);
        } else {
            return ResponseEntity.notFound().header("message", "No winner found").build();
        }
    }
}
