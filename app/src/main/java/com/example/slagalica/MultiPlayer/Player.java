package com.example.slagalica.MultiPlayer;

import java.util.ArrayList;
import java.util.Random;

public class Player {
    public String username;
    public String gameId;
    public ArrayList<Boolean> gamesPlayed;
    public ArrayList<Integer> gamePoints;
    public Boolean challenged;
    public boolean challengedSomeone;
    public int typeOfPlayer;
    private long timeOnline;

    public long getTimeOnline() {
        return timeOnline;
    }

    public void setTimeOnline(long timeOnline) {
        this.timeOnline = timeOnline;
    }

    public String name;

    public Boolean getChallenged() {
        return challenged;
    }

    public void setChallenged(Boolean challenged) {
        this.challenged = challenged;
    }

    public boolean isChallengedSomeone() {
        return challengedSomeone;
    }

    public void setChallengedSomeone(boolean challengedSomeone) {
        this.challengedSomeone = challengedSomeone;
    }

    public String lastname;
    public String idInGame;
    public Player challenger = null;


    public Player getChallenger() {
        return challenger;
    }

    public void setChallenger(Player challenger) {
        this.challenger = challenger;
    }

    public Player() {
        gamesPlayed = new ArrayList<>();
        gamePoints = new ArrayList<>();
        for (int i = 0;i<6;i++)
        {
            gamesPlayed.add(false);
            gamePoints.add(0);
        }
        challenger = null;
        challenged = null;
        gameId = "";
        this.username = "";
        this.name = "";
        this.lastname = "";
        this.idInGame = "";
        timeOnline = System.currentTimeMillis() / 1000; // to seconds
        challengedSomeone = false;
    }

    public Player(String username, String gameId, ArrayList<Boolean> gamesPlayed, ArrayList<Integer> gamePoints, boolean chalenged, int typeOfPlayer, String name, String lastname, String idInGame) {
        this.username = username;
        this.gameId = gameId;
        this.gamesPlayed = gamesPlayed;
        this.gamePoints = gamePoints;
        this.challenged = chalenged;
        this.typeOfPlayer = typeOfPlayer;
        this.name = name;
        this.lastname = lastname;
        this.idInGame = idInGame;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getGameId() {
        return gameId;
    }

    public void setGameId(String gameId) {
        this.gameId = gameId;
    }

    public ArrayList<Boolean> getGamesPlayed() {
        return gamesPlayed;
    }

    public void setGamesPlayed(ArrayList<Boolean> gamesPlayed) {
        this.gamesPlayed = gamesPlayed;
    }

    public ArrayList<Integer> getGamePoints() {
        return gamePoints;
    }

    public void setGamePoints(ArrayList<Integer> gamePoints) {
        this.gamePoints = gamePoints;
    }

    public Boolean isChalenged() {
        return challenged;
    }

    public void setChalenged(Boolean chalenged) {
        this.challenged = chalenged;
    }

    public int getTypeOfPlayer() {
        return typeOfPlayer;
    }

    public void setTypeOfPlayer(int typeOfPlayer) {
        this.typeOfPlayer = typeOfPlayer;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getIdInGame() {
        return idInGame;
    }

    public void setIdInGame(String idInGame) {
        this.idInGame = idInGame;
    }
}
