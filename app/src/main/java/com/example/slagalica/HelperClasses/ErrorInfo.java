package com.example.slagalica.HelperClasses;

public enum ErrorInfo {

    FirebaseAppInitialise("Error creating firebase instance"),
    AddPlayerToOnlineList("Adding player to online list error:"),
    MultiPlayerActivity("Multiplayer activity error"),
    SinglePlayerActivityChildAdded("Child added in singleplayer activity error"),
    SinglePlayerActivityDataChanged("Data changed in singleplayer activity error"),
    MultiPlayerDataChanged("Data changed in multiplayer activity error"),
    MultiPlayerDataChangedChallenge("Data changed in multiplayer challenger error"),
    RegisterUserMultiplayer("Registering new user in database error"),
    AddingChatListenerError("Adding new chat listener error"),
    SendingMessage("Sending new message error"),
    UpdatePoints("Updating points error"),
    AddingUpdatePointsListener("Listener for updating points error"),
    CheckingUsersInDatabase("Listener for existing user in database error"),
    WaitingForPlayer("Listener for waiting player error"),
    RefuseChallengeFromPlayer("Refuse challenge error"),
    RemovingPlayerFromOnlineList("Remove players from database online list error"),
    RejectingChallengeFromPlayer("When player reject challenge error"),
    AcceptingChallengeError("Error when player accepted challenge"),
    ErrorChallengePlayer("Challenge another player error"),
    ChallengePlayerDialog("Challenge player creation dialog error"),
    PlayerListUpdate("Update player list error"),
    CreatingGameListener("Creating listener for game error");
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
