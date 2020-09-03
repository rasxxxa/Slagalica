package com.example.slagalica.Controllers;

import com.example.slagalica.HelperClasses.ResourceHelper;
import com.example.slagalica.R;

import java.util.ArrayList;
import java.util.Random;

public class SkockoController {

    private static SkockoController instance;

    private final int numberOfColumns = 4;
    private final int numberOfRows = 6;
    private final int maxValueInCombination = 6;

    private final int[] ResourceImageId = {R.drawable.fire, R.drawable.heart, R.drawable.leaf, R.drawable.plant, R.drawable.square, R.drawable.star};

    private Random random;

    private SkockoController()
    {
        random = ResourceHelper.getInstance(null).getRandomInstance();
    }

    public static SkockoController getInstance()
    {
        if (instance == null)
        {
            instance = new SkockoController();
        }
        return  instance;
    }

    // Creating random combination for game
    public ArrayList<Integer> getRandomCombination() {
        ArrayList<Integer> combination = new ArrayList<>();
        for (int i = 0; i < numberOfColumns; i++) {
            combination.add(Math.abs(random.nextInt() % maxValueInCombination));
        }
        return combination;
    }

    // Checking if user combination is matching with desired combination, hits are number of elements placed on right place
    // almosthits are elements placed on wrong place
    public int[] checkUserCombination(int[] userComb, ArrayList<Integer> combination) {
        int hits = 0;
        int almostHits = 0;
        int [] returnResult = new int[2];
        int[] copyOfUserComb = new int[numberOfColumns];
        int[] copyOfMainComb = new int[numberOfColumns];
        for (int i = 0; i < numberOfColumns; i++) {
            copyOfUserComb[i] = userComb[i];
            copyOfMainComb[i] = combination.get(i);
        }
        for (int i = 0; i < numberOfColumns; i++) {
            if (copyOfUserComb[i] == copyOfMainComb[i]) {
                hits++;
                copyOfUserComb[i] = -1;
                copyOfMainComb[i] = -2;
            }
        }
        for (int i = 0; i < numberOfColumns; i++) {
            for (int j = 0; j < numberOfColumns; j++) {
                if (copyOfUserComb[i] == copyOfMainComb[j] && i != j) {
                    almostHits++;
                    copyOfMainComb[j] = -2;
                    copyOfUserComb[i] = -1;
                }
            }
        }
        returnResult[0] = hits;
        returnResult[1] = almostHits;
        return returnResult;
    }

    public int getNumberOfColumns() {
        return numberOfColumns;
    }

    public int getNumberOfRows() {
        return numberOfRows;
    }

    public int[] getResourceImageId() {
        return ResourceImageId;
    }
}
