package com.example.slagalica.Activities.MainMenu;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.slagalica.Activities.Chat.ChatActivity;
import com.example.slagalica.Activities.Games.AsocijacijeGame;
import com.example.slagalica.Activities.Games.KoZnaZnaGame;
import com.example.slagalica.Activities.Games.MojBrojGame;
import com.example.slagalica.Activities.Games.SkockoGame;
import com.example.slagalica.Activities.Games.SlagalicaGame;
import com.example.slagalica.Activities.Games.SpojniceGame;
import com.example.slagalica.Connection.ConnectionController;
import com.example.slagalica.Game.Game;
import com.example.slagalica.HelperClasses.DialogBuilder;
import com.example.slagalica.HelperClasses.ResourceHelper;
import com.example.slagalica.HelperClasses.TypeOfGame;
import com.example.slagalica.R;
import com.google.firebase.database.ChildEventListener;

import java.util.ArrayList;

public class SinglePlayerActivity extends AppCompatActivity {


    public static TypeOfGame typeOfGame;

    // Niz za proveru da li je korisnik igrao igru
    private boolean[] playedGames;
    public int[] pointsMainPlayer;
    public int[] pointsOpponent;
    private final int numberOfGames = 6;
    public static final String sharedPreferences = "History";
    public static Game game;

    // Cuvamo referencu jer listenera oce da pokupi garbage colector
    private SharedPreferences.OnSharedPreferenceChangeListener listener;
    private ArrayList<TextView> textViewsMainPlayerPoints;

    public int[] getPointsOpponent() {
        return pointsOpponent;
    }

    public void setPointsOpponent(int[] pointsOpponent) {
        this.pointsOpponent = pointsOpponent;
    }

    private ArrayList<TextView> textViewsOpponentPoints;
    private ChildEventListener pointListener;
    private TextView mainPlayerSumOfPoints;

    // Opponent points se skrivaju ako je single player
    private TextView opponentSumOfPoints;

    // Header se skriva ukoliko je singlePlayer;
    private TextView opponentLabel;

    private ConstraintLayout constraintLayout;

    private ConnectionController connectionController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_player);
        initialize();
    }

    @Override
    public void onBackPressed() {
        DialogBuilder.createAlertDialogForExit(this).show();
    }

    public void initialize() {

        if (typeOfGame == TypeOfGame.SinglePlayer) {
            game = ResourceHelper.getInstance(this).createSinglePlayerGame();
        } else {
            connectionController = ConnectionController.getInstance();
            MainActivity.typeOfPlayer = getIntent().getIntExtra(getResources().getString(R.string.typeofplayer), 1);
            final int opponent = MainActivity.typeOfPlayer == 1 ? 2 : 1;
            pointListener = connectionController.pointsUpdatedListener(this,opponent,game);
        }

        constraintLayout = findViewById(R.id.layoutSinglePlayer);
        SharedPreferences preferences = getSharedPreferences(MainActivity.settingsPreferencesKey, MODE_PRIVATE);
        String color = preferences.getString(getResources().getString(R.string.backgroundColorKey), getResources().getString(R.string.color1));
        constraintLayout.setBackgroundColor(Color.parseColor(color));
        playedGames = new boolean[MainActivity.numberOfGames];
        pointsMainPlayer = new int[MainActivity.numberOfGames];
        pointsOpponent = new int[MainActivity.numberOfGames];
        textViewsMainPlayerPoints = new ArrayList<>();
        textViewsOpponentPoints = new ArrayList<>();
        referenceElements();
        checkForOlderGame();
    }

    private void checkForOlderGame() {
        boolean olderGame = getIntent().getBooleanExtra(MainActivity.existGameInHistory, false);
        if (olderGame) {
            SharedPreferences preferencesPoints = getSharedPreferences(MainActivity.historyPointsPreferencesKey[typeOfGame.getValue()], MODE_PRIVATE);
            SharedPreferences preferencesGames = getSharedPreferences(MainActivity.historyGamesPreferencesKey[typeOfGame.getValue()], MODE_PRIVATE);
            for (int i = 1; i <= MainActivity.numberOfGames; i++) {
                pointsMainPlayer[i - 1] = preferencesPoints.getInt(getResources().getString(R.string.gamePrefix) + i + getResources().getString(R.string.Points), 0);
                playedGames[i - 1] = preferencesGames.getBoolean(getResources().getString(R.string.gamePrefix) + i, false);
            }
            updatePoints();
        }
    }

    private void referenceElements() {
        listener = new SharedPreferences.OnSharedPreferenceChangeListener() {
            public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
                int points = prefs.getInt(key, 0);
                switch (key) {
                    case (SlagalicaGame.gameName + "Points"):
                        pointsMainPlayer[0] = points;
                        updatePoints();
                        break;
                    case (MojBrojGame.gameName + "Points"):
                        pointsMainPlayer[1] = points;
                        updatePoints();
                        break;
                    case (SpojniceGame.gameName + "Points"):
                        pointsMainPlayer[2] = points;
                        updatePoints();
                        break;
                    case (SkockoGame.gameName + "Points"):
                        pointsMainPlayer[3] = points;
                        updatePoints();
                        break;
                    case (KoZnaZnaGame.gameName + "Points"):
                        pointsMainPlayer[4] = points;
                        updatePoints();
                        break;
                    case (AsocijacijeGame.gameName + "Points"):
                        pointsMainPlayer[5] = points;
                        updatePoints();
                        break;
                    default:
                        break;
                }
            }
        };
        getSharedPreferences(MainActivity.historyPointsPreferencesKey[typeOfGame.getValue()], MODE_PRIVATE).registerOnSharedPreferenceChangeListener(listener);
        for (int i = 1; i <= numberOfGames; i++) {
            int idMain = getResources().getIdentifier(getResources().getString(R.string.gamePrefix) + i + getResources().getString(R.string.mainPlyerPoints), "id", getPackageName());
            int idOpponent = getResources().getIdentifier(getResources().getString(R.string.gamePrefix) + i + getResources().getString(R.string.opponentPoints), "id", getPackageName());
            TextView textViewMainPlayerPoints = findViewById(idMain);
            TextView textViewOpponentPoints = findViewById(idOpponent);
            textViewsMainPlayerPoints.add(textViewMainPlayerPoints);
            textViewsOpponentPoints.add(textViewOpponentPoints);
        }

        Button buttonChat = findViewById(R.id.buttonGame7);
        buttonChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SinglePlayerActivity.this, ChatActivity.class);
                startActivity(intent);
            }
        });
        if (SinglePlayerActivity.typeOfGame == TypeOfGame.SinglePlayer) {
            buttonChat.setVisibility(View.INVISIBLE);
        } else {
            buttonChat.setVisibility(View.VISIBLE);
        }
        mainPlayerSumOfPoints = findViewById(R.id.SumPointsMainPlayer);
        opponentSumOfPoints = findViewById(R.id.SumPointsOpponent);

        opponentLabel = findViewById(R.id.textViewOpponent);

        if (TypeOfGame.SinglePlayer == typeOfGame) {
            for (int i = 0; i < numberOfGames; i++) {
                textViewsOpponentPoints.get(i).setVisibility(View.INVISIBLE);
            }
            opponentSumOfPoints.setVisibility(View.INVISIBLE);
            opponentLabel.setVisibility(View.INVISIBLE);
        }


    }

    private void checkGameInHistory(String gameId) {
        SharedPreferences.Editor editor = getSharedPreferences(MainActivity.historyGamesPreferencesKey[typeOfGame.getValue()], MODE_PRIVATE).edit();
        editor.putBoolean(gameId, true);
        editor.apply();
    }

    public void updatePoints() {
        int sum = 0;
        for (int i = 0; i < pointsMainPlayer.length; i++) {
            sum += pointsMainPlayer[i];
            textViewsMainPlayerPoints.get(i).setText((String.valueOf(pointsMainPlayer[i])));
            mainPlayerSumOfPoints.setText(String.valueOf(sum));
        }
        sum = 0;
        for (int i = 0; i < pointsOpponent.length; i++) {
            sum += pointsOpponent[i];
            textViewsOpponentPoints.get(i).setText((String.valueOf(pointsOpponent[i])));
            opponentSumOfPoints.setText(String.valueOf(sum));
        }
    }

    public void startSlagalica(View view) {

        if (!playedGames[0]) {
            playedGames[0] = true;
            checkGameInHistory(SlagalicaGame.gameName);
            Intent intent = new Intent(this, SlagalicaGame.class);
            startActivity(intent);
        }
    }

    public void startMojBroj(View view) {
        if (!playedGames[1]) {
            playedGames[1] = true;
            checkGameInHistory(MojBrojGame.gameName);
            Intent intent = new Intent(this, MojBrojGame.class);
            startActivity(intent);
        }

    }

    public void startSpojnice(View view) {
        if (!playedGames[2]) {
            playedGames[2] = true;
            checkGameInHistory(SpojniceGame.gameName);
            Intent intent = new Intent(this, SpojniceGame.class);
            startActivity(intent);
        }

    }

    public void startSkocko(View view) {
        if (!playedGames[3]) {
            playedGames[3] = true;
            checkGameInHistory(SkockoGame.gameName);
            Intent intent = new Intent(this, SkockoGame.class);
            startActivity(intent);
        }

    }

    public void startKoZnaZna(View view) {
        if (!playedGames[4]) {
            playedGames[4] = true;
            checkGameInHistory(KoZnaZnaGame.gameName);
            Intent intent = new Intent(this, KoZnaZnaGame.class);
            startActivity(intent);
        }

    }

    public void startAsocijacije(View view) {
        if (!playedGames[5]) {
            playedGames[5] = true;
            checkGameInHistory(AsocijacijeGame.gameName);
            Intent intent = new Intent(this, AsocijacijeGame.class);
            startActivity(intent);
        }

    }
}
