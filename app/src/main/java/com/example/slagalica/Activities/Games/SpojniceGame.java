package com.example.slagalica.Activities.Games;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.slagalica.Activities.MainMenu.MainActivity;
import com.example.slagalica.Activities.MainMenu.SinglePlayerActivity;
import com.example.slagalica.Connection.ConnectionController;
import com.example.slagalica.Controllers.SpojniceController;
import com.example.slagalica.HelperClasses.DialogBuilder;
import com.example.slagalica.HelperClasses.TypeOfGame;
import com.example.slagalica.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SpojniceGame extends AppCompatActivity implements GameInterface {

    private List<Button> leftButtons;
    private Button chosen;
    private Drawable defaultColor;
    private int playedFields = 0;
    private int sumOfPoints = 0;
    public static final String gameName = "Game_3";
    private CountDownTimer timer;
    private int timeClock;
    private int clockInterval;
    private TextView textViewTimer;
    private SpojniceController controller;
    private ConnectionController connectionController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spojnice_game);
        initializeGame();
    }

    public void endGame()
    {
        timer.cancel();
        DialogBuilder
                .createDialogForGame(getResources().getString(R.string.gameOverMessage)
                        + sumOfPoints
                        + getResources().getString(R.string.points1),this)
                .show();
    }

    @Override
    public void endGame(boolean timeOver) {
        endGame();
    }

    @Override
    public void initializeGame()
    {
        connectionController = ConnectionController.getInstance();
        controller = SpojniceController.getInstance();
        ConstraintLayout constraintLayout = findViewById(R.id.layoutSpojnice);
        SharedPreferences preferences = getSharedPreferences(MainActivity.settingsPreferencesKey,MODE_PRIVATE);
        String color = preferences.getString(getResources().getString(R.string.backgroundColorKey),getResources().getString(R.string.color1));
        constraintLayout.setBackgroundColor(Color.parseColor(color));
        timeClock = getResources().getInteger(R.integer.timeGame4);
        clockInterval = getResources().getInteger(R.integer.clockInterval);
        List<String> allWords;
        List<String> leftWords = new ArrayList<>();
        List<String> rightWords = new ArrayList<>();
        leftButtons = new ArrayList<>();
        chosen = null;
        textViewTimer = findViewById(R.id.textViewTimer);
        allWords = SinglePlayerActivity.game.getGame3Words();
        int numOfRows = getResources().getInteger(R.integer.numberOfElementsInRowGame4);
        for (int i = 0;i<numOfRows;i++)
        {
            leftWords.add(allWords.get(i));
            rightWords.add(allWords.get(i+numOfRows));
        }
        Collections.shuffle(rightWords);
        for (int i = 1;i<=numOfRows*2;i++)
        {

            int id = getResources().getIdentifier(getResources().getString(R.string.buttonPrefix) + i + gameName, "id", getPackageName());
            Button button = findViewById(id);
            if (i<=getResources().getInteger(R.integer.numberOfElementsInRowGame4))
            {
                button.setText(leftWords.get(i-1));
                leftButtons.add(button);
            }
            else
            {
                button.setText(rightWords.get(i - numOfRows - 1));
            }
        }
        // Initialize timer
        timer = new CountDownTimer(timeClock * 1000, clockInterval * 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                SpojniceGame.this.timeClock--;
                SpojniceGame.this.textViewTimer.setText(String.valueOf(SpojniceGame.this.timeClock));
            }

            @Override
            public void onFinish() {
                SpojniceGame.this.endGame();
            }
        };
        timer.start();

    }

    public void leftSideClick(View view)
    {
        if (chosen != null)
        {
            chosen.setBackground(defaultColor);
        }
        Button button = (Button)view;

        defaultColor = button.getBackground();
        button.setTextColor(Color.parseColor(getResources().getString(R.string.grayColor)));
        chosen = button;
    }

    public void rightSideClick(View view)
    {
        if (chosen==null)
        {
            return;
        }
        playedFields++;
        chosen.setClickable(false);
        Button rightSide = (Button)view;
        String leftString = chosen.getText().toString();
        String rightString = rightSide.getText().toString();

        if (controller.checkTwoSortedStrings(leftString,rightString))
        {
            chosen.setTextColor(Color.parseColor(getResources().getString(R.string.greenColor)));
            rightSide.setClickable(false);
            rightSide.setTextColor(Color.parseColor(getResources().getString(R.string.greenColor)));
            // Update points after every guess
            SharedPreferences preferences = getSharedPreferences(MainActivity.historyPointsPreferencesKey[SinglePlayerActivity.typeOfGame.getValue()],MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            int points = preferences.getInt(gameName+getResources().getString(R.string.Points),0);
            points += getResources().getInteger(R.integer.pointsOneCorrectGame4);
            editor.putInt(gameName+getResources().getString(R.string.Points),points);
            editor.apply();
            if (SinglePlayerActivity.typeOfGame == TypeOfGame.MultiPlayer)
            {
                String pointsDatabase = getResources().getString(R.string.pointsFirebase) + MainActivity.typeOfPlayer;
                int gameId = 2;
                connectionController.updatePoints(this,pointsDatabase,points,String.valueOf(gameId));
            }
            sumOfPoints += getResources().getInteger(R.integer.pointsOneCorrectGame4);
        }
        else
        {
            chosen.setTextColor(Color.parseColor(getResources().getString(R.string.redColor)));
        }
        chosen.setClickable(false);
        chosen = null;
        // If user clicks on every 8 words on left, game is over
        if (playedFields == leftButtons.size())
        {
            endGame();
        }

    }

    @Override
    public void onBackPressed() {
        DialogBuilder
                .createAlertDialogForExit(this,timer)
                .show();
    }
}
