package com.example.slagalica.Controllers;

import android.util.Log;

import com.example.slagalica.HelperClasses.ResourceHelper;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Random;
import java.util.Stack;

public class MojBrojController {

    private static MojBrojController instance;

    private Random random;

    private final int firstColumnCount = 4;

    private final int firstColumnMod = 9;
    private final int secondColumnMod = 3;
    private final int thirdColumnMod = 4;

    private final int firstColumnMultiplier = 1;
    private final int secondColumnMultiplier = 5;
    private final int thirdColumnMultiplier = 25;

    // For algorithm for finding correct (or nearest) solution for main number
    private int desiredSolution = 0;
    private volatile boolean threadBlock = false;
    private String operations[] = {"+", "-", "*", "/"};
    static ArrayList<String> numbersForCombination = new ArrayList<>();
    private Thread threadForCalculations;
    static int numberOfUsedOperations = 0;
    static int numberOfUsedNumbers = 0;
    private Stack<String> localCombination = new Stack<String>();
    private boolean solutionFound = false;
    private int numberOfCombinations = 0;
    private ArrayList<String> finalCombination = new ArrayList<>();
    private int distance = Integer.MAX_VALUE;
    private int closestElementDistance = Integer.MAX_VALUE;


    private MojBrojController() {
        random = ResourceHelper.getInstance(null).getRandomInstance();
    }

    public static MojBrojController getInstance() {
        if (instance == null) {
            instance = new MojBrojController();
        }
        return instance;
    }

    private int getRandom1000() {
        return Math.abs(random.nextInt() % 1000) + 1;
    }

    // Create numbers for finding a solution (first 4 to 10, next is to 20, and last one is to 100)
    public ArrayList<Integer> getNumbers() {

        ArrayList<Integer> numbers = new ArrayList<>();

        // adding main number
        numbers.add(getRandom1000());

        // four numbers 1-9
        for (int i = 0; i < firstColumnCount; i++) {
            numbers.add((Math.abs(random.nextInt() % firstColumnCount) + 1) * firstColumnMultiplier);
        }
        // one number {5,10,15,20}
        Integer numberMid = (Math.abs(random.nextInt()) % secondColumnMod + 2) * secondColumnMultiplier;
        numbers.add(numberMid);

        // one number {25, 50, 75, 100}
        Integer numberRight = (Math.abs(random.nextInt()) % thirdColumnMod + 1) * thirdColumnMultiplier;

        numbers.add(numberRight);
        return numbers;

    }

    // Creating thread for finding main solution by computer
    public void startComputerCalculation(int mainNumber, ArrayList<String> numbers) {
        desiredSolution = mainNumber;
        numbersForCombination = numbers;
        threadBlock = false;
        numberOfUsedOperations = 0;
        numberOfUsedNumbers = 0;
        localCombination = new Stack<>();
        solutionFound = false;
        numberOfCombinations = 0;
        finalCombination = new ArrayList<>();
        distance = Integer.MAX_VALUE;
        closestElementDistance = Integer.MAX_VALUE;
        threadForCalculations = new Thread(new Runnable() {
            @Override
            public void run() {
                findNumberForMojBroj();
            }
        });
        threadForCalculations.start();
    }

    // finishing thread and get result of calculation as string
    public String getResultOfCalculation() {
        if (threadForCalculations.isAlive()) {
            threadBlock = true;
            try {
                threadForCalculations.join();
            } catch (Exception ignored) {

            }

        }
        for (int i = 0; i < finalCombination.size(); i++) {
            Log.i("From calculation", finalCombination.get(i));
        }
        LinkedList<String> result = reversePolishNotation(finalCombination);
        StringBuilder resultEvaluation = new StringBuilder();
        for (String element : result) {
            resultEvaluation.append(element);
        }

        return resultEvaluation.toString();

    }

    // nearest result found by computer
    public int nearestResult() {
        return closestElementDistance;
    }

    // Running backtrack algorithm for calculating best result
    private void findNumberForMojBroj() {
        if (threadBlock) {
            return;
        }
        // Checking some conditions for evaluating backtracked result
        // number of numbers - numbers of used operation must be 1 or polish notation expression wont be valid
        // in every expression there needs to be at least 1 operation
        // and for polish notation expression there must be at least 3 characters (2 numbers and 1 operation)
        if (numberOfUsedNumbers - numberOfUsedOperations == 1 && numberOfUsedOperations != 0 && localCombination.size() > 2) {
            // Statistics use only
            numberOfCombinations++;
            double rez = resultOfEvaluationWithoutPolish(localCombination);
            if (rez < 0) {
                return;
            }
            int temprez = (int) rez;
            // Checking if generated expression is whole number ex. 2.5 when it casted to int it is 2 but that is not valid result
            if (rez - temprez == 0.0 && temprez == desiredSolution) {
                finalCombination = new ArrayList<>();
                for (int i = localCombination.size() - 1; i >= 0; i--) {
                    finalCombination.add(localCombination.get(i));
                }
                closestElementDistance = temprez;
                solutionFound = true;
                threadBlock = true;
                return;
            } else if (rez - temprez == 0.0) // If we could not find correct solution, we go for closest
            {
                if (Math.abs(temprez - desiredSolution) < distance) {
                    distance = Math.abs(temprez - desiredSolution);
                    closestElementDistance = temprez;
                    //Log.i("Closest ", String.valueOf(closestElementDistance));
                    finalCombination = new ArrayList<>();
                    for (int i = localCombination.size() - 1; i >= 0; i--) {
                        finalCombination.add(String.valueOf(localCombination.get(i)));
                      //  Log.i("Elements", String.valueOf(localCombination.get(i)));
                    }
                    //Log.i("Result closest", String.valueOf(resultOfEvaluationWithoutPolish(localCombination)));


                }
            }

        }
        // Max number of character in expression is 11
        if (localCombination.size() == 11) {
            return;
        } else if (solutionFound) {
            return;
        } else {
            // on first 2 places in expression there needs to be 2 numbers, if operation is first, expression is not valid
            if (localCombination.size() < 2 || numberOfUsedNumbers - numberOfUsedOperations <= 1) {
                for (int i = 0; i < numbersForCombination.size() && !solutionFound; i++) {
                    localCombination.add(numbersForCombination.get(0));
                    String temp = numbersForCombination.get(0);
                    numbersForCombination.remove(0);
                    numberOfUsedNumbers++;
                    findNumberForMojBroj();
                    numberOfUsedNumbers--;
                    localCombination.pop();
                    numbersForCombination.add(temp);

                }
                // When we use all numbers, we add only operation
            } else if (numbersForCombination.size() == 0) {
                for (int i = 0; i < 4 && !solutionFound && !threadBlock; i++) {
                    localCombination.add(operations[i]);
                    numberOfUsedOperations++;
                    findNumberForMojBroj();
                    numberOfUsedOperations--;
                    localCombination.pop();
                }
            } else // In other cases we can add and numbers and operations
            {

                for (int i = 0; i < 4 && !solutionFound && !threadBlock; i++) {
                    localCombination.add(operations[i]);
                    numberOfUsedOperations++;
                    findNumberForMojBroj();
                    numberOfUsedOperations--;
                    localCombination.pop();
                }
                for (int i = 0; i < numbersForCombination.size() && !solutionFound && !threadBlock; i++) {
                    localCombination.add(numbersForCombination.get(0));
                    String temp = numbersForCombination.get(0);
                    numbersForCombination.remove(0);
                    numberOfUsedNumbers++;
                    findNumberForMojBroj();
                    numberOfUsedNumbers--;
                    localCombination.pop();
                    numbersForCombination.add(temp);
                }
            }
        }
    }

    // Create readable expression from polishNotation
    private LinkedList<String> reversePolishNotation(ArrayList<String> expression) {

        LinkedList<String> linkedList = new LinkedList<String>();
        for (int i = expression.size() - 1; i >= 0; i--) {
            if (expression.get(i).equals("+") || expression.get(i).equals("-")) {
                String temp1 = linkedList.getLast();
                linkedList.removeLast();
                String temp2 = linkedList.getLast();
                linkedList.removeLast();
                String novi = "";
                if (expression.get(i).equals("-")) {
                    novi = temp2 + expression.get(i) + temp1;
                } else {
                    novi = temp1 + expression.get(i) + temp2;
                }

                linkedList.addLast(novi);
            } else if (expression.get(i).equals("*") || expression.get(i).equals("/")) {
                String temp1 = linkedList.getLast();
                linkedList.removeLast();

                String temp2 = linkedList.getLast();
                linkedList.removeLast();
                if (expression.get(i).equals("/")) {
                    String tempCheck = temp1;
                    temp1 = temp2;
                    temp2 = tempCheck;
                }
                String refractored = "";
                if (temp2.length() < 3 && temp1.length() >= 3) {
                    if (!temp1.equals("100")) {
                        refractored = "(" + temp1 + ")" + expression.get(i) + temp2;
                    } else {
                        refractored = temp1 + expression.get(i) + temp2;
                    }

                } else if (temp2.length() < 3 && temp1.length() < 3) {
                    refractored = temp1 + expression.get(i) + temp2;
                } else if (temp2.length() >= 3 && temp1.length() < 3) {
                    if (!temp2.equals("100")) {
                        refractored = temp1 + expression.get(i) + "(" + temp2 + ")";
                    } else {
                        refractored = temp1 + expression.get(i) + temp2;
                    }
                } else {
                    if (!temp1.equals("100") && !temp2.equals("100")) {
                        refractored = "(" + temp1 + ")" + expression.get(i) + "(" + temp2 + ")";
                    } else if (temp1.equals("100") && !temp2.equals("100")) {
                        refractored = temp1 + expression.get(i) + "(" + temp2 + ")";
                    } else if (temp2.equals("100") && !temp1.equals("100")) {
                        refractored = "(" + temp1 + ")" + expression.get(i) + temp2;
                    } else {
                        // Both numbers cant be 100
                    }
                }

                linkedList.addLast(refractored);

            } else {
                linkedList.addLast(expression.get(i));
            }
        }
        return linkedList;
    }

    // double value that is returned is result of evaluating some expression
    public Integer resultOfEvaluation(Stack<String> expressionToEvaluate) {
        Integer finalResult = 0;
        try {
            Stack<String> transformedExpression = polishNotation(expressionToEvaluate);
            Stack<Double> result = new Stack<>();
            for (int i = 0; i < transformedExpression.size(); i++) {
                String expression = transformedExpression.get(i);
                if (expression.equals("*") || expression.equals("/") || expression.equals("+") || expression.equals("-")) {
                    double number1 = result.pop();
                    double number2 = result.pop();
                    if (expression.equals("*")) {
                        result.push(number1 * number2);
                    }
                    if (expression.equals("/")) {
                        result.push(number2 / number1);
                    }
                    if (expression.equals("-")) {
                        result.push(number2 - number1);
                    }
                    if (expression.equals("+")) {
                        result.push(number1 + number2);
                    }
                } else {
                    double number = Double.valueOf(transformedExpression.get(i));
                    result.push(number);
                }
            }

            Double resultReturn = result.peek();
            if (Math.ceil(resultReturn) - Math.floor(resultReturn) > 0) {
                finalResult = 0;
            } else {
                finalResult = resultReturn.intValue();
            }
        } catch (Exception e) {
            finalResult = 0;
        }

        return finalResult;
    }

    // Get result of some expression without transfer that expression to polish notation
    public double resultOfEvaluationWithoutPolish(Stack<String> expressionToEvaluate) {
        Stack<String> transformedExpression = expressionToEvaluate;
        Stack<Double> result = new Stack<>();
        for (int i = 0; i < transformedExpression.size(); i++) {
            String expression = transformedExpression.get(i);
            if (expression.equals("*") || expression.equals("/") || expression.equals("+") || expression.equals("-")) {
                double number1 = result.pop();
                double number2 = result.pop();
                if (expression.equals("*")) {
                    result.push(number1 * number2);
                }
                if (expression.equals("/")) {
                    result.push(number2 / number1);
                }
                if (expression.equals("-")) {
                    result.push(number2 - number1);
                }
                if (expression.equals("+")) {
                    result.push(number1 + number2);
                }
            } else {
                double number = Double.valueOf(transformedExpression.get(i));
                result.push(number);
            }
        }

        return result.peek();
    }

    // Polish notation is method for creating simple expression of some mathematical expression by removing parenthesis
    private Stack<String> polishNotation(Stack<String> expression) {
        Stack<String> operators = new Stack<>();
        Stack<String> ArrayList = new Stack<>();
        for (int i = 0; i < expression.size(); i++) {
            if (expression.get(i).equals("+") || expression.get(i).equals("-") || expression.get(i).equals("*") || expression.get(i).equals("/")) {
                if (expression.get(i).equals("+") || expression.get(i).equals("-")) {
                    if (!(operators.size() == 0)) {
                        String topOperator = operators.peek();
                        while (!topOperator.equals("(") && !(operators.size() == 0)) {
                            ArrayList.push(operators.peek());
                            operators.pop();
                            if (operators.size() == 0) {
                                topOperator = "dummyString";
                            } else {
                                topOperator = operators.peek();
                            }
                        }
                        operators.push(expression.get(i));
                    } else {
                        operators.push(expression.get(i));
                    }
                } else if (expression.get(i).equals("*") || expression.get(i).equals("/")) {
                    if (!(operators.size() == 0)) {
                        String topOperator = operators.peek();
                        while ((topOperator.equals("*") || topOperator.equals("/")) && !(operators.size() == 0)) {
                            ArrayList.push(operators.peek());
                            operators.pop();
                            if (operators.size() == 0) {
                                topOperator = "dummyString";
                            } else {
                                topOperator = operators.peek();
                            }
                        }

                    }
                    operators.push(expression.get(i));
                }

            } else if (expression.get(i).equals("(")) {
                operators.push(expression.get(i));
            } else if (expression.get(i).equals(")")) {
                if (!(operators.size() == 0)) {
                    String topOperator = operators.peek();
                    while (!topOperator.equals("(") && !(operators.size() == 0)) {
                        ArrayList.push(operators.peek());
                        operators.pop();
                        if (!(operators.size() == 0)) {
                            topOperator = operators.peek();
                        }
                    }
                    if (!(operators.size() == 0)) {
                        operators.pop();
                    }
                }
            } else {
                ArrayList.push(expression.get(i));
            }
        }
        while (!(operators.size() == 0)) {
            ArrayList.push(operators.peek());
            operators.pop();
        }


        return ArrayList;

    }

}
