package com.example.slagalica.Controllers;

import com.example.slagalica.HelperClasses.ResourceHelper;
import com.example.slagalica.HelperClasses.Question;

import java.util.ArrayList;
import java.util.Random;

public class KoZnaZnaController {

    private static KoZnaZnaController instance;

    private ArrayList<Question> questionsGenerated;

    private Random random;

    private final int numberOfQuestions = 10;

    private KoZnaZnaController()
    {
        questionsGenerated = ResourceHelper.getInstance(null).getQuestions();
        random = ResourceHelper.getInstance(null).getRandomInstance();

    }

    public static KoZnaZnaController getInstance()
    {
        if (instance == null)
        {
            instance = new KoZnaZnaController();
        }
        return instance;
    }

    public Question getQuestionGenerated(int index) {
        if (index < questionsGenerated.size())
        {
            return questionsGenerated.get(index);
        }else
        {
            return questionsGenerated.get(0);
        }
    }

    public int getNumberOfQuestions() {
        return numberOfQuestions;
    }

    public ArrayList<Integer> generateQuestions() {
        ArrayList<Integer> newQuestions = new ArrayList<>();
        for (int i = 0; i < numberOfQuestions; i++) {
            newQuestions.add((random.nextInt(questionsGenerated.size())));
        }
        return newQuestions;
    }

}
