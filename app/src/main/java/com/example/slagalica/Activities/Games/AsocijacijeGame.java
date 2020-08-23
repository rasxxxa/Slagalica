package com.example.slagalica.Activities.Games;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.slagalica.Activities.MainMenu.MainActivity;
import com.example.slagalica.Activities.MainMenu.SinglePlayerActivity;
import com.example.slagalica.Connection.ConnectionController;
import com.example.slagalica.Controllers.AsocijacijeController;
import com.example.slagalica.HelperClasses.Association;
import com.example.slagalica.HelperClasses.DialogBuilder;
import com.example.slagalica.HelperClasses.TypeOfGame;
import com.example.slagalica.R;

import java.util.ArrayList;
import java.util.Objects;

public class AsocijacijeGame extends AppCompatActivity implements GameInterface {
    private static final String[] Columns = {"A", "B", "C", "D"};
    private static final String[] ColumnsCyrilic = {"А", "Б", "Ц", "Д"};
    private static final String[] ColumnNumbers = {"1", "2", "3", "4"};
    public static final String gameName = "Game_6";
    private ArrayList<EditText> editTexts;
    private Association association;
    private int sumOfPoints = 0;
    private EditText mainSolution;
    private ArrayList<ArrayList<Button>> buttons;
    private CountDownTimer timer;
    private int timeClock;
    private int clockInterval;
    private TextView textViewTimer;
    private int pointsField;
    private int pointsColumn;
    private int pointsMain;
    private final int gameId = 5;
    private AsocijacijeController controller;
    private ConnectionController connectionController;
    private boolean[] solved;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_asocijacije_game);
        initializeGame();
    }

    public void initializeGame() {
        connectionController = ConnectionController.getInstance();
        controller = AsocijacijeController.getInstance();
        ConstraintLayout constraintLayout = findViewById(R.id.layoutAsocijacije);
        SharedPreferences preferences = getSharedPreferences(MainActivity.settingsPreferencesKey, MODE_PRIVATE);
        String color = preferences.getString(getResources().getString(R.string.backgroundColorKey), getResources().getString(R.string.color1));
        constraintLayout.setBackgroundColor(Color.parseColor(color));
        pointsField = getResources().getInteger(R.integer.pointsFieldGame6);
        pointsColumn = getResources().getInteger(R.integer.pointsColumnGame6);
        pointsMain = getResources().getInteger(R.integer.pointsMainSolutionGame6);
        clockInterval = getResources().getInteger(R.integer.clockInterval);
        timeClock = getResources().getInteger(R.integer.timeGame6);
        solved = new boolean[4];
        buttons = new ArrayList<>();
        editTexts = new ArrayList<>();
        setButtons();
        setEditTexts();
        textViewTimer = findViewById(R.id.textViewTimer);
        association = controller.getAssociation(SinglePlayerActivity.game.getAssociationNumber());
        timer = new CountDownTimer(timeClock * 1000, clockInterval * 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                AsocijacijeGame.this.timeClock--;
                AsocijacijeGame.this.textViewTimer.setText(String.valueOf(AsocijacijeGame.this.timeClock));
            }

            @Override
            public void onFinish() {
                AsocijacijeGame.this.endGame(true);
            }
        };
        timer.start();
    }

    @Override
    public void endGame(boolean timeOut) {
        timer.cancel();
        if (timeOut) {
            solveMain(true);
        }
    }

    private void solveColumn(int column, boolean timeOut) {
        if (solved[column]) {
            return;
        }
        solved[column] = true;
        ArrayList<String> columnWords = association.getColumnsList().get(column).getFields();
        for (int i = 0; i < columnWords.size(); i++) {
            Button matched = buttons.get(column).get(i);
            if (matched.isEnabled() && !timeOut) {
                SharedPreferences preferences = getSharedPreferences(MainActivity.historyPointsPreferencesKey[SinglePlayerActivity.typeOfGame.getValue()], MODE_PRIVATE);
                int points = preferences.getInt(gameName + getResources().getString(R.string.Points), 0);
                points += pointsField;
                SharedPreferences.Editor editor = preferences.edit();
                editor.putInt(gameName + getResources().getString(R.string.Points), points);
                editor.apply();
                if (SinglePlayerActivity.typeOfGame == TypeOfGame.MultiPlayer) {
                    String pointsDatabase = getResources().getString(R.string.pointsFirebase) + MainActivity.typeOfPlayer;
                    connectionController.updatePoints(this, pointsDatabase, points, String.valueOf(gameId));
                }
                sumOfPoints += pointsField;
            }
            if (!timeOut) {
                matched.setTextColor(Color.parseColor(getResources().getString(R.string.greenColor)));
            } else {
                matched.setTextColor(Color.parseColor(getResources().getString(R.string.redColor)));
            }
            matched.setText(columnWords.get(i));
            matched.setEnabled(false);
        }
        EditText editText = editTexts.get(column);
        editText.setText(association.getColumnsList().get(column).getSolution());
        if (editText.isEnabled() && !timeOut) {
            SharedPreferences preferences = getSharedPreferences(MainActivity.historyPointsPreferencesKey[SinglePlayerActivity.typeOfGame.getValue()], MODE_PRIVATE);
            int points = preferences.getInt(gameName + getResources().getString(R.string.Points), 0);
            points += pointsColumn;
            SharedPreferences.Editor editor = preferences.edit();
            editor.putInt(gameName + getResources().getString(R.string.Points), points);
            editor.apply();
            if (SinglePlayerActivity.typeOfGame == TypeOfGame.MultiPlayer) {
                String pointsDatabase = getResources().getString(R.string.pointsFirebase) + MainActivity.typeOfPlayer;
                connectionController.updatePoints(this, pointsDatabase, points, String.valueOf(gameId));
            }
            sumOfPoints += pointsColumn;

        }
        if (editText.isEnabled()) {
            lockEditText(editText, timeOut);
        }

    }

    private void solveMain(boolean timeOut) {
        SharedPreferences preferences = getSharedPreferences(MainActivity.historyPointsPreferencesKey[SinglePlayerActivity.typeOfGame.getValue()], MODE_PRIVATE);
        if (!timeOut) {
            int points = preferences.getInt(gameName + getResources().getString(R.string.Points), 0);
            points += pointsMain;
            SharedPreferences.Editor editor = preferences.edit();
            editor.putInt(gameName + getResources().getString(R.string.Points), points);
            editor.apply();
            if (SinglePlayerActivity.typeOfGame == TypeOfGame.MultiPlayer) {
                String pointsDatabase = getResources().getString(R.string.pointsFirebase) + MainActivity.typeOfPlayer;
                connectionController.updatePoints(this, pointsDatabase, points, String.valueOf(gameId));
            }
            sumOfPoints += pointsMain;
        }

        for (int i = 0; i < association.getColumnsList().size(); i++) {
            solveColumn(i, timeOut);
        }
        mainSolution.setText(association.getMainSolution());
        lockEditText(mainSolution, timeOut);
        String message = getResources().getString(R.string.gameOverMessage) + sumOfPoints + getResources().getString(R.string.points1);
        if (timeOut) {
            message += " \n " + getResources().getString(R.string.finalSolutionGame6) + association.getMainSolution();
        }
        DialogBuilder
                .createDialogForGame(message, this)
                .show();

    }

    private void setEditTexts() {
        for (int column = 0; column < ColumnNumbers.length; column++) {
            int id = getResources().getIdentifier(getResources().getString(R.string.editTextPrefix) + Columns[column] + gameName, "id", getPackageName());
            final EditText editText = findViewById(id);
            final int columnNumber = column;
            editText.setOnKeyListener(new View.OnKeyListener() {
                @Override
                public boolean onKey(View v, int keyCode, KeyEvent event) {
                    if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                        String text = association.getColumnsList().get(columnNumber).getSolution();
                        boolean matchedWords;
                        try {
                            matchedWords = controller.MatchWords(text, editText.getText().toString(), false);
                        } catch (Exception e) {
                            matchedWords = false;
                        }
                        try {
                            // if check for cyrilic or latin matching between two words
                            matchedWords = matchedWords || controller.MatchWords(text, editText.getText().toString(), true);
                        } catch (Exception e) {
                            // Just handle
                        }
                        if (matchedWords) {
                            AsocijacijeGame.this.solveColumn(columnNumber, false);

                        } else {
                            editText.setTextColor(Color.parseColor(getResources().getString(R.string.redColor)));
                        }

                        if (editText.isFocused()) {
                            hideSoftKeyboard(AsocijacijeGame.this);
                        }

                    }
                    return false;
                }
            });
            editTexts.add(editText);
        }
        mainSolution = findViewById(R.id.main_solution);
        mainSolution.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    String text = association.getMainSolution();
                    boolean matchedWords;
                    try {
                        matchedWords = controller.MatchWords(text, mainSolution.getText().toString(), false);
                    } catch (Exception e) {
                        matchedWords = false;
                    }
                    try {
                        matchedWords = matchedWords || controller.MatchWords(text, mainSolution.getText().toString(), true);
                    } catch (Exception e) {
                        // Just handle
                    }
                    if (matchedWords) {
                        AsocijacijeGame.this.solveMain(false);
                    } else {
                        mainSolution.setTextColor(Color.parseColor(getResources().getString(R.string.redColor)));
                    }
                    if (mainSolution.isFocused()) {
                        hideSoftKeyboard(AsocijacijeGame.this);
                    }


                }
                return false;
            }
        });
    }

    private void setButtons() {
        for (int column = 0; column < ColumnsCyrilic.length; column++) {
            ArrayList<Button> buttonInColumn = new ArrayList<>();
            String textForId = Columns[column];
            String textForButton = ColumnsCyrilic[column];

            for (String columnNumber : ColumnNumbers) {
                int id = getResources().getIdentifier(getResources().getString(R.string.buttonPrefix) + textForId + columnNumber + gameName, "id", getPackageName());
                Button button = findViewById(id);
                String textForButtonConcatenated = textForButton + columnNumber;
                button.setText(textForButtonConcatenated);
                buttonInColumn.add(button);
            }
            buttons.add(buttonInColumn);
        }
    }

    public void openField(View view) {
        int posI = -1, posJ = -1;
        Button clicked = (Button) view;
        for (int i = 0; i < buttons.size() && (posI == -1 && posJ == -1); i++) {
            ArrayList<Button> buttonInColumn = buttons.get(i);
            for (int j = 0; j < buttonInColumn.size(); j++) {
                Button button = buttonInColumn.get(j);
                if (button.getId() == clicked.getId()) {
                    posI = i;
                    posJ = j;
                    break;
                }
            }
        }
        if (posI != -1) {
            clicked.setText(association.getColumnsList().get(posI).getFields().get(posJ));
            clicked.setEnabled(false);
        }
    }

    @Override
    public void onBackPressed() {
        DialogBuilder.createAlertDialogForExit(this, timer).show();
    }

    private void lockEditText(EditText editText, boolean timeout) {
        if (timeout) {
            editText.setTextColor(Color.parseColor(getResources().getString(R.string.redColor)));
        } else {
            editText.setTextColor(Color.parseColor(getResources().getString(R.string.greenColor)));
        }
        editText.setFocusable(false);
        editText.setEnabled(false);
        editText.setKeyListener(null);
        editText.setCursorVisible(false);
        editText.setBackgroundColor(Color.TRANSPARENT);
    }

    // Collapse keyboard if keyboard is in focus
    private void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager =
                (InputMethodManager) activity.getSystemService(
                        Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(
                Objects.requireNonNull(activity.getCurrentFocus()).getWindowToken(), 0);
    }
}
