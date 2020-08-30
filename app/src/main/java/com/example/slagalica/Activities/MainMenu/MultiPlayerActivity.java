package com.example.slagalica.Activities.MainMenu;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.SearchView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.slagalica.Connection.ConnectionController;
import com.example.slagalica.HelperClasses.ErrorInfo;
import com.example.slagalica.MultiPlayer.Player;
import com.example.slagalica.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MultiPlayerActivity extends AppCompatActivity {

    private ConstraintLayout constraintLayout;
    private ArrayList<Player> players = new ArrayList<>();
    private ValueEventListener listenerForPlayersOnline;
    private ValueEventListener listenerForChallenge;
    private ValueEventListener listenerForGame;
    private ValueEventListener listenerChallengeResponse;
    private AlertDialog dialogAccept;
    private DatabaseReference referenceGame;
    private DatabaseReference referenceChallenger;
    private DatabaseReference referenceChallenge;
    private DatabaseReference referencePlayers;

    public DatabaseReference getReferencePlayers() {
        return referencePlayers;
    }

    public void setReferencePlayers(DatabaseReference referencePlayers) {
        this.referencePlayers = referencePlayers;
    }

    public DatabaseReference getReferenceGame() {
        return referenceGame;
    }

    public void setReferenceGame(DatabaseReference referenceGame) {
        this.referenceGame = referenceGame;
    }

    public DatabaseReference getReferenceChallenger() {
        return referenceChallenger;
    }

    public void setReferenceChallenger(DatabaseReference referenceChallenger) {
        this.referenceChallenger = referenceChallenger;
    }

    public DatabaseReference getReferenceChallenge() {
        return referenceChallenge;
    }

    public void setReferenceChallenge(DatabaseReference referenceChallenge) {
        this.referenceChallenge = referenceChallenge;
    }

    public AlertDialog getDialogAwait() {
        return dialogAwait;
    }

    public void setDialogAwait(AlertDialog dialogAwait) {
        this.dialogAwait = dialogAwait;
    }

    private AlertDialog dialogAwait;
    private ListView mainListView;
    private SearchView searchView;
    private ConnectionController connectionController;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multi_player);
        initialize();
    }


    public AlertDialog getDialog() {
        return dialogAccept;
    }

    public void setDialog(AlertDialog dialog) {
        this.dialogAccept = dialog;
    }

    public ValueEventListener getListenerChallengeResponse() {
        return listenerChallengeResponse;
    }

    public void setListenerChallengeResponse(ValueEventListener listenerChallengeResponse) {
        this.listenerChallengeResponse = listenerChallengeResponse;
    }

    public void initialize() {
        mainListView = findViewById(R.id.listViewMultiplayer);
        searchView = findViewById(R.id.searchView);
        connectionController = ConnectionController.getInstance();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                connectionController.getAdapter().getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                connectionController.getAdapter().getFilter().filter(newText);
                return false;
            }
        });
        players = new ArrayList<>();
        constraintLayout = findViewById(R.id.layoutMultiPlayer);
        SharedPreferences preferences = getSharedPreferences(MainActivity.settingsPreferencesKey, MODE_PRIVATE);
        String color = preferences.getString(getResources().getString(R.string.backgroundColorKey), getResources().getString(R.string.color1));
        constraintLayout.setBackgroundColor(Color.parseColor(color));
        initializeListOfPlayers();
    }

    public ValueEventListener getListenerForPlayersOnline() {
        return listenerForPlayersOnline;
    }

    public void setListenerForPlayersOnline(ValueEventListener listenerForPlayersOnline) {
        this.listenerForPlayersOnline = listenerForPlayersOnline;
    }

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

    private void initializeListOfPlayers() {
        try {

            listenerForPlayersOnline = connectionController.getOnlinePlayersListener(players, this, mainListView);
            listenerForChallenge = connectionController.getChallengePlayer(this);

        } catch (Exception e) {
            Log.e(ErrorInfo.MultiPlayerActivity.getValue(), e.toString());
            int pid = android.os.Process.myPid();
            android.os.Process.killProcess(pid);
            System.exit(1);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // remove user from online list
        connectionController.removePlayerForOnline(this, MainActivity.player);
        connectionController.removeResourcesMultiplayer(this);
        if (dialogAccept != null)
        {
            dialogAccept.dismiss();
        }
        if (dialogAwait != null)
        {
            dialogAwait.dismiss();
        }

    }


}
