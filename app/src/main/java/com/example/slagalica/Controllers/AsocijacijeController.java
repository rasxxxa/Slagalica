package com.example.slagalica.Controllers;

import com.example.slagalica.HelperClasses.Association;
import com.example.slagalica.HelperClasses.ResourceHelper;

import java.util.ArrayList;
import java.util.Random;

public class AsocijacijeController {

    private static AsocijacijeController instance;

    private ArrayList<Association> associations;

    private Random random;

    private AsocijacijeController()
    {
        associations = ResourceHelper.getInstance(null).getAssociations();
        random = ResourceHelper.getInstance(null).getRandomInstance();
    }

    public static AsocijacijeController getInstance()
    {
        if (instance == null)
        {
            instance = new AsocijacijeController();
        }
        return instance;
    }

    public Association getAssociation(int index)
    {
        if (index < associations.size())
        {
            return associations.get(index);
        }else
        {
            return associations.get(0);
        }
    }

    public Integer getAssociationNumber()
    {
        return Math.abs(random.nextInt() % associations.size());
    }


    // Function for transform String word to Cyrilic version of that word
    // For example miljenica = миљеница
    private String transform(String word) {

        ArrayList<Character> ArrayList = new ArrayList<>();
        for (int i = 0; i < word.length(); i++) {

            switch (word.charAt(i)) {
                case 'a':
                    ArrayList.add('а');
                    break;
                case 'b':
                    ArrayList.add('б');
                    break;
                case 'v':
                    ArrayList.add('в');
                    break;
                case 'g':
                    ArrayList.add('г');
                    break;
                case 'd':
                    ArrayList.add('д');
                    break;
                case 'đ':
                    ArrayList.add('ђ');
                    break;
                case 'e':
                    ArrayList.add('е');
                    break;
                case 'ž':
                    if (ArrayList.size() > 0) {
                        if (ArrayList.get(ArrayList.size() - 1) == 'д') {
                            ArrayList.remove(ArrayList.size() - 1);
                            ArrayList.add('џ');
                        }

                    } else {
                        ArrayList.add('ж');
                    }
                    break;
                case 'z':
                    if (ArrayList.size() > 0) {
                        if (ArrayList.get(ArrayList.size() - 1) == 'д') {
                            ArrayList.remove(ArrayList.size() - 1);
                            ArrayList.add('џ');
                        }


                    } else {
                        ArrayList.add('з');
                    }
                    break;
                case 'i':
                    ArrayList.add('и');
                    break;
                /* lnd */
                case 'j':
                    if (ArrayList.size() > 0) {
                        if (ArrayList.get(ArrayList.size() - 1) == 'л') {
                            ArrayList.remove(ArrayList.size() - 1);
                            ArrayList.add('љ');
                        } else if (ArrayList.get(ArrayList.size() - 1) == 'н') {
                            ArrayList.remove(ArrayList.size() - 1);
                            ArrayList.add('њ');
                        } else if (ArrayList.get(ArrayList.size() - 1) == 'д') {
                            ArrayList.remove(ArrayList.size() - 1);
                            ArrayList.add('ђ');
                        } else {
                            ArrayList.add('ј');
                        }
                    } else {
                        ArrayList.add('ј');
                    }
                    break;
                case 'k':
                    ArrayList.add('к');
                    break;
                case 'l':
                    ArrayList.add('л');
                    break;
                case 'm':
                    ArrayList.add('м');
                    break;
                case 'n':
                    ArrayList.add('н');
                    break;
                case 'o':
                    ArrayList.add('о');
                    break;
                case 'p':
                    ArrayList.add('п');
                    break;
                case 'r':
                    ArrayList.add('р');
                    break;
                case 's':
                    ArrayList.add('с');
                    break;
                case 't':
                    ArrayList.add('т');
                    break;
                case 'ć':
                    ArrayList.add('ћ');
                    break;
                case 'u':
                    ArrayList.add('у');
                    break;
                case 'f':
                    ArrayList.add('ф');
                    break;
                case 'h':
                    ArrayList.add('х');
                    break;
                case 'c':
                    ArrayList.add('ц');
                    break;
                case 'č':
                    ArrayList.add('ч');
                    break;
                case 'š':
                    ArrayList.add('ш');
                    break;
                case ' ':
                    ArrayList.add(' ');
                    break;
            }
        }
        StringBuilder newWord = new StringBuilder();
        for (int i = 0; i < ArrayList.size(); i++) {

            newWord.append(Character.toUpperCase(ArrayList.get(i)));
        }


        return newWord.toString();
    }

    // Function for matching 2 words
    // This function can compare 2 strings with some exceptions for example
    // if user enter some space extra, or some extra character, this algorithm will filter it
    // or if user types mrsav instead of мршав this algorithm will check it
    public boolean MatchWords(String word1, String word2, boolean Cyrilic) {
        String transformedWord = word2;
        if (!Cyrilic)
            transformedWord = transform(word2);

        if (Math.abs(word1.length() - transformedWord.length()) > 1) {
            return false;
        }
        if (word1.length() == transformedWord.length() && word1.length() < 4) {
            int numberOfTries = 0;
            int tryNumber = Math.min(word1.length(), transformedWord.length()); // warning for some reason, words are not same...
            int longerWord = Math.max(word1.length(), transformedWord.length());

            for (int i = 0; i < tryNumber; i++) {
                if (word1.charAt(i) == transformedWord.charAt(i)) {
                    numberOfTries++;
                } else if (transformedWord.charAt(i) == 'З') {
                    if (word1.charAt(i) == 'Ж' || word1.charAt(i) == 'З') {
                        numberOfTries++;
                    }
                } else if (transformedWord.charAt(i) == 'С') {
                    if (word1.charAt(i) == 'С' || word1.charAt(i) == 'Ш') {
                        numberOfTries++;
                    }
                } else if (transformedWord.charAt(i) == 'Ц') {
                    if (word1.charAt(i) == 'Ч' || word1.charAt(i) == 'Ћ' || word1.charAt(i) == 'Ц') {
                        numberOfTries++;
                    }
                }
            }
            return numberOfTries == longerWord;
        } else if (word1.length() >= 4) {

            int numberOfTries1 = 0;
            int numberOfTries2 = 0;
            int pointer1 = 0, pointer2 = 0;
            int numberOfMisses1 = 0;
            int numberOfMisses2 = 0;
            while (pointer2 != transformedWord.length() && pointer1 != word1.length()) {
                if (transformedWord.charAt(pointer2) == word1.charAt(pointer1)) {
                    numberOfTries1++;
                    pointer2++;
                    pointer1++;
                } else if (transformedWord.charAt(pointer2) == 'З') {
                    if (word1.charAt(pointer1) == 'Ж' || word1.charAt(pointer1) == 'З') {
                        pointer1++;
                        pointer2++;
                        numberOfTries1++;
                    } else {
                        numberOfMisses1++;
                        pointer1++;
                    }
                } else if (transformedWord.charAt(pointer2) == 'С') {
                    if (word1.charAt(pointer1) == 'С' || word1.charAt(pointer1) == 'Ш') {
                        pointer1++;
                        pointer2++;
                        numberOfTries1++;
                    } else {
                        numberOfMisses1++;
                        pointer1++;
                    }
                } else if (transformedWord.charAt(pointer2) == 'Ц') {
                    if (word1.charAt(pointer1) == 'Ч' || word1.charAt(pointer1) == 'Ћ' || word1.charAt(pointer1) == 'Ц') {
                        pointer1++;
                        pointer2++;
                        numberOfTries1++;
                    } else {
                        numberOfMisses1++;
                        pointer1++;
                    }
                } else {
                    numberOfMisses1++;
                    pointer1++;
                }
            }
            pointer1 = 0;
            pointer2 = 0;
            while (pointer1 != word1.length() && pointer2 != transformedWord.length()) {
                if (transformedWord.charAt(pointer2) == word1.charAt(pointer1)) {
                    numberOfTries2++;
                    pointer1++;
                    pointer2++;
                } else if (transformedWord.charAt(pointer2) == 'З') {
                    if (word1.charAt(pointer1) == 'Ж' || word1.charAt(pointer1) == 'З') {
                        pointer1++;
                        pointer2++;
                        numberOfTries2++;
                    } else {
                        numberOfMisses2++;
                        pointer2++;
                    }
                } else if (transformedWord.charAt(pointer2) == 'С') {
                    if (word1.charAt(pointer1) == 'С' || word1.charAt(pointer1) == 'Ш') {
                        pointer1++;
                        pointer2++;
                        numberOfTries2++;
                    } else {
                        numberOfMisses2++;
                        pointer2++;
                    }
                } else if (transformedWord.charAt(pointer2) == 'Ц') {
                    if (word1.charAt(pointer1) == 'Ч' || word1.charAt(pointer1) == 'Ћ' || word1.charAt(pointer1) == 'Ц') {
                        pointer1++;
                        pointer2++;
                        numberOfTries2++;
                    } else {
                        numberOfMisses2++;
                        pointer2++;
                    }
                } else {
                    numberOfMisses2++;
                    pointer2++;
                }
            }
            return (Math.abs(word1.length() - numberOfTries1) <= 1 && numberOfMisses1 <= 1) || (Math.abs(word1.length() - numberOfTries2)) <= 1 && numberOfMisses2 <= 1;
        }
        return false;
    }


}
