package com.example.slagalica.HelperClasses;

public enum TypeOfGame {

    SinglePlayer(0), MultiPlayer(1);
    private final int value;

    TypeOfGame(int value)
    {
        this.value = value;
    }

    public int getValue()
    {
        return value;
    }
}
