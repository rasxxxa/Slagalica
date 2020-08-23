package com.example.slagalica.HelperClasses;

public enum ErrorInfo {

    FirebaseAppInitialise("Error creating firebase instance"),
    AddPlayerToOnlineList("Adding player to online list error:"),
    MultiPlayerActivity("Multiplayer activity error"),
    SinglePlayerActivityChildAdded("Child added in singleplayer activity error"),
    SinglePlayerActivityDataChanged("Data changed in singleplayer activity error"),
    MultiPlayerDataChanged("Data changed in multiplayer activity error"),
    MultiPlayerDataChangedChallenge("Data changed in multiplayer challenger error");
    //MultiPlayer("awd");


    private final String value;

    ErrorInfo(String value)
    {
        this.value = value;
    }

    public String getValue()
    {
        return value;
    }
}
