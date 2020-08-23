package com.example.slagalica.Activities.Games;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.slagalica.Activities.MainMenu.MainActivity;
import com.example.slagalica.Activities.MainMenu.SinglePlayerActivity;
import com.example.slagalica.Connection.ConnectionController;
import com.example.slagalica.Controllers.SlagalicaController;
import com.example.slagalica.HelperClasses.DialogBuilder;
import com.example.slagalica.HelperClasses.TypeOfGame;
import com.example.slagalica.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class SlagalicaGame extends AppCompatActivity implements GameInterface {

    public static final String gameName = "Game_1";
    private EditText editTextUserInput;
    private Stack<Button> buttonClicked;
    private CountDownTimer timer;
    private int timeClock;
    private int clockInterval;
    private TextView textViewTimer;
    private SlagalicaController controller;
    private ConnectionController connectionController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_slagalica_game);
        initializeGame();
    }

    @Override
    public void initializeGame() {
        connectionController = ConnectionController.getInstance();
        controller = SlagalicaController.getInstance();
        ConstraintLayout constraintLayout = findViewById(R.id.layoutSlagalica);
        SharedPreferences preferences = getSharedPreferences(MainActivity.settingsPreferencesKey,MODE_PRIVATE);
        String color = preferences.getString(getResources().getString(R.string.backgroundColorKey),getResources().getString(R.string.color1));
        constraintLayout.setBackgroundColor(Color.parseColor(color));
        ArrayList<Button> letterButtons = new ArrayList<>();
        List<String> randomLetters;
        buttonClicked = new Stack<>();
        editTextUserInput = findViewById(R.id.editText1_Game1);
        textViewTimer = findViewById(R.id.textViewTimer);
        randomLetters = SinglePlayerActivity.game.getGame1Letters();
        timeClock = getResources().getInteger(R.integer.timeGame1);
        clockInterval = getResources().getInteger(R.integer.clockInterval);
        ArrayList<Character> lettersChar = new ArrayList<>();

        for (int i = 0; i < randomLetters.size(); i++) {
            lettersChar.add(randomLetters.get(i).charAt(0));
        }
        for (int i = 1; i <= getResources().getInteger(R.integer.numberOfLetters); i++) {
            int id = getResources().getIdentifier(getResources().getString(R.string.buttonPrefix) + i + gameName, "id", getPackageName());
            Button button = findViewById(id);
            button.setText(randomLetters.get(i - 1).toUpperCase());
            letterButtons.add(button);
        }
        controller.startLongestWordFinder(lettersChar);
        // Creating timer
        timer = new CountDownTimer(timeClock * 1000, clockInterval * 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                SlagalicaGame.this.timeClock--;
                SlagalicaGame.this.textViewTimer.setText(String.valueOf(SlagalicaGame.this.timeClock));
            }

            @Override
            public void onFinish() {
                SlagalicaGame.this.endGame();
            }
        };
        timer.start();
    }

    public void onClickLetterButton(View view) {
        Button clicked = (Button) view;
        String buttonLetter = clicked.getText().toString();
        clicked.setEnabled(false);
        editTextUserInput.setText(String.format("%s%s", editTextUserInput.getText().toString(), buttonLetter));
        buttonClicked.push(clicked);
    }

    public void submit(View view) {
        endGame();
    }

    public void endGame() {
        timer.cancel();
        String word = editTextUserInput.getText().toString().toLowerCase();
        int points = 0;
        SharedPreferences preferences = getSharedPreferences(MainActivity.historyPointsPreferencesKey[SinglePlayerActivity.typeOfGame.getValue()], MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        String pointLabel = getResources().getString(R.string.Points);

        if (controller.checkWord(word)) {
            points = word.length() * 2;
            editor.putInt(gameName + pointLabel, points);
            editor.apply();
        } else {
            editor.putInt(gameName + pointLabel, points);
            editor.apply();
        }
        if (SinglePlayerActivity.typeOfGame == TypeOfGame.MultiPlayer)
        {
            String pointsDatabase = getResources().getString(R.string.pointsFirebase) + MainActivity.typeOfPlayer;
            int gameId = 0;
            connectionController.updatePoints(this,pointsDatabase,points,String.valueOf(gameId));
        }
        String longestWord = controller.longestWord();

        DialogBuilder.createDialogForGame(getResources().getString(R.string.gameOverMessage)
                + points
                + getResources().getString(R.string.points1)
                + getResources().getString(R.string.ourSolution)
                + longestWord, this).show();
    }

    public void checkWord(View view) {
        if (buttonClicked.size() > 0) {
            String word = editTextUserInput.getText().toString().toLowerCase();

            if (controller.checkWord(word)) {
                //GREEN
                editTextUserInput.setBackgroundColor(Color.parseColor(getResources().getString(R.string.greenColor)));
            } else {
                //RED
                editTextUserInput.setBackgroundColor(Color.parseColor(getResources().getString(R.string.redColor)));
            }
        }

    }

    public void deleteLetter(View view) {

        editTextUserInput.setBackgroundColor(Color.parseColor(getResources().getString(R.string.transparentColor)));
        if (buttonClicked.size() > 0) {
            Button button = buttonClicked.pop();
            button.setEnabled(true);
            String text = editTextUserInput.getText().toString();
            StringBuffer newWord = new StringBuffer();
            for (int i = 0; i < text.length() - 1; i++) {
                newWord.append(text.charAt(i));
            }
            editTextUserInput.setText(newWord);
        }

    }

    public void eraseAll(View view) {
        editTextUserInput.setBackgroundColor(Color.parseColor(getResources().getString(R.string.transparentColor)));
        editTextUserInput.setText("");
        while (buttonClicked.size() != 0) {
            Button button = buttonClicked.pop();
            button.setEnabled(true);
        }
    }

    @Override
    public void endGame(boolean timeOver) {
        endGame();
    }

    @Override
    public void onBackPressed() {
        DialogBuilder.createAlertDialogForExit(this,timer).show();
    }
}
