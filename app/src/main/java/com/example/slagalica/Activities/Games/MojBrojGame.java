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
import com.example.slagalica.Controllers.MojBrojController;
import com.example.slagalica.HelperClasses.DialogBuilder;
import com.example.slagalica.HelperClasses.TypeOfGame;
import com.example.slagalica.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class MojBrojGame extends AppCompatActivity implements GameInterface {

    // True for numbers, false for operation
    private Stack<Boolean> typeOfButtonClicked;
    private Stack<Button> allButtons;
    private EditText editText_1Game_2;
    private TextView textView_2Game_2;
    public static final String gameName = "Game_2";
    private Integer mainNumber;
    private CountDownTimer timer;
    private int timeClock;
    private int clockInterval;
    private TextView textViewTimer;
    private MojBrojController controller;
    private ConnectionController connectionController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_moj_broj_game);
        initializeGame();
    }

    public void endGame() {
        timer.cancel();
        StringBuilder eval = new StringBuilder();
        Stack<String> forEvalString = new Stack<>();
        for (Button button : allButtons) {
            eval.append(button.getText().toString());
            forEvalString.push(button.getText().toString());
        }
        Integer result = controller.resultOfEvaluation(forEvalString);
        int points = 0;
        String computerResult = getResources().getString(R.string.ourSolution) + controller.getResultOfCalculation() + " = ";
        int closest = controller.nearestResult();
        computerResult += closest;
        if (Math.abs(result - mainNumber) == 0) {
            points = getResources().getInteger(R.integer.pointsCorrectSolution);
        } else if (Math.abs(result - mainNumber) <= getResources().getInteger(R.integer.differenceToSoltuion)) {
            points = getResources().getInteger(R.integer.pointsNearSolution);
        }
        SharedPreferences preferences = getSharedPreferences(MainActivity.historyPointsPreferencesKey[SinglePlayerActivity.typeOfGame.getValue()], MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        if (SinglePlayerActivity.typeOfGame == TypeOfGame.MultiPlayer) {
            String pointsDatabase = getResources().getString(R.string.pointsFirebase) + MainActivity.typeOfPlayer;
            int gameId = 1;
            connectionController.updatePoints(this, pointsDatabase, points, String.valueOf(gameId));
        }
        if (points == 0) {
            editor.putInt(gameName + getResources().getString(R.string.Points), 0);
        } else {
            editor.putInt(gameName + getResources().getString(R.string.Points), points);
        }
        editor.apply();
        DialogBuilder
                .createDialogForGame(getResources().getString(R.string.gameOverMessage)
                        + points
                        + getResources().getString(R.string.points1)
                        + computerResult, this)
                .show();
    }

    public void submit(View view) {
        endGame();
    }

    public void xClick(View view) {
        editText_1Game_2.setText("");
        for (Button button : allButtons) {
            button.setEnabled(true);
        }
        allButtons = new Stack<>();
        typeOfButtonClicked = new Stack<>();
        changeEval();
    }

    @Override
    public void endGame(boolean timeOver) {
        endGame();
    }

    @Override
    public void initializeGame() {
        connectionController = ConnectionController.getInstance();
        controller = MojBrojController.getInstance();
        typeOfButtonClicked = new Stack<>();
        allButtons = new Stack<>();
        List<Integer> numbersGenerated = SinglePlayerActivity.game.getGame2Numbers();
        ConstraintLayout constraintLayout = findViewById(R.id.layoutMojBroj);
        SharedPreferences preferences = getSharedPreferences(MainActivity.settingsPreferencesKey, MODE_PRIVATE);
        String color = preferences.getString(getResources().getString(R.string.backgroundColorKey), getResources().getString(R.string.color1));
        constraintLayout.setBackgroundColor(Color.parseColor(color));
        timeClock = getResources().getInteger(R.integer.timeGame2);
        clockInterval = getResources().getInteger(R.integer.clockInterval);
        editText_1Game_2 = findViewById(R.id.editText_1Game_2);
        textView_2Game_2 = findViewById(R.id.textView2_Game2);
        textViewTimer = findViewById(R.id.textViewTimer);
        for (int i = 0; i < getResources().getInteger(R.integer.numberOfNumbers); i++) {

            int id = getResources().getIdentifier(getResources().getString(R.string.buttonPrefix) + (i + 1) + gameName, "id", getPackageName());
            Button button = findViewById(id);
            if (i == 0) {
                mainNumber = numbersGenerated.get(i);
                button.setText(String.valueOf(mainNumber));
                continue;
            }
            button.setText(String.valueOf(numbersGenerated.get(i)));
        }
        ArrayList<String> numbersString = new ArrayList<>();
        for (int i = 1; i < numbersGenerated.size(); i++) {
            numbersString.add(String.valueOf(numbersGenerated.get(i)));
        }
        controller.startComputerCalculation(mainNumber, numbersString);
        // Initialize timer
        timer = new CountDownTimer(timeClock * 1000, clockInterval * 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                MojBrojGame.this.timeClock--;
                MojBrojGame.this.textViewTimer.setText(String.valueOf(MojBrojGame.this.timeClock));
            }

            @Override
            public void onFinish() {
                MojBrojGame.this.endGame();
            }
        };
        timer.start();
    }

    public void numberClick(View view) {
        if (!typeOfButtonClicked.isEmpty()) {
            if (typeOfButtonClicked.peek()) {
                return;
            }

        }
        Button button = (Button) view;
        button.setEnabled(false);
        typeOfButtonClicked.push(true);
        allButtons.push(button);
        changeEval();
    }

    public void operationClick(View view) {
        Button button = (Button) view;
        typeOfButtonClicked.push(false);
        allButtons.push(button);
        changeEval();

    }

    public void cClick(View view) {
        Button button = allButtons.pop();
        Boolean temp = typeOfButtonClicked.pop();
        if (temp) {
            button.setEnabled(true);
        }
        changeEval();

    }

    public void changeEval() {
        StringBuilder eval = new StringBuilder();
        Stack<String> forEvalString = new Stack<>();
        for (Button button : allButtons) {
            eval.append(button.getText().toString());
            forEvalString.push(button.getText().toString());
        }
        editText_1Game_2.setText(eval.toString());
        Integer result = controller.resultOfEvaluation(forEvalString);
        textView_2Game_2.setText(String.valueOf(result));
    }

    @Override
    public void onBackPressed() {
        DialogBuilder.createAlertDialogForExit(this, timer).show();
    }
}
