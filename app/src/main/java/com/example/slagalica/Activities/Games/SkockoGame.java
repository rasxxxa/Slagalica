package com.example.slagalica.Activities.Games;

import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.Gravity;
import android.view.View;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.slagalica.Activities.MainMenu.MainActivity;
import com.example.slagalica.Activities.MainMenu.SinglePlayerActivity;
import com.example.slagalica.Connection.ConnectionController;
import com.example.slagalica.Controllers.SkockoController;
import com.example.slagalica.HelperClasses.DialogBuilder;
import com.example.slagalica.HelperClasses.TypeOfGame;
import com.example.slagalica.R;

import java.util.ArrayList;

public class SkockoGame extends AppCompatActivity implements GameInterface {

    private LinearLayout mainLayout;
    public static final String gameName = "Game_4";
    private int numOfColumns;
    private int numOfRows;
    private int numOfAdditionalButtons;
    private ArrayList<ImageView> leftGrid;
    private ArrayList<ImageView> rightGrid;
    private boolean gameOver = false;
    private boolean nextRow = true;
    private int position = 0;
    private int insertedElementInRow = 0;
    private int[] userComb;
    private int[] ResourceImageId;
    private ArrayList<Integer> combination;
    private CountDownTimer timer;
    private int timeClock;
    private int cloclInterval;
    private TextView textViewTimer;
    private int points = 0;
    private SkockoController controller;
    private ConnectionController connectionController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_skocko_game);
        initializeGame();
    }

    private void checkCombination() {
        if (insertedElementInRow == numOfColumns && !gameOver) {

            int[] result = controller.checkUserCombination(userComb, combination);
            int hits = result[0];
            int almostHits = result[1];

            for (int i = 0; i < hits; i++) {
                rightGrid.get(numOfColumns * (position / numOfColumns - 1) + i).setImageResource(R.drawable.hit);
            }
            for (int i = hits; i < hits + almostHits; i++) {
                rightGrid.get(numOfColumns * (position / numOfColumns - 1) + i).setImageResource(R.drawable.miss);
            }
            insertedElementInRow = 0;
            if (hits == numOfColumns || position >= leftGrid.size()) {
                gameOver = true;
            }

            if (gameOver) {
                SharedPreferences preferences = getSharedPreferences(MainActivity.historyPointsPreferencesKey[SinglePlayerActivity.typeOfGame.getValue()], MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();

                if (hits == numOfColumns) {
                    points = getResources().getInteger(R.integer.maxPointGame3) - getResources().getInteger(R.integer.rowMultiplierGame3) * (int) (Math.ceil(position / numOfColumns));
                    editor.putInt(gameName + getResources().getString(R.string.Points), points);
                    editor.apply();
                    endGame(false);

                } else {
                    editor.putInt(gameName + getResources().getString(R.string.Points), 0);
                    editor.apply();
                    endGame(true);
                }
                if (SinglePlayerActivity.typeOfGame == TypeOfGame.MultiPlayer) {
                    String pointsDatabase = getResources().getString(R.string.pointsFirebase) + MainActivity.typeOfPlayer;
                    int gameId = 3;
                    connectionController.updatePoints(this, pointsDatabase, points, String.valueOf(gameId));
                }

            }


        }
    }

    private void clearRow() {
        while (insertedElementInRow != 0) {
            userComb[insertedElementInRow - 1] = -1;
            insertedElementInRow--;
            leftGrid.get(--position).setImageResource(R.drawable.field);
        }

    }

    private void createGameFrontEnd() {
        textViewTimer = findViewById(R.id.textViewTimer);
        // creating layouts
        LinearLayout layoutContent = new LinearLayout(this);
        layoutContent.setGravity(Gravity.CENTER);


        LinearLayout layoutMenuButtons = new LinearLayout(this);
        GridLayout buttonsGrid = new GridLayout(this);
        buttonsGrid.setRowCount(numOfRows);
        buttonsGrid.setColumnCount(numOfColumns * 2);

        //Merging layouts and adding them to main layout
        layoutContent.addView(buttonsGrid);

        mainLayout.addView(layoutContent);
        mainLayout.addView(layoutMenuButtons);


        layoutContent.setOrientation(LinearLayout.HORIZONTAL);
        layoutMenuButtons.setOrientation(LinearLayout.HORIZONTAL);


        LinearLayout.LayoutParams buttonParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT, 1.0f);
        // Creating fields
        for (int i = 0; i < numOfRows; i++) {
            for (int j = 0; j < numOfColumns * 2; j++) {
                ImageView buttonGrid = new ImageView(this);
                buttonGrid.setLayoutParams(buttonParams);
                buttonGrid.setAdjustViewBounds(true);
                buttonGrid.setScaleType(ImageView.ScaleType.CENTER_CROP);

                buttonGrid.setImageResource(R.drawable.field);
                int sizeOfView = getResources().getInteger(R.integer.sizeOfImageGame3);
                buttonGrid.setMaxHeight(sizeOfView);
                buttonGrid.setMaxWidth(sizeOfView);
                if (j < numOfColumns) {
                    leftGrid.add(buttonGrid);
                } else {
                    rightGrid.add(buttonGrid);
                }
                buttonsGrid.addView(buttonGrid);
            }
        }

        for (int i = 0; i < numOfAdditionalButtons; i++) {
            ImageView additionalButton = new ImageView(this);
            additionalButton.setAdjustViewBounds(true);
            additionalButton.setScaleType(ImageView.ScaleType.FIT_CENTER);
            additionalButton.setLayoutParams(buttonParams);
            int sizeOfViewAdditional = getResources().getInteger(R.integer.sizeofImageAdditionalGame3);
            additionalButton.setMaxHeight(sizeOfViewAdditional);
            additionalButton.setMaxWidth(sizeOfViewAdditional);
            layoutMenuButtons.addView(additionalButton);
            if (i < ResourceImageId.length) {
                additionalButton.setImageResource(ResourceImageId[i]);
                final int positionResource = i;
                additionalButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (position < leftGrid.size() && !gameOver && nextRow) {
                            userComb[insertedElementInRow] = positionResource;
                            leftGrid.get(position++).setImageResource(ResourceImageId[positionResource]);
                            insertedElementInRow++;
                            if (position > 0 && insertedElementInRow == numOfColumns) {
                                nextRow = false;
                            }
                        }

                    }
                });
            } else if (i == ResourceImageId.length) {
                additionalButton.setImageResource(R.drawable.xbutton);
                additionalButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        nextRow = true;
                        SkockoGame.this.clearRow();
                    }
                });

            } else {
                additionalButton.setImageResource(R.drawable.okbutton);
                additionalButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (insertedElementInRow == numOfColumns) {
                            nextRow = true;
                            SkockoGame.this.checkCombination();
                        }

                    }
                });
            }
        }

        timer = new CountDownTimer(timeClock * 1000, cloclInterval * 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                SkockoGame.this.timeClock--;
                SkockoGame.this.textViewTimer.setText(String.valueOf(SkockoGame.this.timeClock));
            }

            @Override
            public void onFinish() {
                SkockoGame.this.endGame(true);
            }
        };
        timer.start();
    }

    @Override
    public void onBackPressed() {
        DialogBuilder
                .createAlertDialogForExit(this, timer)
                .show();
    }

    public void endGame(boolean timeOver) {
        timer.cancel();
        if (!timeOver) {
            DialogBuilder
                    .createDialogForGame(getResources().getString(R.string.gameOverMessage)
                            + points
                            + getResources().getString(R.string.points1), this)
                    .show();
        } else {
            // Prikazivanje resenja u alert dialogu
            LinearLayout.LayoutParams buttonParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f);
            LinearLayout layout = new LinearLayout(this);
            for (int i = 0; i < combination.size(); i++) {
                ImageView solutionForDialog = new ImageView(this);
                solutionForDialog.setLayoutParams(buttonParams);
                solutionForDialog.setAdjustViewBounds(true);
                solutionForDialog.setScaleType(ImageView.ScaleType.CENTER_CROP);
                int sizeOfView = getResources().getInteger(R.integer.sizeInAlertDialogGame3);
                solutionForDialog.setMaxWidth(sizeOfView);
                solutionForDialog.setMaxWidth(sizeOfView);
                solutionForDialog.setImageResource(ResourceImageId[combination.get(i)]);
                layout.addView(solutionForDialog);
            }
            AlertDialog.Builder builder1 = DialogBuilder
                    .createDialogForGame(getResources().getString(R.string.gameOverMessage)
                            + points
                            + getResources().getString(R.string.points1)
                            + getResources().getString(R.string.ourSolution), this);
            builder1.setView(layout);
            builder1.show();
        }
    }

    @Override
    public void initializeGame() {
        connectionController = ConnectionController.getInstance();
        controller = SkockoController.getInstance();
        ConstraintLayout constraintLayout = findViewById(R.id.layoutSkocko);
        SharedPreferences preferences = getSharedPreferences(MainActivity.settingsPreferencesKey, MODE_PRIVATE);
        String color = preferences.getString(getResources().getString(R.string.backgroundColorKey), getResources().getString(R.string.color1));
        constraintLayout.setBackgroundColor(Color.parseColor(color));
        ResourceImageId = controller.getResourceImageId();
        mainLayout = findViewById(R.id.mainLinearLayout);
        leftGrid = new ArrayList<>();
        rightGrid = new ArrayList<>();
        timeClock = getResources().getInteger(R.integer.timeGame3);
        cloclInterval = getResources().getInteger(R.integer.clockInterval);
        numOfColumns = controller.getNumberOfColumns();
        numOfRows = controller.getNumberOfRows();
        numOfAdditionalButtons = getResources().getInteger(R.integer.additionalButtonsGame3);
        createGameFrontEnd();
        combination = SinglePlayerActivity.game.getGame4Combination();
        userComb = new int[numOfColumns];
        for (int i = 0; i < numOfColumns; i++) {
            userComb[i] = -1;
        }
    }
}
