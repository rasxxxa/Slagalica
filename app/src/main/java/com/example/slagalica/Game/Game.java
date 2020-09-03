package com.example.slagalica.Game;

import com.example.slagalica.Activities.MainMenu.MainActivity;
import com.example.slagalica.MultiPlayer.Message;
import com.example.slagalica.MultiPlayer.Player;

import java.util.ArrayList;

public class Game {
    protected ArrayList<String> game1Letters;
    protected ArrayList<Integer> game2Numbers;
    protected ArrayList<String> game3Words;
    protected ArrayList<Integer> game4Combination;
    protected ArrayList<Integer> game5QuestionsNumbers;
    protected Integer associationNumber;
    protected Player player1;
    protected Player player2;
    protected ArrayList<Integer> points1;
    protected ArrayList<Integer> points2;
    protected ArrayList<Message> messages;
    protected String gameId;


    public Game()
    {
        game1Letters = new ArrayList<>();
        game2Numbers = new ArrayList<>();
        game3Words = new ArrayList<>();
        game4Combination = new ArrayList<>();
        game5QuestionsNumbers = new ArrayList<>();
        associationNumber = 0;
        player1 = null;
        player2 = null;
        points1 = new ArrayList<>();
        points2 = new ArrayList<>();
        for (int i = 0; i<MainActivity.numberOfGames;i++)
        {
            points1.add(0);
            points2.add(0);
        }
        messages = new ArrayList<>();
        gameId = "";
    }

    // Single player constructor

    public ArrayList<String>  getGame1Letters() {
        return game1Letters;
    }

    public void setGame1Letters(ArrayList<String>  game1Letters) {
        this.game1Letters = game1Letters;
    }

    public ArrayList<Integer> getGame2Numbers() {
        return game2Numbers;
    }

    public void setGame2Numbers(ArrayList<Integer> game2Numbers) {
        this.game2Numbers = game2Numbers;
    }

    public ArrayList<String> getGame3Words() {
        return game3Words;
    }

    public void setGame3Words(ArrayList<String> game3Words) {
        this.game3Words = game3Words;
    }

    public String getGameId() {
        return gameId;
    }

    public void setGameId(String gameId) {
        this.gameId = gameId;
    }

    public ArrayList<Integer> getGame4Combination() {
        return game4Combination;
    }

    public void setGame4Combination(ArrayList<Integer> game4Combination) {
        this.game4Combination = game4Combination;
    }

    public ArrayList<Integer> getGame5QuestionsNumbers() {
        return game5QuestionsNumbers;
    }

    public void setGame5QuestionsNumbers(ArrayList<Integer> game5QuestionsNumbers) {
        this.game5QuestionsNumbers = game5QuestionsNumbers;
    }

    public Integer getAssociationNumber() {
        return associationNumber;
    }

    public void setAssociationNumber(Integer associationNumber) {
        this.associationNumber = associationNumber;
    }

    public Game(ArrayList<String> game1Letters, ArrayList<Integer> game2Numbers, ArrayList<String> game3Words, ArrayList<Integer> game4Combination, ArrayList<Integer> game5QuestionsNumbers, Integer associationNumber, Player player1, Player player2, ArrayList<Integer> points1, ArrayList<Integer> points2, ArrayList<Message> messages, String gameId) {
        this.game1Letters = game1Letters;
        this.game2Numbers = game2Numbers;
        this.game3Words = game3Words;
        this.game4Combination = game4Combination;
        this.game5QuestionsNumbers = game5QuestionsNumbers;
        this.associationNumber = associationNumber;
        this.player1 = player1;
        this.player2 = player2;
        this.points1 = points1;
        this.points2 = points2;
        this.messages = messages;
        this.gameId = gameId;
    }

    public void setPlayer1(Player player1) {
        this.player1 = player1;
    }

    public ArrayList<Message> getMessages() {
        return messages;
    }

    public void setMessages(ArrayList<Message> messages) {
        this.messages = messages;
    }

    public Player getPlayer1() {
        return player1;
    }

    public Player getPlayer2() {
        return player2;
    }

    public void setPlayer2(Player player2) {
        this.player2 = player2;
    }

    public ArrayList<Integer> getPoints1() {
        return points1;
    }

    public void setPoints1(ArrayList<Integer> points1) {
        this.points1 = points1;
    }

    public ArrayList<Integer> getPoints2() {
        return points2;
    }

    public void setPoints2(ArrayList<Integer> points2) {
        this.points2 = points2;
    }
}
