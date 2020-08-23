package com.example.slagalica.HelperClasses;

import android.util.Log;

import java.util.ArrayList;

public class RunnableForLongestWord implements Runnable {

    private Thread thread;
    private int l,r;
    public static volatile boolean stopThread = false;
    private ArrayList<Character> letters;
    private String longestWord = "";
    private ArrayList<String> allWords;
    public RunnableForLongestWord(int l, int r, final ArrayList<Character> letters, final ArrayList<String> allWords)
    {
        this.letters = letters;
        this.l = l;
        this.r = r;
        this.allWords = allWords;
    }
    @Override
    public void run() {

        try{
            for (int i = l; i<r && !stopThread; i++)
            {
                ArrayList<Character> characters = new ArrayList<>(letters);
                String tempWord = (allWords.get(i));
                if (tempWord.length() > 12)
                {
                    continue;
                }
                int numOfLetters = tempWord.length();
                int counterOfLetters = 0;
                for (int j = 0; j < numOfLetters; j++)
                {

                    if (characters.contains(tempWord.charAt(j)))
                    {
                        char characterForRemove = tempWord.charAt(j);
                        characters.remove((Character)characterForRemove);
                        counterOfLetters++;
                    }
                }
                if (counterOfLetters > longestWord.length() && counterOfLetters == tempWord.length())
                {
                    longestWord = allWords.get(i);
                }
            }
        }catch (Exception e)
        {
            Log.e("Error from thread",e.toString());
        }

    }
    public void start()
    {
        if (thread == null)
        {
            thread = new Thread(this);
            thread.start();
        }
    }
    public Thread getThread()
    {
        return thread;
    }
    public String getLongestWord()
    {
        return longestWord;
    }
}
