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
import com.example.slagalica.Exceptions.ErrorInfo;
import com.example.slagalica.MultiPlayer.Player;
import com.example.slagalica.R;

import java.util.ArrayList;

public class MultiPlayerActivity extends AppCompatActivity {

    private ArrayList<Player> players = new ArrayList<>();
    private AlertDialog dialogAccept;

    public AlertDialog getDialogAwait() {
        return dialogAwait;
    }

    public void setDialogAwait(AlertDialog dialogAwait) {
        this.dialogAwait = dialogAwait;
    }

    private AlertDialog dialogAwait;
    private ListView mainListView;
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

    public void initialize() {
        mainListView = findViewById(R.id.listViewMultiplayer);
        SearchView searchView = findViewById(R.id.searchView);
        connectionController = ConnectionController.getInstance();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (players.size()==0)
                {
                    return false;
                }
                connectionController.getAdapter().getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (players.size()==0)
                {
                    return false;
                }
                connectionController.getAdapter().getFilter().filter(newText);
                return false;
            }
        });
        players = new ArrayList<>();
        ConstraintLayout constraintLayout = findViewById(R.id.layoutMultiPlayer);
        SharedPreferences preferences = getSharedPreferences(MainActivity.settingsPreferencesKey, MODE_PRIVATE);
        String color = preferences.getString(getResources().getString(R.string.backgroundColorKey), getResources().getString(R.string.color1));
        constraintLayout.setBackgroundColor(Color.parseColor(color));
        initializeListOfPlayers();
    }

    private void initializeListOfPlayers() {
        try {

            connectionController.getOnlinePlayersListener(players, this, mainListView);
            connectionController.getChallengePlayer(this);

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
        connectionController.removeResourcesMultiplayer();
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
