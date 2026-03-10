package org.example.card24_game;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import java.util.*;

public class HelloController {
    @FXML
    private ImageView card1, card2, card3, card4;

    @FXML
    private TextField expressionField;

    @FXML
    private TextField solutionField;


    private final int[] cardValues = new int[4];
    private final int[] cardIndices = new int[4];

    Random random = new Random();

    @FXML
    public void initialize(){
        generateCards();
    }

    private void generateCards(){
        for (int i = 0; i < 4; i++) {
            cardIndices[i] = random.nextInt(52) + 1;
            cardValues[i] = getCardValue(cardIndices[i]);
        }

        setImage(card1,cardIndices[0]);
        setImage(card2,cardIndices[1]);
        setImage(card3,cardIndices[2]);
        setImage(card4,cardIndices[3]);
    }

    private void setImage(ImageView imageView, int index){
        Image image = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/org/example/card24_game/png/" + index + ".png")));
        imageView.setImage(image);
    }

    private int getCardValue(int index1){
        int value = index1 % 13;
        if (value == 0){
            return 13;
        }
        return value;
    }

    @FXML
    private void handleRefresh(){
        generateCards();
        expressionField.clear();
        solutionField.clear();
    }

    @FXML
    private void handleVerify(){
        String expression = expressionField.getText();

        if(expression == null || expression.trim().isEmpty()){
            alert("Empty","Please enter an expression");
            return;
        }

        if(!checkNumber(expression)){
            alert("Invalid","Enter the same numbers as the card shown on screen");
            return;
        }

        double result = evaluate(expression);

        if (Math.abs(result - 24) < 0.0001) {
            alert("Correct!", "Expression evaluates to 24.");
        } else {
            alert("Incorrect", "Result is " + result + ", not 24.");
        }
    }

    private boolean checkNumber(String expression) {
        ArrayList<Integer> enteredNumbers = extractNumbers(expression);

        if (enteredNumbers.size() != 4) {
            return false;
        }

        ArrayList<Integer> displayedNumbers = new ArrayList<>();
        for (int value : cardValues) {
            displayedNumbers.add(value);
        }

        Collections.sort(enteredNumbers);
        Collections.sort(displayedNumbers);

        return enteredNumbers.equals(displayedNumbers);
    }

    private ArrayList<Integer> extractNumbers(String expression) {
        ArrayList<Integer> numbers = new ArrayList<>();
        StringBuilder currentNumber = new StringBuilder();

        for (int i = 0; i < expression.length(); i++) {
            char ch = expression.charAt(i);

            if (Character.isDigit(ch)) {
                currentNumber.append(ch);
            } else {
                if (!currentNumber.isEmpty()) {
                    numbers.add(Integer.parseInt(currentNumber.toString()));
                    currentNumber.setLength(0);
                }
            }
        }

        if (!currentNumber.isEmpty()) {
            numbers.add(Integer.parseInt(currentNumber.toString()));
        }

        return numbers;
    }

    private double evaluate(String expression) {
        return new Parser(expression).parse();
    }

    private static class Parser {
        private final String input;
        private int pos = -1;
        private int ch;

        Parser(String input) {
            this.input = input;
        }

        void nextChar() {
            pos++;
            ch = pos < input.length() ? input.charAt(pos) : -1;
        }

        boolean eat(int charToEat) {
            while (ch == ' ') {
                nextChar();
            }

            if (ch == charToEat) {
                nextChar();
                return true;
            }
            return false;
        }

        double parse() {
            nextChar();
            double x = parseExpression();

            if (pos < input.length()) {
                throw new RuntimeException("Unexpected character");
            }

            return x;
        }

        double parseExpression() {
            double x = parseTerm();

            while (true) {
                if (eat('+')) {
                    x += parseTerm();
                } else if (eat('-')) {
                    x -= parseTerm();
                } else {
                    return x;
                }
            }
        }

        double parseTerm() {
            double x = parseFactor();

            while (true) {
                if (eat('*')) {
                    x *= parseFactor();
                } else if (eat('/')) {
                    double divisor = parseFactor();
                    if (Math.abs(divisor) < 0.0000001) {
                        throw new ArithmeticException("Division by zero");
                    }
                    x /= divisor;
                } else {
                    return x;
                }
            }
        }

        double parseFactor() {
            if (eat('+')) {
                return parseFactor();
            }

            if (eat('-')) {
                return -parseFactor();
            }

            double x;
            int startPos = this.pos;

            if (eat('(')) {
                x = parseExpression();
                if (!eat(')')) {
                    throw new RuntimeException("Missing closing parenthesis");
                }
            } else if (ch >= '0' && ch <= '9') {
                while (ch >= '0' && ch <= '9') {
                    nextChar();
                }
                x = Double.parseDouble(input.substring(startPos, this.pos));
            } else {
                throw new RuntimeException("Unexpected character");
            }

            return x;
        }
    }

    private void alert(String title, String message){
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
