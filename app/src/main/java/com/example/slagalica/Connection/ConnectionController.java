package com.example.slagalica.Connection;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.slagalica.Game.Game;
import com.example.slagalica.HelperClasses.DialogBuilder;
import com.example.slagalica.HelperClasses.ErrorInfo;
import com.example.slagalica.HelperClasses.ResourceHelper;
import com.example.slagalica.HelperClasses.TypeOfGame;
import com.example.slagalica.Activities.MainMenu.MainActivity;
import com.example.slagalica.Activities.MainMenu.MultiPlayerActivity;
import com.example.slagalica.Activities.MainMenu.Settings;
import com.example.slagalica.Activities.MainMenu.SinglePlayerActivity;
import com.example.slagalica.MultiPlayer.Message;
import com.example.slagalica.MultiPlayer.MessageListAdapter;
import com.example.slagalica.MultiPlayer.Player;
import com.example.slagalica.MultiPlayer.PlayerListAdapter;
import com.example.slagalica.MultiPlayer.ShortPlayerInfo;
import com.example.slagalica.R;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ConnectionController {

    private static ConnectionController instance;
    private PlayerListAdapter adapter;

    private ValueEventListener listenerForPlayersOnline;
    private ValueEventListener listenerForChallenge;

    public ValueEventListener getListenerForChallenge() {
        return listenerForChallenge;
    }

    public void setListenerForChallenge(ValueEventListener listenerForChallenge) {
        this.listenerForChallenge = listenerForChallenge;
    }

    public ValueEventListener getListenerForGame() {
        return listenerForGame;
    }

    public void setListenerForGame(ValueEventListener listenerForGame) {
        this.listenerForGame = listenerForGame;
    }

    private ValueEventListener listenerForGame;
    private ValueEventListener listenerChallengeResponse;

    private DatabaseReference referenceGame;
    private DatabaseReference referenceChallenger;
    private DatabaseReference referenceChallenge;
    private DatabaseReference referencePlayers;

    private ConnectionController() {

    }

    public static ConnectionController getInstance() {
        if (instance == null) {
            instance = new ConnectionController();
        }
        return instance;
    }

    public PlayerListAdapter getAdapter() {
        return adapter;
    }

    public void setAdapter(PlayerListAdapter adapter) {
        this.adapter = adapter;
    }

    public boolean addOnlinePlayer(Context context, String username, Player player) {

        final FirebaseApp firebaseApp = FirebaseApp.initializeApp(context);
        if (firebaseApp == null)
        {
            killMultiplayerGame(context, ErrorInfo.AddPlayerToOnlineList, true);
            return false;
        }
        final FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance(firebaseApp);
        firebaseDatabase.getReference().child(context.getResources().getString(R.string.playersOnlineNode)).child(username).setValue(player);
        return true;


    }

    public void getOnlinePlayersListener(final ArrayList<Player> players, final Context context, final ListView mainListView) {
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                try {
                    players.clear();
                    for (DataSnapshot dsnapshot : snapshot.getChildren()) {
                        final Player player = dsnapshot.getValue(Player.class);
                        if (!player.getUsername().equals(MainActivity.player.getUsername()) && player.getChallenger() == null && player.challenged == null && player.challengedSomeone == false && checkUserOnlineStatus(context, player.getTimeOnline())) {
                            players.add(dsnapshot.getValue(Player.class));
                        }

                    }
                    adapter = new PlayerListAdapter(context, R.layout.adapterviewlayout, players);
                    mainListView.setAdapter(adapter);
                } catch (Exception e) {
                    killMultiplayerGame(context, ErrorInfo.PlayerListUpdate, true);
                    return;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };

        try {
            final FirebaseApp firebaseApp = FirebaseApp.initializeApp(context);
            if (firebaseApp == null) {
                killMultiplayerGame(context, ErrorInfo.FirebaseAppInitialise, true);
                return;
            }
            final FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance(firebaseApp);
            final DatabaseReference referencePlayers = firebaseDatabase.getReference().child(context.getResources().getString(R.string.playersOnlineNode));
            this.referencePlayers = referencePlayers;
            referencePlayers.addValueEventListener(valueEventListener);
        } catch (Exception e) {
            killMultiplayerGame(context, ErrorInfo.FirebaseAppInitialise, true);
            return;
        }
        listenerForPlayersOnline = valueEventListener;
    }


    private boolean checkUserOnlineStatus(Context context, long playerOnlineTime) {
        // If user was idle in multiplayer more than x minutes, other online players wont see him
        long currentTimeSeconds = System.currentTimeMillis() / 1000;
        return (currentTimeSeconds - playerOnlineTime < context.getResources().getInteger(R.integer.idleTimeInMultiplayer));

    }

    public void getChallengePlayer(final Context context) {
        ValueEventListener listenerForChallenge = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                try {
                    final Player challenger = snapshot.getValue(Player.class);
                    if (challenger != null) {
                        //MultiPlayerActivity.this.playerChallengerUsername = challenger.getUsername();
                        // setting dialog to multiplayer
                        ((MultiPlayerActivity) context).setDialog(DialogBuilder.challengeFromPlayer(challenger, context).create());
                        ((MultiPlayerActivity) context).getDialog().show();
                    }
                    if (challenger == null) {
                        if (((MultiPlayerActivity) context).getDialog() != null) {
                            ((MultiPlayerActivity) context).getDialog().dismiss();
                        }
                    }

                } catch (Exception e) {
                    killMultiplayerGame(context, ErrorInfo.ChallengePlayerDialog, true);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        final FirebaseApp firebaseApp = FirebaseApp.initializeApp(context);
        if (firebaseApp == null) {
            killMultiplayerGame(context, ErrorInfo.ChallengePlayerDialog, true);
            return;
        }
        final FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance(firebaseApp);
        final DatabaseReference referenceChallenged = firebaseDatabase.getReference().child(context.getResources().getString(R.string.playersOnlineNode)).child(MainActivity.player.getUsername()).child(context.getResources().getString(R.string.challengerFirebase));
        referenceChallenge = referenceChallenged;
        referenceChallenged.addValueEventListener(listenerForChallenge);
        this.listenerForChallenge = listenerForChallenge;
    }

    public ValueEventListener createGameListener(final Context context, final Player player) {
        final FirebaseApp firebaseApp = FirebaseApp.initializeApp(context);
        if (firebaseApp == null) {
            killMultiplayerGame(context, ErrorInfo.CreatingGameListener, true);
            return null;
        }
        final FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance(firebaseApp);
        ValueEventListener listenerForGame = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (SinglePlayerActivity.game != null) {
                    return;
                }
                Game game = snapshot.getValue(Game.class);
                if (game != null && SinglePlayerActivity.game == null) {
                    firebaseDatabase.getReference().child(context.getResources().getString(R.string.gameFirebase)).child(game.getGameId()).child(context.getResources().getString(R.string.player2Firebase)).setValue(MainActivity.player);
                    firebaseDatabase.getReference().child(context.getResources().getString(R.string.playersOnlineNode)).child(MainActivity.player.getUsername()).removeValue();
                    SinglePlayerActivity.game = game;
                    Intent intent = new Intent(context, SinglePlayerActivity.class);
                    SinglePlayerActivity.typeOfGame = TypeOfGame.MultiPlayer;
                    intent.putExtra(context.getResources().getString(R.string.typeofplayer), 2);
                    context.startActivity(intent);
                    ((MultiPlayerActivity) context).finish();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }


        };
        final DatabaseReference gameReference = firebaseDatabase.getReference().child(context.getResources().getString(R.string.gameFirebase)).child(player.getGameId());
        this.referenceGame = gameReference;
        gameReference.addValueEventListener(listenerForGame);
        return listenerForGame;
    }


    public void acceptChallengeFrom(Context context) {
        final FirebaseApp firebaseApp = FirebaseApp.initializeApp(context);
        if (firebaseApp == null) {
            killMultiplayerGame(context, ErrorInfo.AcceptingChallengeError, true);
            return;
        }
        final FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance(firebaseApp);
        firebaseDatabase.getReference().child(context.getResources().getString(R.string.playersOnlineNode))
                .child(MainActivity.player.getUsername())
                .child(context.getResources().getString(R.string.challengedFirebase))
                .setValue(true);
    }

    public void rejectChallengeFrom(Context context) {
        final FirebaseApp firebaseApp = FirebaseApp.initializeApp(context);
        if (firebaseApp == null) {
            killMultiplayerGame(context, ErrorInfo.RejectingChallengeFromPlayer, true);
            return;
        }
        final FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance(firebaseApp);
        DatabaseReference ref = firebaseDatabase.getReference().child(context.getResources().getString(R.string.playersOnlineNode))
                .child(MainActivity.player.getUsername());

        ref.child(context.getResources().getString(R.string.challengerFirebase))
                .removeValue();
        ref.child(context.getResources().getString(R.string.challengedFirebase)).setValue(false);
    }

    public void removePlayerForOnline(Context context, Player player) {
        final FirebaseApp firebaseApp = FirebaseApp.initializeApp(context);
        if (firebaseApp == null) {
            killMultiplayerGame(context, ErrorInfo.RemovingPlayerFromOnlineList, true);
            return;
        }
        final FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance(firebaseApp);
        firebaseDatabase.getReference().child(context.getResources().getString(R.string.playersOnlineNode)).child(player.getUsername()).removeValue();
    }

    public void refuseChallenge(final Context context, final Player player, String username) {
        final FirebaseApp firebaseApp = FirebaseApp.initializeApp(context);
        if (firebaseApp == null) {
            killMultiplayerGame(context, ErrorInfo.RefuseChallengeFromPlayer, true);
            return;
        }
        final FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance(firebaseApp);
        DatabaseReference ref = firebaseDatabase
                .getReference()
                .child(context.getResources().getString(R.string.playersOnlineNode));

        ref.child(username)
                .child(context.getResources().getString(R.string.challengerFirebase))
                .removeValue();

        ref.child(player.getUsername())
                .child(context.getResources().getString(R.string.challengedSomeone)).setValue(false);
        player.setChallengedSomeone(false);
    }

    public void challengeSomeone(Context context, String username, Player playerChallenger) {
        final FirebaseApp firebaseApp = FirebaseApp.initializeApp(context);
        if (firebaseApp == null) {
            killMultiplayerGame(context, ErrorInfo.ErrorChallengePlayer, true);
            return;
        }
        final FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance(firebaseApp);
        assert firebaseApp != null;
        DatabaseReference ref = firebaseDatabase
                .getReference()
                .child(context.getResources().getString(R.string.playersOnlineNode));

        ref.child(username)
                .child(context.getResources().getString(R.string.challengerFirebase))
                .setValue(playerChallenger);
        ref.child(playerChallenger.getUsername())
                .child(context.getResources().getString(R.string.challengedSomeone)).setValue(true);
        playerChallenger.setChallengedSomeone(true);
    }

    public ValueEventListener getListenerChallengeResponse() {
        return listenerChallengeResponse;
    }

    public void setListenerChallengeResponse(ValueEventListener listenerChallengeResponse) {
        this.listenerChallengeResponse = listenerChallengeResponse;
    }

    public ValueEventListener waitingForAnswerListener(final Context context, final String username) {
        final FirebaseApp firebaseApp = FirebaseApp.initializeApp(context);
        if (firebaseApp == null) {
            killMultiplayerGame(context, ErrorInfo.WaitingForPlayer, true);
            return null;
        }
        final FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance(firebaseApp);
        ValueEventListener listenerForAnswer = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Boolean value = snapshot.getValue(Boolean.class);
                if (value != null && SinglePlayerActivity.game == null && value) // Challenger accepted challenge
                {
                    Game game = ResourceHelper.getInstance(null).createSinglePlayerGame();
                    game.setGameId(MainActivity.player.getGameId());
                    game.setPlayer1(MainActivity.player);
                    firebaseDatabase.getReference().child(context.getResources().getString(R.string.gameFirebase)).child(MainActivity.player.getGameId()).setValue(game);
                    firebaseDatabase.getReference().child(context.getResources().getString(R.string.playersOnlineNode)).child(MainActivity.player.getUsername()).removeValue();
                    Intent intent = new Intent(context, SinglePlayerActivity.class);
                    intent.putExtra(context.getResources().getString(R.string.typeofplayer), 1);
                    SinglePlayerActivity.game = game;
                    SinglePlayerActivity.typeOfGame = TypeOfGame.MultiPlayer;
                    context.startActivity(intent);
                } else if (value != null && !value && SinglePlayerActivity.game == null) // Challenger refuse challenge
                {
                    firebaseDatabase.getReference().child(context.getResources().getString(R.string.playersOnlineNode))
                            .child(MainActivity.player.getUsername())
                            .child(context.getResources().getString(R.string.challengerFirebase))
                            .removeValue();
                    MainActivity.player.setIdInGame("");
                    DatabaseReference ref = firebaseDatabase.getReference().child(context.getResources().getString(R.string.playersOnlineNode))
                            .child(username);

                    ref.child(context.getResources().getString(R.string.challengerFirebase))
                            .removeValue();
                    ref.child(context.getResources().getString(R.string.challengedFirebase)).removeValue();
                    ((MultiPlayerActivity) context).getDialogAwait().dismiss();
                    firebaseDatabase.getReference().child(context.getResources().getString(R.string.playersOnlineNode)).child(MainActivity.player.getUsername()).child("challengedSomeone").setValue(false);
                    DialogBuilder.dialogPlayerRefused(context).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };

        final DatabaseReference referenceChallenged = firebaseDatabase.getReference().child(context.getResources().getString(R.string.playersOnlineNode))
                .child(username)
                .child(context.getResources().getString(R.string.challengedFirebase));
        this.referenceChallenger = referenceChallenged;
        referenceChallenged.addValueEventListener(listenerForAnswer);
        return listenerForAnswer;
    }

    public ValueEventListener checkForUserInListOfPlayersListener(final Context context, final EditText editTextUserName) {
        ValueEventListener eventListenerForUser = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String value = editTextUserName.getText().toString();
                if (snapshot.hasChild(value) || value.length() < 3) {
                    ((Settings) context).setExists(true);
                    editTextUserName.setBackgroundColor(Color.parseColor(context.getResources().getString(R.string.color13)));
                    editTextUserName.setError(context.getResources().getString(R.string.usernameError));
                } else {
                    ((Settings) context).setExists(false);
                    editTextUserName.setBackgroundColor(Color.parseColor(context.getResources().getString(R.string.color14)));
                    editTextUserName.setError(null);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };

        final FirebaseApp firebaseApp = FirebaseApp.initializeApp(context);
        // TODO futrher implementation
        if (firebaseApp == null) {
            killMultiplayerGame(context, ErrorInfo.CheckingUsersInDatabase, null);
            return null;
        }
        final FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance(firebaseApp);
        firebaseDatabase.getReference().child(context.getResources().getString(R.string.playerNode)).addListenerForSingleValueEvent(eventListenerForUser);
        return eventListenerForUser;
    }

    public ChildEventListener pointsUpdatedListener(final Context context, final Integer opponent, final Game game) {
        final FirebaseApp firebaseApp = FirebaseApp.initializeApp(context);
        // TODO further implementation needed
        if (firebaseApp == null) {
            killMultiplayerGame(context, ErrorInfo.AddingUpdatePointsListener, false);
        }
        final FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance(firebaseApp);
        ChildEventListener listenerForPoints = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                final FirebaseApp firebaseApp1 = FirebaseApp.initializeApp(context);
                final FirebaseDatabase firebaseDatabase1 = FirebaseDatabase.getInstance(firebaseApp1);
                firebaseDatabase1.getReference().child(context.getResources().getString(R.string.gameFirebase)).child(game.getGameId()).child(context.getResources().getString(R.string.pointsFirebase) + opponent)
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                try {
                                    ArrayList<Integer> pointsOpponentArray = new ArrayList<>();
                                    for (DataSnapshot snapshots : snapshot.getChildren()) {
                                        pointsOpponentArray.add(snapshots.getValue(Integer.class));
                                    }
                                    if (pointsOpponentArray.size() == 0) {
                                        return;
                                    }

                                    int pointsOpponent[] = ((SinglePlayerActivity) context).getPointsOpponent();

                                    for (int i = 0; i < pointsOpponentArray.size(); i++) {
                                        pointsOpponent[i] = pointsOpponentArray.get(i);
                                    }
                                    ((SinglePlayerActivity) context).setPointsOpponent(pointsOpponent);
                                    ((SinglePlayerActivity) context).updatePoints();
                                } catch (Exception e) {
                                    Log.e(ErrorInfo.SinglePlayerActivityDataChanged.getValue(), e.toString());
                                    int pid = android.os.Process.myPid();
                                    android.os.Process.killProcess(pid);
                                    System.exit(1);
                                }

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        firebaseDatabase.getReference().child(context.getResources().getString(R.string.gameFirebase)).child(game.getGameId()).child(context.getResources().getString(R.string.pointsFirebase) + opponent).addChildEventListener(listenerForPoints);
        return listenerForPoints;
    }

    public void updatePoints(final Context context, final String pointsNode, int points, String gameId) {
        final FirebaseApp firebaseApp = FirebaseApp.initializeApp(context);
        if (firebaseApp == null) {
            // TODO to implement further
            killMultiplayerGame(context, ErrorInfo.UpdatePoints, false);
        }
        final FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance(firebaseApp);
        firebaseDatabase.getReference().child(context.getResources().getString(R.string.gameFirebase)).child(SinglePlayerActivity.game.getGameId()).child(pointsNode).child((gameId)).setValue(points);
    }

    public ChildEventListener chatListener(final Context context, final ListView mainListView) {
        final FirebaseApp firebaseApp = FirebaseApp.initializeApp(context);
        if (firebaseApp == null) {
            // TODO implement for multiplayer flow
            killMultiplayerGame(context, ErrorInfo.AddingChatListenerError, true);
            return null;
        }
        final FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance(firebaseApp);

        ChildEventListener chatEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                final FirebaseDatabase firebaseDatabaseNested = FirebaseDatabase.getInstance(firebaseApp);
                firebaseDatabaseNested.getReference().child(context.getResources().getString(R.string.gameFirebase)).child(SinglePlayerActivity.game.getGameId()).child(context.getResources().getString(R.string.messagesFirebase))
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                ArrayList<Message> messagesArray = new ArrayList<>();
                                for (DataSnapshot snapshots : snapshot.getChildren()) {
                                    messagesArray.add(snapshots.getValue(Message.class));
                                }
                                if (messagesArray.size() == 0) {
                                    return;
                                }
                                SinglePlayerActivity.game.getMessages().clear();
                                SinglePlayerActivity.game.setMessages(messagesArray);
                                MessageListAdapter listAdapter = new MessageListAdapter(context, R.layout.message_layout, SinglePlayerActivity.game.getMessages());
                                mainListView.setAdapter(listAdapter);
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });


            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };

        firebaseDatabase.getReference().child(context.getResources().getString(R.string.gameFirebase)).child(SinglePlayerActivity.game.getGameId()).child(context.getResources().getString(R.string.messagesFirebase)).addChildEventListener(chatEventListener);
        return chatEventListener;
    }

    public void sendMessange(final Context context, ArrayList<Message> messages) {
        final FirebaseApp firebaseApp = FirebaseApp.initializeApp(context);
        final FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance(firebaseApp);
        if (firebaseApp == null) {
            killMultiplayerGame(context, ErrorInfo.SendingMessage, true);
        }
        firebaseDatabase.getReference().child(context.getResources().getString(R.string.gameFirebase)).child(SinglePlayerActivity.game.getGameId()).child(context.getResources().getString(R.string.messagesFirebase)).setValue(messages);
    }

    public void registerUser(final Context context, final ShortPlayerInfo shortPlayerInfo) {
        final FirebaseApp firebaseApp = FirebaseApp.initializeApp(context);
        if (firebaseApp == null) {
            killMultiplayerGame(context, ErrorInfo.RegisterUserMultiplayer, true);
            return;
        }
        final FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance(firebaseApp);
        firebaseDatabase.getReference().child(context.getResources().getString(R.string.playerNode)).child(shortPlayerInfo.getUsername()).setValue(shortPlayerInfo);
    }

    public void removeResourcesMultiplayer() {
        // closing listeners
        if (this.referenceChallenge != null) {
            if (listenerForChallenge != null) {
                referenceChallenge.removeEventListener(listenerForChallenge);
                this.referenceChallenge = null;
                this.listenerForChallenge = null;
            }

        }
        if (referenceChallenger != null) {
            if (listenerChallengeResponse != null) {
                referenceChallenger.removeEventListener(listenerChallengeResponse);
                this.referenceChallenger = null;
                this.listenerChallengeResponse = null;
            }

        }
        if (referenceGame != null) {
            if (listenerForGame != null) {
                referenceGame.removeEventListener(listenerForGame);
                this.referenceGame = null;
                this.listenerForGame = null;
            }

        }
        if (referencePlayers != null) {
            if (listenerForPlayersOnline != null) {
                referencePlayers.removeEventListener(listenerForPlayersOnline);
                this.referencePlayers = null;
                this.listenerForPlayersOnline = null;
            }

        }
    }

    public void killMultiplayerGame(Context context, ErrorInfo info, Boolean errorFromMultiplayerActivity) {
        Toast.makeText(context, context.getResources().getString(R.string.connectionProblem), Toast.LENGTH_SHORT).show();
        if (errorFromMultiplayerActivity == null) {
            // DO nothing

        } else if (!errorFromMultiplayerActivity) {
            // Multiplayer game
        } else {
            Log.e(info.toString(), info.getValue());
            removeResourcesMultiplayer();
        }
        Intent intent = new Intent(context, MainActivity.class);
        context.startActivity(intent);
        ((Activity) context).finish();
    }
}
