package com.example.slagalica.Activities.MainMenu;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.slagalica.Connection.ConnectionController;
import com.example.slagalica.HelperClasses.DialogBuilder;
import com.example.slagalica.HelperClasses.ResourceHelper;
import com.example.slagalica.HelperClasses.TypeOfGame;
import com.example.slagalica.MultiPlayer.Player;
import com.example.slagalica.R;

import pl.droidsonroids.gif.GifImageView;

public class MainActivity extends AppCompatActivity {

    public static final String existGameInHistory = "OldGame";
    public static final String[] historyGamesPreferencesKey = {"HistoryGames", "HistoryGamesMultiPlayer"};
    public static final String[] historyPointsPreferencesKey = {"HistoryPoints", "HistoryPointsMultiPlayer"};
    public static final String settingsPreferencesKey = "Settings";
    public static ResourceHelper helperClass;
    public static int typeOfGame;
    public static int typeOfPlayer;
    public static final int numberOfGames = 6;
    private ConstraintLayout constraintLayout;
    private GifImageView gifImageView;
    public static Player player;
    private ConnectionController connectionController;

    /**
     * Need to be saved as reference not as local, because sometimes garbage collector clean his value
     * and listener does not work anymore
     */
    private SharedPreferences.OnSharedPreferenceChangeListener listener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initialize();
    }

    public void initialize() {
        helperClass = ResourceHelper.getInstance(this);
        connectionController = ConnectionController.getInstance();
        SharedPreferences preferences = getSharedPreferences(settingsPreferencesKey, MODE_PRIVATE);
        String color = preferences.getString(getResources().getString(R.string.backgroundColorKey), getResources().getString(R.string.color1));
        constraintLayout = findViewById(R.id.layoutMainActivity);
        constraintLayout.setBackgroundColor(Color.parseColor(color));
        gifImageView = findViewById(R.id.gifImageView);
        listener = new SharedPreferences.OnSharedPreferenceChangeListener() {
            @Override
            public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
                if (key.equals(getResources().getString(R.string.backgroundColorKey))) {
                    String color = sharedPreferences.getString(getResources().getString(R.string.backgroundColorKey), getResources().getString(R.string.color1));
                    constraintLayout.setBackgroundColor(Color.parseColor(color));
                }
            }
        };
        SinglePlayerActivity.game = null;
        typeOfPlayer = 1;
        typeOfGame = 1;
        preferences.registerOnSharedPreferenceChangeListener(listener);
        preferences.edit().apply();
        gifImageView.setVisibility(View.INVISIBLE);
    }


    @Override
    protected void onResume() {
        super.onResume();
        // Setting some variables, semaphores...
        SinglePlayerActivity.game = null;
        typeOfPlayer = 1;
        typeOfGame = 1;
    }

    public void startSinglePlayer(View view) {
        //Creating new Intent on click
        boolean[] playedGamesFromHistory = new boolean[numberOfGames];
        boolean existHistory = false;
        SharedPreferences preferences = getSharedPreferences(historyGamesPreferencesKey[0], MODE_PRIVATE);
        for (int i = 1; i <= numberOfGames; i++) {
            playedGamesFromHistory[i - 1] = preferences.getBoolean(getResources().getString(R.string.gamePrefix) + i, false);
            existHistory = existHistory || playedGamesFromHistory[i - 1];
        }
        if (existHistory) {
            DialogBuilder.createAlertDialogForNewGame(this).show();
        } else {
            Intent intent = new Intent(MainActivity.this, SinglePlayerActivity.class);
            intent.putExtra(existGameInHistory, false);
            typeOfGame = 1;
            typeOfPlayer = 1;
            SinglePlayerActivity.typeOfGame = TypeOfGame.SinglePlayer;
            startActivity(intent);
        }
    }

    public void startMultiPlayer(View view) {
        final SharedPreferences preferences = getSharedPreferences(settingsPreferencesKey, MODE_PRIVATE);
        final String username = preferences.getString(getResources().getString(R.string.usernameKey), "");
        assert username != null;
        if (username.equals("") || username.equals(" ")) {
            DialogBuilder.createAlertDialog(this).show();
        } else {
            // If user exit from multiplayer, and then fastly goes again in multiplayer, there is a chance
            // that data in database are not updated so a little time to wait for clearing the data
            gifImageView.setVisibility(View.VISIBLE);
            final CountDownTimer timer = new CountDownTimer(2000, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {

                }

                @Override
                public void onFinish() {
                    gifImageView.setVisibility(View.INVISIBLE);
                    createMultiplayerActivity(preferences, username);
                }
            };
            timer.start();
        }

    }

    public void createMultiplayerActivity(SharedPreferences preferences, String username) {

        Intent intent = new Intent(this, MultiPlayerActivity.class);
        SharedPreferences.Editor editorGames = getSharedPreferences(historyGamesPreferencesKey[TypeOfGame.MultiPlayer.getValue()], MODE_PRIVATE).edit();
        editorGames.clear();
        editorGames.apply();
        SharedPreferences.Editor editorPoints = getSharedPreferences(historyPointsPreferencesKey[TypeOfGame.MultiPlayer.getValue()], MODE_PRIVATE).edit();
        editorPoints.clear();
        editorPoints.apply();
        typeOfGame = 2;
        String name = preferences.getString(getResources().getString(R.string.nameKey), "");
        String lastName = preferences.getString(getResources().getString(R.string.lastnameKey), "");
        player = new Player();
        player.setUsername(username);
        player.setName(name);
        player.setLastname(lastName);
        if (connectionController.addOnlinePlayer(this, username, player)) {
            startActivity(intent);
        } else {
            Toast.makeText(this, getResources().getString(R.string.connectionProblem), Toast.LENGTH_SHORT).show();
        }
    }

    public void startSettings(View view) {
        Intent intent = new Intent(this, Settings.class);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        DialogBuilder.exitGame(this).show();
    }
}
