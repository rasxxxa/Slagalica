package com.example.slagalica.Activities.Games;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.slagalica.Activities.MainMenu.MainActivity;
import com.example.slagalica.Activities.MainMenu.SinglePlayerActivity;
import com.example.slagalica.Connection.ConnectionController;
import com.example.slagalica.Controllers.KoZnaZnaController;
import com.example.slagalica.HelperClasses.DialogBuilder;
import com.example.slagalica.HelperClasses.TypeOfGame;
import com.example.slagalica.HelperClasses.Question;
import com.example.slagalica.R;

import java.util.ArrayList;
import java.util.List;

public class KoZnaZnaGame extends AppCompatActivity implements GameInterface {

    private List<Question> questionList;
    private List<Button> answers;
    private TextView question;
    private int nextQuestion = 0;
    private Integer correctAnswer = null;
    private int sumOfPoints = 0;
    public static final String gameName = "Game_5";
    private CountDownTimer timer;
    private int timeClock;
    private static final int gameId = 4;
    private int cloclInterval;
    private TextView textViewTimer;
    private KoZnaZnaController controller;
    private ConnectionController connectionController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ko_zna_zna_game);
        initializeGame();
    }

    @Override
    public void initializeGame() {

        connectionController = ConnectionController.getInstance();
        controller = KoZnaZnaController.getInstance();
        ConstraintLayout constraintLayout = findViewById(R.id.layoutKoZnaZna);
        SharedPreferences preferences = getSharedPreferences(MainActivity.settingsPreferencesKey, MODE_PRIVATE);
        String color = preferences.getString(getResources().getString(R.string.backgroundColorKey), getResources().getString(R.string.color1));
        constraintLayout.setBackgroundColor(Color.parseColor(color));
        timeClock = getResources().getInteger(R.integer.timeGame5);
        cloclInterval = getResources().getInteger(R.integer.clockInterval);
        answers = new ArrayList<>();
        questionList = new ArrayList<>();
        ArrayList<Integer> questionNumber = SinglePlayerActivity.game.getGame5QuestionsNumbers();
        for (int i = 0; i < questionNumber.size(); i++) {
            Question question = controller.getQuestionGenerated(questionNumber.get(i));
            questionList.add(question);
        }
        textViewTimer = findViewById(R.id.textViewTimer);
        question = findViewById(R.id.textView_1Game_5);
        for (int i = 1; i <= getResources().getInteger(R.integer.numberOfAnswersGame5); i++) {
            int id = getResources().getIdentifier(getResources().getString(R.string.buttonPrefix) + i + gameName, "id", getPackageName());
            Button button = findViewById(id);
            answers.add(button);
        }
        playQuestion();
        // Creating timer
        timer = new CountDownTimer(timeClock * 1000, cloclInterval * 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                KoZnaZnaGame.this.timeClock--;
                KoZnaZnaGame.this.textViewTimer.setText(String.valueOf(KoZnaZnaGame.this.timeClock));
            }

            @Override
            public void onFinish() {
                KoZnaZnaGame.this.endGame();
            }
        };
        timer.start();

    }

    public void endGame() {
        timer.cancel();
        DialogBuilder
                .createDialogForGame(getResources().getString(R.string.gameOverMessage)
                        + sumOfPoints + getResources().getString(R.string.points1), this)
                .show();
    }

    @SuppressLint("DefaultLocale")
    private void playQuestion() {
        if (nextQuestion < controller.getNumberOfQuestions()) {

            Question questionNext = questionList.get(nextQuestion++);
            correctAnswer = questionNext.getCorrectAnswer();
            question.setText(String.format("%s%d. \n %s", getResources().getString(R.string.question), nextQuestion, questionNext.getQuestion()));
            for (int i = 0; i < getResources().getInteger(R.integer.numberOfAnswersGame5) - 1; i++) {
                answers.get(i).setText(questionNext.getPossibleAnswers().get(i));
            }
        } else {
            endGame();
        }
    }


    public void tryAnswerClick(View view) {

        Button button = (Button) view;
        Integer buttonFound = answers.indexOf(button);
        SharedPreferences preferences = getSharedPreferences(MainActivity.historyPointsPreferencesKey[SinglePlayerActivity.typeOfGame.getValue()], MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        int points = preferences.getInt(gameName + getResources().getString(R.string.Points), 0);
        if (buttonFound.equals(correctAnswer - 1)) {
            Toast.makeText(this, getResources().getString(R.string.correctAnswer), Toast.LENGTH_SHORT).show();
            int correctAnswer = getResources().getInteger(R.integer.correctAnswerPoints);
            points += correctAnswer;
            sumOfPoints += correctAnswer;
        } else {
            Toast.makeText(this, getResources().getString(R.string.wrongAnswer), Toast.LENGTH_SHORT).show();
            int wrongAnswer = getResources().getInteger(R.integer.wrongAnswerPenalty);
            points += wrongAnswer;
            sumOfPoints += wrongAnswer;
        }

        editor.putInt(gameName + getResources().getString(R.string.Points), points);
        editor.apply();
        if (SinglePlayerActivity.typeOfGame == TypeOfGame.MultiPlayer) {
            String pointsDatabase = getResources().getString(R.string.pointsFirebase) + MainActivity.typeOfPlayer;
            connectionController.updatePoints(this, pointsDatabase, points, String.valueOf(gameId));
        }
        playQuestion();
    }

    public void nextQuestionClick(View view) {
        playQuestion();
    }

    @Override
    public void endGame(boolean timeOver) {
        endGame();
    }

    @Override
    public void onBackPressed() {
        DialogBuilder.createAlertDialogForExit(this, timer).show();
    }
}
