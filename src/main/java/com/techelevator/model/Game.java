package com.techelevator.model;

import javax.validation.constraints.NotBlank;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Game {
    private int gameId;
    @NotBlank(message = "The field `gameName` should not be blank.")
    private String gameName;
    private String organizerName;
    @NotBlank(message="The field `endDate` should not be blank.")
    private String endDate;
    private List<String> users;

    public Game() {
    }

    public Game(String gameName, String organizerName, String endDate) {
        this.gameName = gameName;
        this.organizerName = organizerName;
        this.endDate = endDate;
    }

    public Game(int gameId, String gameName, String organizerName, String endDate) {
        this.gameId = gameId;
        this.gameName = gameName;
        this.organizerName = organizerName;
        this.endDate = endDate;
    }

    public int getGameId() {
        return gameId;
    }

    public void setGameId(int gameId) {
        this.gameId = gameId;
    }

    public String getGameName() {
        return gameName;
    }

    public void setGameName(String gameName) {
        this.gameName = gameName;
    }

    public String getOrganizerName() {
        return organizerName;
    }

    public void setOrganizerName(String organizerName) {
        this.organizerName = organizerName;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public List<String> getUsers() {
        return users;
    }

    public void setUsers(List<String> users) {
        this.users = users;
    }

    public boolean isGameEnded() {
        LocalDate currentDate = LocalDate.now();
        LocalDate endDate = LocalDate.parse(this.endDate);

        return currentDate.isAfter(endDate) || currentDate.isEqual(endDate);
    }

    public void addUser(String username) {
        if (users == null) {
            users = new ArrayList<>();
        }
        users.add(username);
    }



}





