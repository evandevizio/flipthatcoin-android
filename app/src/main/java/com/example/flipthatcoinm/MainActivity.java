package com.example.flipthatcoinm;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import java.util.Random;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    int headsCount = 0;
    int tailsCount = 0;
    int headsRoundCount = 0;
    int tailsRoundCount = 0;
    boolean bestMode = false;
    boolean game_over = false;

    ImageView coinImage;

    EditText statusText;
    EditText resultText;
    EditText headsRoundText;
    EditText tailsRoundText;

    CheckBox headsCheck1;
    CheckBox headsCheck2;
    CheckBox tailsCheck1;
    CheckBox tailsCheck2;

    Button singleButton;
    Button bestButton;
    Button resetButton;
    Button flipButton;

    ImageButton infoButton;

    MediaPlayer mp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        statusText = findViewById(R.id.statusText);
        resultText = findViewById(R.id.resultText);
        headsRoundText = findViewById(R.id.headsRoundEditText);
        tailsRoundText = findViewById(R.id.tailsRoundEditText);

        headsCheck1 = findViewById(R.id.headsCheck1);
        headsCheck2 = findViewById(R.id.headsCheck2);
        tailsCheck1 = findViewById(R.id.tailsCheck1);
        tailsCheck2 = findViewById(R.id.tailsCheck2);

        singleButton = findViewById(R.id.singleButton);
        bestButton = findViewById(R.id.bestButton);
        resetButton = findViewById(R.id.resetButton);
        flipButton = findViewById(R.id.flipButton);
        infoButton = findViewById(R.id.infoImageButton);
        coinImage = findViewById(R.id.imageView);

        singleButton.setOnClickListener(this);
        bestButton.setOnClickListener(this);
        resetButton.setOnClickListener(this);
        flipButton.setOnClickListener(this);
        infoButton.setOnClickListener(this);

        mp = MediaPlayer.create(this, R.raw.coinflip);

        firstRun();
    }

        @Override
        public void onClick (View v){
            switch (v.getId()) {
                case R.id.resetButton:
                    resetClick();
                    break;
                case R.id.singleButton:
                    singleClick();
                    break;
                case R.id.bestButton:
                    bestClick();
                    break;
                case R.id.flipButton:
                    flipClick();
                    break;
                case R.id.infoImageButton:
                    infoClick();
                default:
                    break; 
            }
        }

    private void infoClick() {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
        builder1.setTitle("FlipThatCoin v1.0");
        builder1.setMessage("Created by Evan DeVizio" + "\n" + "\n" +
                "Would you like to visit my website?");
        builder1.setCancelable(true);

        builder1.setPositiveButton(
                "Yes",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW,
                                Uri.parse("http://www.evandevizio.com"));
                        startActivity(browserIntent);
                    }
                });

        builder1.setNegativeButton(
                "No",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        AlertDialog alert11 = builder1.create();
        alert11.show();
    }

    private void firstRun() {
        headsCount = 0;
        tailsCount = 0;
        headsRoundCount = 0;
        tailsRoundCount = 0;
        bestMode = false;

        clearChecks();
        flipButton.setEnabled(false);
        flipButton.setClickable(false);
        singleButton.setEnabled(true);
        singleButton.setClickable(true);
        bestButton.setEnabled(true);
        bestButton.setClickable(true);
        statusText.setText("Please select a mode.");
        headsRoundText.setText(String.valueOf(headsRoundCount));
        tailsRoundText.setText(String.valueOf(tailsRoundCount));
    }

    private void clearChecks() {
        headsCheck1.setChecked(false);
        headsCheck2.setChecked(false);
        tailsCheck1.setChecked(false);
        tailsCheck2.setChecked(false);
    }

    private void singleClick() {
        firstRun();

        singleButton.setBackgroundColor(Color.parseColor("#228C22")); //green
        bestButton.setBackgroundColor(Color.parseColor("#a9a9a9")); //grey
        statusText.setText("");
        resultText.setText("");
        coinImage.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.unknown));
        game_over = false;
        readyToFlip();
    }

    private void bestClick() {
        firstRun();
        bestMode = true;

        bestButton.setBackgroundColor(Color.parseColor("#228C22")); //green
        singleButton.setBackgroundColor(Color.parseColor("#a9a9a9")); //grey
        statusText.setText("");
        resultText.setText("");
        coinImage.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.unknown));
        game_over = false;
        readyToFlip();
    }

    private void resetClick() {
        firstRun();
        statusText.setText("");
        resultText.setText("");
        coinImage.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.unknown));
    }

    private void flipClick() {
        mp.start(); // play coinflip.mp3

        int coin = coinFlip(); // get random int between 0 and 1

        try { // pause for 1 second:
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        if (coin == 0) {
            coinImage.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.heads));
            resultText.setText("It's heads!");
            if (bestMode) {
                landedHeads();
            }
        }
        else {
            coinImage.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.tails));
            resultText.setText("It's tails!");
            if (bestMode) {
                landedTails();
            }
        }

        if (bestMode && (headsCount >= 2 || tailsCount >= 2)) {
            // match win:
            matchWinner();
        }

    readyToFlip();
    }

    private void readyToFlip() {
        if (!game_over) {
            flipButton.setEnabled(true);
            flipButton.setClickable(true);
            resetButton.setEnabled(true);
            statusText.setText("READY");
            if(bestMode){
                headsRoundText.setText(String.valueOf(headsRoundCount));
                tailsRoundText.setText(String.valueOf(tailsRoundCount));
            }
        }
    }

    private int coinFlip() {
        flipButton.setEnabled(false);
        flipButton.setClickable(false);

        return randomCoin();
    }

    private static int randomCoin() {
        // 0 or 1
        Random r = new Random();
        return r.nextInt(2);
    }

    private void landedHeads() {
        if (headsCount == 0) {
            headsCheck1.setChecked(true);
        }
        else {
            if (headsCount == 1) {
                headsCheck2.setChecked(true);
            }
        }
        headsCount++;
    }

    private void landedTails() {
        if (tailsCount == 0) {
            tailsCheck1.setChecked(true);
        }
        else {
            if (tailsCount == 1) {
                tailsCheck2.setChecked(true);
            }
        }
        tailsCount++;
    }

    private void matchWinner() {
        if (tailsCount > headsCount) {
            tailsRoundCount++;
            tailsRoundText.setText(String.valueOf(tailsRoundCount));
        }
        if (headsCount > tailsCount){
            headsRoundCount++;
            headsRoundText.setText(String.valueOf(headsRoundCount));
        }

        headsCount = 0;
        tailsCount = 0;
        clearChecks();

        if (bestMode && (headsRoundCount >= 2 || tailsRoundCount >= 2)
                && (headsRoundCount != tailsRoundCount)) {
            //game win:
            winner();
            gameOver();
        }
    }

    private void winner() {
        if (headsRoundCount > tailsRoundCount) {
            resultText.setText("");
            resultText.setText("Heads wins!");
        }
        else {
            resultText.setText("");
            resultText.setText("Tails wins!");
        }
    }

    private void gameOver() {
        game_over = true;
        statusText.setText("Game Over");
        flipButton.setEnabled(false);
        flipButton.setClickable(false);
        singleButton.setEnabled(false);
        singleButton.setClickable(false);
        singleButton.setBackgroundColor(Color.parseColor("#a9a9a9")); //grey;
        bestButton.setEnabled(false);
        bestButton.setClickable(false);
        bestButton.setBackgroundColor(Color.parseColor("#a9a9a9")); //grey
    }
}