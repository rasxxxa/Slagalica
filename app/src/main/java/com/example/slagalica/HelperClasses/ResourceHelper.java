package com.example.slagalica.HelperClasses;

import android.content.Context;
import android.util.Log;

import com.example.slagalica.Controllers.AsocijacijeController;
import com.example.slagalica.Controllers.KoZnaZnaController;
import com.example.slagalica.Controllers.MojBrojController;
import com.example.slagalica.Controllers.SkockoController;
import com.example.slagalica.Controllers.SlagalicaController;
import com.example.slagalica.Controllers.SpojniceController;
import com.example.slagalica.Game.Game;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Random;


public class ResourceHelper {

    private ArrayList<String> words;

    public ArrayList<String> getLongWords() {
        return longWords;
    }

    private ArrayList<String> longWords;

    private ArrayList<Association> associations;

    private ArrayList<Question> questions;

    private int minimumLengthForKey = 15;

    private Random random;

    private static ResourceHelper instance;

    private ArrayList<Thread> threads;

    private static final String pathWords = "text_resource/reci2.txt";

    private static final String pathAssociation = "text_resource/asocijacije.txt";

    private static final String pathQuestions = "text_resource/pitanja.txt";

    public ArrayList<String> getWords() {
        if (threads.get(0).isAlive()) {
            try {
                threads.get(0).join();
            } catch (Exception e) {
                Log.e("Thread", "Thread interupted");
            }

        }
        return words;
    }

    private ResourceHelper(final Context context) {
        words = new ArrayList<>();
        questions = new ArrayList<>();
        associations = new ArrayList<>();
        longWords = new ArrayList<>();
        random = new Random();
        threads = new ArrayList<>();
        threads.add(new Thread(

                new Runnable() {
                    @Override
                    public void run() {
                        readWords(context);
                    }
                }

        ));

        threads.add(new Thread(

                new Runnable() {
                    @Override
                    public void run() {
                        readQuestions(context);
                    }
                }

        ));

        threads.add(new Thread(

                new Runnable() {
                    @Override
                    public void run() {
                        readAssociation(context);
                    }
                }

        ));


        for (int i = 0; i < threads.size(); i++) {
            threads.get(i).start();
        }
    }

    private void readQuestions(Context context) {
        try {
            // Creating input stream from file
            InputStream inputStream = context.getAssets().open(pathQuestions);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            //reading file
            while ((line = bufferedReader.readLine()) != null) {
                StringBuilder question = new StringBuilder();
                String sampleLine = line;

                while (sampleLine.charAt(0) != 'а' || sampleLine.charAt(1) != ')') {

                    question.append(sampleLine).append(" ");
                    sampleLine = bufferedReader.readLine();

                }

                String responseA = sampleLine;
                String responseB = bufferedReader.readLine();
                String responseC = bufferedReader.readLine();
                int answer = Integer.parseInt(bufferedReader.readLine());

                //Creating question and adding question to ArrayList of questions
                Question fQuestion = new Question(question.toString());
                fQuestion.addPossibleAnswers(responseA);
                fQuestion.addPossibleAnswers(responseB);
                fQuestion.addPossibleAnswers(responseC);
                fQuestion.setCorrectAnswer(answer);
                questions.add(fQuestion);
            }


        } catch (IOException e) {
            Log.e("Input stream error", e.toString());
            e.printStackTrace();
        }
    }

    private void readWords(Context context) {
        try {
            // Creating input stream from file
            InputStream inputStream = context.getAssets().open(pathWords);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                words.add(line);
                if (line.length() >= 10 && line.length() <= 12) {
                    longWords.add(line);
                }
            }


        } catch (IOException e) {
            Log.e("Input stream error", e.toString());
            e.printStackTrace();
        }

    }

    public static ResourceHelper getInstance(Context context) {
        if (instance == null) {
            instance = new ResourceHelper(context);
        }
        return instance;
    }

    private void readAssociation(Context context) {
        try {
            // Creating input stream from file
            InputStream inputStream = context.getAssets().open(pathAssociation);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                Association association = new Association();
                for (int i = 0; i < 4; i++) {
                    Column column = new Column();
                    for (int j = 0; j < 4; j++) {
                        column.add(line.toUpperCase());
                        line = bufferedReader.readLine().toUpperCase();
                    }
                    column.setSolution(line);
                    association.addColumn(column);
                    line = bufferedReader.readLine();
                }
                association.setMainSolution(line.toUpperCase());
                associations.add(association);


            }
        } catch (Exception e) {
            Log.e("INPUTOUTPUT", "Error reading file");
        }

    }

    public ArrayList<Association> getAssociations() {
        if (threads.get(2).isAlive()) {
            try {
                threads.get(2).join();
            } catch (Exception e) {
                Log.e("Thread", "Thread is interupted");
            }

        }
        return associations;
    }

    public ArrayList<Question> getQuestions() {
        if (threads.get(1).isAlive()) {
            try {
                threads.get(1).join();
            } catch (Exception e) {
                Log.e("Thread", "Thread is interupted");
            }

        }
        return questions;
    }


    public Random getRandomInstance() {return random;}

    public Game createSinglePlayerGame()
    {
        Game game = new Game();
        // Game 1
        game.setGame1Letters(SlagalicaController.getInstance().getLetters());

        // Game 2
        game.setGame2Numbers(MojBrojController.getInstance().getNumbers());

        // Game 3
        game.setGame3Words(SpojniceController.getInstance().getWordsForMatching());

        // Game 4
        game.setGame4Combination(SkockoController.getInstance().getRandomCombination());

        // Game 5
        game.setGame5QuestionsNumbers(KoZnaZnaController.getInstance().generateQuestions());

        // Game 6
        game.setAssociationNumber(AsocijacijeController.getInstance().getAssociationNumber());

        return game;
    }



    public String createRandomKey(String username)
    {
        StringBuilder key = new StringBuilder(username);

        int keyLenght = Math.abs(random.nextInt(20)) +minimumLengthForKey;

        for (int i = 0; i<keyLenght;i++)
        {
            int charSelected = random.nextInt(26) + 65; // First value in ascii table
            int bigCaps = random.nextInt(2);
            if (bigCaps == 0)
            {
                charSelected += 32;
            }
            key.append((char) (charSelected));
        }
        return key.toString();
    }
}
