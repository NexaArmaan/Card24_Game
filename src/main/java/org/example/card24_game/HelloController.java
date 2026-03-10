package org.example.card24_game;

import javafx.fxml.FXML;
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

        setImage(card1,cardValues[0]);
        setImage(card1,cardValues[0]);
        setImage(card1,cardValues[0]);
        setImage(card1,cardValues[0]);
    }

    private void setImage(ImageView imageView, int index){
        Image image = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/org/example/card24_game/png/" + index + ".png")));
        imageView.setImage(image);
    }

    private int getCardValue(int index){
        int value = index % 13;
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


}
