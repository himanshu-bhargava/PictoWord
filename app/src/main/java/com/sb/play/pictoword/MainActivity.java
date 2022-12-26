package com.sb.play.pictoword;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.vit.assignment.pictoword.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    private ArrayList<String> clickedButtonTags = new ArrayList<>();
    private String userAnswer;
    private SharedPreferences sharedPreferences;
    private boolean[] position;
    private int currentRound;
    private AdView adView;
    private InterstitialAd interstitialAd;
    private ImageView firstImage;
    private ImageView secondImage;
    private Button currentButton;
    private ImageButton deleteButton;
    private ImageButton nextButton;
    private LinearLayout buttonsGrid;
    private TextView userAnswerView;
    private TextView roundCountShow;
    public List<RoundDetail> availableRounds;
    private static final String CURRENT_ROUND = "Round";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        loadFullScreenAd();
        getSupportActionBar().hide();
        readJsonData();
        MobileAds.initialize(this);
        adView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);
        sharedPreferences = this.getSharedPreferences("com.vit.assignment.com.sb.play.pictoword", Context.MODE_PRIVATE);
        firstImage = findViewById(R.id.imageView1);
        secondImage = findViewById(R.id.imageView2);
        userAnswerView = findViewById(R.id.textViewAnswer);
        buttonsGrid = findViewById(R.id.buttonGrid);
        nextButton = findViewById(R.id.next);
        roundCountShow = findViewById(R.id.roundCounter);
        deleteButton = findViewById(R.id.delete);
        initiateRound();
    }

    private void renewFullScreenAd() {
        interstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
            @Override
            public void onAdDismissedFullScreenContent() {
                // Called when fullscreen content is dismissed.
                Log.d("TAG", "The ad was dismissed.");
            }

            @Override
            public void onAdFailedToShowFullScreenContent(AdError adError) {
                // Called when fullscreen content failed to show.
                Log.d("TAG", "The ad failed to show.");
            }

            @Override
            public void onAdShowedFullScreenContent() {
                // Called when fullscreen content is shown.
                // Make sure to set your reference to null so you don't
                // show it a second time.
                interstitialAd = null;
                Log.d("TAG", "The ad was shown.");
            }
        });
        loadFullScreenAd();
    }

    private void readJsonData() {
        try {
            availableRounds = Arrays.asList(new ObjectMapper().readValue(
                    this.getAssets().open("roundDetails.json"), RoundDetail[].class));
        } catch (Exception e) {
            Log.e("Reading error", e.toString());
        }
    }

    private void enableNextRound() {
        sharedPreferences.edit().putInt(CURRENT_ROUND, ++currentRound).apply();
        nextButton.setVisibility(View.VISIBLE);
    }

    public void onClickGoToNext(View view) {
        if (currentRound % 2 == 0) {
            if (this.interstitialAd != null) {
                Log.i("onClickGoToNext", "onClickGoToNext: loading ad");
                this.interstitialAd.show(this);
                renewFullScreenAd();
            } else {
                Log.i("onClickGoToNext", "onClickGoToNext: Add was not ready");
            }
        }
        nextButton.setVisibility(View.GONE);
        initiateRound();
    }

    private void loadFullScreenAd() {
        AdRequest adRequest = new AdRequest.Builder().build();
        InterstitialAd.load(this, "ca-app-pub-7566740815223579/9598210695", adRequest, new InterstitialAdLoadCallback() {
            @Override
            public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                // The mInterstitialAd reference will be null until
                // an ad is loaded.
                MainActivity.this.interstitialAd = interstitialAd;
                Log.i("", "onAdLoaded");
            }

            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                // Handle the error
                Log.i("failed to load add", loadAdError.getMessage());
                MainActivity.this.interstitialAd = null;
            }
        });
    }

    private void popBasicAlertDialog(String message) {
        new AlertDialog.Builder(this)
                .setMessage(message)
                .create()
                .show();
    }

    private void popFinalAlertDialog() {
        new AlertDialog.Builder(this)
                .setMessage("Congratulations! You have completed all the rounds!!!\nGo to play store and update your app for more rounds.")
                .setCancelable(false)
                .create()
                .show();
    }

    private void initiateRound() {
        clearUserAnswerView();
        deleteButton.setVisibility(View.VISIBLE);
        currentRound = sharedPreferences.getInt(CURRENT_ROUND, 0);
        Log.i("currentRound :", String.valueOf(currentRound));
        if (isGameCompleted()) {
            popFinalAlertDialog();
            return;
        }
        roundCountShow.setText("Round: " + (currentRound + 1) + "/" + availableRounds.size());
        firstImage.setImageResource(getResourceIdForImage(availableRounds.get(currentRound).getFirstImage()));
        secondImage.setImageResource(getResourceIdForImage(availableRounds.get(currentRound).getSecondImage()));
        setTextToButtons();
        setAnswerAlphabetsToButtons(availableRounds.get(currentRound).getAnswer());

    }

    private void clearUserAnswerView() {
        clickedButtonTags.clear();
        userAnswerView.setText("");
        userAnswer = "";
    }

    public int getResourceIdForImage(String name) {
        return this.getResources().getIdentifier(name, "drawable", this.getPackageName());
    }

    public void setTextToButtons() {
        Log.i("Updating button", "random");
        for (int i = 0; i < 14; i++) {
            int rand = getRandom(65, 90);
            currentButton = buttonsGrid.findViewWithTag(i + "");
            currentButton.setText(String.valueOf((char) rand));
            updateButtons(currentButton, true);
        }
    }

    public void setAnswerAlphabetsToButtons(String ans) {
        Log.i("String:", ans);
        position = new boolean[14];
        for (int i = 0; i < ans.length(); i++) {
            String random = generateUnusedRandomNumber();
            currentButton = buttonsGrid.findViewWithTag(random);
            currentButton.setText(String.valueOf(ans.charAt(i)));
        }
    }

    private String generateUnusedRandomNumber() {
        int number = getRandom(0, 13);
        while (position[number]) {
            number = (number + 1) % 14;
        }
        position[number] = true;
        return String.valueOf(number);
    }

    public void onButtonClick(View view) {
        if (currentRound >= availableRounds.size()) {
            return;
        }
        Button button = (Button) view;
        updateButtons(button, false);
        clickedButtonTags.add((String) button.getTag());
        String selectedChar = button.getText().toString().toUpperCase();
        userAnswer = userAnswerView.getText().toString().concat(selectedChar);
        userAnswerView.setText(userAnswer);
        if (userAnswer.length() == availableRounds.get(currentRound).getAnswer().length()) {
            checkAnswer(userAnswer);
        }
    }

    private void updateButtons(Button button, boolean isClickable) {
        button.setClickable(isClickable);
        button.setBackgroundColor(isClickable ? getResources().getColor(R.color.buttonBaseColor) :
                getResources().getColor(R.color.buttonClickColor));
        button.setTextColor(isClickable ? Color.WHITE : Color.BLACK);
    }

    public void checkAnswer(String userAnswer) {
        String actualAnswer = availableRounds.get(currentRound).getAnswer();
        Log.i("Actual answer:", actualAnswer);
        Log.i("User answered: ", userAnswer);
        if (userAnswer.equalsIgnoreCase(actualAnswer)) {
            enableNextRound();
            if (!isGameCompleted()) {
                popBasicAlertDialog(actualAnswer.toUpperCase() + " is the Right answer!!!");
            } else {
                popFinalAlertDialog();
            }
            deleteButton.setVisibility(View.INVISIBLE);
        } else {
            popBasicAlertDialog("Wrong answer!!!");
            initiateRound();
        }
    }

    public void restartGame(View view) {
        new AlertDialog.Builder(this).setTitle("Are you sure?")
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setMessage("Note: All progress will be lost if you select YES")
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        return;
                    }
                }).setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Log.i("Round: ", String.valueOf(currentRound));
                sharedPreferences.edit().putInt("Round", 0).apply();
                Log.i("Round: ", String.valueOf(currentRound));
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        initiateRound();
                    }
                });
            }
        })
                .setCancelable(false)
                .create()
                .show();
    }

    public void deleteWrongAlphabets(View view) {
        if (clickedButtonTags.isEmpty()) {
            return;
        }
        userAnswer = userAnswer.substring(0, userAnswer.length() - 1);
        userAnswerView.setText(userAnswer);
        Button button = (Button) buttonsGrid.findViewWithTag(clickedButtonTags.get(clickedButtonTags.size() - 1));
        updateButtons(button, true);
        clickedButtonTags.remove(clickedButtonTags.size() - 1);

    }

    private static int getRandom(int start, int end) {
        return (int) ((Math.random() * (end - start)) + start);
    }

    public void imageEnlarge(View view) {
        if (isGameCompleted()) {
            return;
        }
        if (nextButton.getVisibility() == View.VISIBLE) {
            return;
        } else {
            Intent imageIntent = new Intent(this, EnlargePics.class);
            Log.i("image", "Enlarged");
            ImageView imageView = (ImageView) view;
            Log.i("Tag: ", imageView.getTag().toString());
            if (imageView.getTag().equals("1")) {
                imageIntent.putExtra("ImageName", availableRounds.get(currentRound).getFirstImage());
            } else {
                imageIntent.putExtra("ImageName", availableRounds.get(currentRound).getSecondImage());
            }
            startActivity(imageIntent);
        }
    }

   /* public void allRounds(View view) {
        Intent intent = new Intent(this, Rounds.class);
        intent.putExtra("NumberOfRounds", availableRounds.size());
        startActivity(intent);
    }*/

    private boolean isGameCompleted() {
        return currentRound == availableRounds.size();
    }
} 