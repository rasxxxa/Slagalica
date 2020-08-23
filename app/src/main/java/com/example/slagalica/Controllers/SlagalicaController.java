package com.example.slagalica.Controllers;

import com.example.slagalica.HelperClasses.ResourceHelper;
import com.example.slagalica.HelperClasses.RunnableForLongestWord;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class SlagalicaController {

    private static final char[] allLetters = {'а', 'б', 'в', 'г', 'д', 'ђ', 'е', 'ж', 'з', 'и', 'ј', 'к', 'л', 'љ', 'м', 'н', 'њ', 'о', 'п', 'р', 'с', 'т', 'ћ', 'у', 'ф', 'х', 'ц', 'ч', 'џ', 'ђ'};

    private static SlagalicaController instance;

    private final int lettersCount = 12;

    // Threads for calculation for longest word
    private RunnableForLongestWord[] threadsForWordFinder;

    private ArrayList<String> words;
    private ArrayList<String> longWords;

    private SlagalicaController()
    {
        words = ResourceHelper.getInstance(null).getWords();
        longWords = ResourceHelper.getInstance(null).getLongWords();
    }
    // Singleton
    public static SlagalicaController getInstance()
    {
        if (instance == null)
        {
            instance = new SlagalicaController();
        }
        return instance;
    }

    // Getting letters for first game
    public ArrayList<String> getLetters()
    {
        ResourceHelper helperClass = ResourceHelper.getInstance(null);

        ArrayList<String> word = new ArrayList<>();

        Random random = helperClass.getRandomInstance();

        ArrayList<String> longWords = helperClass.getLongWords();

        int randomElement = Math.abs(random.nextInt()) % longWords.size();
        String someWord = longWords.get(randomElement);
        for (int i = 0; i < someWord.length(); i++) {
            word.add(String.valueOf(someWord.charAt(i)));
        }
        while (word.size() < lettersCount) {
            char randomLetter = allLetters[Math.abs(random.nextInt()) % allLetters.length];
            word.add(String.valueOf(randomLetter));
        }
        Collections.shuffle(word);
        return word;
    }

    // Thread calculation for finding longest word
    public void startLongestWordFinder(ArrayList<Character> letters)
    {
        RunnableForLongestWord.stopThread = false;
        ArrayList<Character> characters = new ArrayList<>(letters);
        int numOfThreads = (int)Math.log10(words.size());
        threadsForWordFinder = new RunnableForLongestWord[numOfThreads];
        int distance = words.size() / numOfThreads;
        for (int i = 0;i<numOfThreads;i++)
        {
            threadsForWordFinder[i] = new RunnableForLongestWord(i*distance,(i == numOfThreads-1)? words.size(): (i+1)*distance,letters,words);
            threadsForWordFinder[i].start();
        }
    }

    // Stoping threads if they still running and finding best result
    public String longestWord()
    {
        RunnableForLongestWord.stopThread = true;
        for (int i = 0; i<threadsForWordFinder.length;i++)
        {
            try {
                threadsForWordFinder[i].getThread().join();
            }catch (Exception e)
            {
                //
            }

        }
        String longestWord = "";
        for (int i = 0;i<threadsForWordFinder.length;i++)
        {
            if (threadsForWordFinder[i].getLongestWord().length() > longestWord.length())
            {
                longestWord = threadsForWordFinder[i].getLongestWord();
            }
        }
        return longestWord;
    }

    // Check if users word exist in list of words
    public boolean checkWord(String word) {
        return words.contains(word);
    }


}
