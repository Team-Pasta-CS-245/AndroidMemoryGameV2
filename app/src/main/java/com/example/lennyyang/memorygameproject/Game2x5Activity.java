package com.example.lennyyang.memorygameproject;

/**
 * Created by lennyyang on 12/2/17.
 */

import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import java.util.Random;


public class Game2x5Activity extends AppCompatActivity implements View.OnClickListener {

    private int correct = 0;
    private int score = 0;

    private int numberOfElements;

    private MemoryButton[] buttons;

    private int[] buttonGraphicLocations;
    private int[] buttonGraphics;

    private MemoryButton selectButton1;
    private MemoryButton selectButton2;

    private boolean isBusy = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game2x5);
        GridLayout gridLayout = findViewById(R.id.grid_layout_2x5);

        int numColumns = gridLayout.getColumnCount();
        int numRows = gridLayout.getRowCount();

        numberOfElements = numColumns * numRows;

        buttons = new MemoryButton[numberOfElements];

        buttonGraphics = new int[numberOfElements/2];

        buttonGraphics[0] = R.drawable.button_1;
        buttonGraphics[1] = R.drawable.button_2;
        buttonGraphics[2] = R.drawable.button_3;
        buttonGraphics[3] = R.drawable.button_4;
        buttonGraphics[4] = R.drawable.button_5;

        buttonGraphicLocations = new int[numberOfElements];

        shuffleButtonGraphics();

        for(int r = 0 ; r < numRows; r++) {
            for (int c = 0; c < numColumns; c++) {
                MemoryButton tempButton = new MemoryButton(this, r, c, buttonGraphics[buttonGraphicLocations[r * numColumns + c]]);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                    tempButton.setId(View.generateViewId());
                }
                tempButton.setOnClickListener(this);
                buttons[r * numColumns + c] = tempButton;
                gridLayout.addView(tempButton);
            }
        }

        final Button tryAgain = findViewById(R.id.try_again_button);
        tryAgain.setEnabled(false);
    }

    protected void shuffleButtonGraphics(){
        Random rand = new Random();

        for(int i = 0; i < numberOfElements; i++){

            buttonGraphicLocations[i] = i % (numberOfElements/2);

        }
        for(int i = 0; i < numberOfElements; i++){

            int temp = buttonGraphicLocations[i];

            int swapIndex = rand.nextInt(10);

            buttonGraphicLocations[i] = buttonGraphicLocations[swapIndex];

            buttonGraphicLocations[swapIndex] = temp;
        }

    }

    @Override
    public void onClick(View view) {

        final Button endGame = findViewById(R.id.end_game_button);
        final Button newGame = findViewById(R.id.new_game_button);
        final Button tryAgain = findViewById(R.id.try_again_button);

        newGame.setOnClickListener( new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = getIntent();
                finish();
                startActivity(intent);
            }
        });

        endGame.setOnClickListener( new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                for(int x = 0; x < buttons.length; x++){
                    isBusy = true;
                    if(buttons[x].isFlipped == false)
                        buttons[x].flip();
                }
                endGame.setEnabled(false);
                tryAgain.setEnabled(false);
            }
        });

        if(isBusy){
            return;
        }

        MemoryButton button = (MemoryButton) view;

        if(button.isMatched){
            tryAgain.setEnabled(false);
            return;
        }

        if(selectButton1 == null){

            selectButton1 = button;
            selectButton1.flip();
            return;
        }

        if (selectButton1.getId() == button.getId()){
            return;
        }

        if(selectButton1.getFrontDrawableId() == button.getFrontDrawableId()){

            button.flip();

            button.setMatched(true);
            selectButton1.setMatched(true);

            selectButton1.setEnabled(false);
            button.setEnabled(false);

            selectButton1 = null;

            score += 2;
            correct += 1;

            System.out.println(score + " " + correct);

            if(correct == 5){

                Intent mIntent = new Intent(this, ScoreActivity.class);
                Bundle mBundle = new Bundle();
                mBundle.putString("test", Integer.toString(score));
                mIntent.putExtras(mBundle);
                startActivity(mIntent);
            }

            return;
        }
        else{

            selectButton2 = button;
            selectButton2.flip();

            isBusy = true;

            if(score != 0){
                score -= 1;
            }

            System.out.println(score + " " + correct);
            tryAgain.setEnabled(true);
            tryAgain.setOnClickListener( new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    tryAgain.setEnabled(false);
                    selectButton2.flip();
                    selectButton1.flip();

                    selectButton1 = null;
                    selectButton2 = null;

                    isBusy = false;

                }
            });
        }
    }

}
