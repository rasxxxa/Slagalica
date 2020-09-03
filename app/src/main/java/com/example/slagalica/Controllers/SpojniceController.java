package com.example.slagalica.Controllers;

import com.example.slagalica.HelperClasses.ResourceHelper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class SpojniceController {

    private static SpojniceController instance;

    private ArrayList<String> longWords;
    private Random random;

    private final int numberOfWordsForMatching = 8;

    private SpojniceController()
    {
        longWords = ResourceHelper.getInstance(null).getLongWords();
        random = ResourceHelper.getInstance(null).getRandomInstance();
    }

    public static SpojniceController getInstance()
    {
        if (instance == null)
        {
            instance = new SpojniceController();
        }
        return instance;
    }

    // Create string from list with every character
    private String fromListToString(ArrayList<String> ArrayList) {
        StringBuilder buffer = new StringBuilder();
        for (int i = 0; i < ArrayList.size(); i++) {
            buffer.append(ArrayList.get(i));
        }
        return buffer.toString();
    }

    // From every character from string create List
    private ArrayList<String> fromStringToList(String word) {
        ArrayList<String> ArrayList = new ArrayList<>();
        for (int i = 0; i < word.length(); i++) {
            ArrayList.add(String.valueOf(word.charAt(i)));
        }
        return ArrayList;
    }

    // Creating random 16 words for game. First 8 are straight words, and other 8 are shuffled version of first 8
    public ArrayList<String> getWordsForMatching()
    {
        ArrayList<String> wordsForMatching = new ArrayList<>();
        for (int i = 0; i < numberOfWordsForMatching; i++) {
            wordsForMatching.add(longWords.get(random.nextInt(longWords.size())));
        }

        // Get 8 random words and shuffle them
        for (int i = 0; i < numberOfWordsForMatching; i++) {
            ArrayList<String> wordToChar = fromStringToList(wordsForMatching.get(i));
            Collections.shuffle(wordToChar);
            String newWord = fromListToString(wordToChar);
            wordsForMatching.add(newWord);
        }
        return wordsForMatching;
    }

    // Check if two strings are equal (by sort)
    public Boolean checkTwoSortedStrings(String left, String right) {

        ArrayList<String> leftStringSorted = fromStringToList(left);
        ArrayList<String> rightStringSorted = fromStringToList(right);
        Collections.sort(leftStringSorted);
        Collections.sort(rightStringSorted);
        String newLeftSortedString = fromListToString(leftStringSorted);
        String newRightSortedString = fromListToString(rightStringSorted);

        return (newLeftSortedString.equals(newRightSortedString));
    }
}
