package com.sb.play.pictoword;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;

import com.vit.assignment.pictoword.R;

import androidx.appcompat.app.AppCompatActivity;

public class Rounds extends AppCompatActivity {
    int totalRounds;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rounds);
        Intent intent = getIntent();
        totalRounds = intent.getIntExtra("NumberOfRounds", 1);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        rounds();
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return false;
    }

    public void rounds() {
        LinearLayout linearLayout = findViewById(R.id.linearLayout);
        //linearLayout.
        for (int i = 0; i < 3; i++) {
            Button button = new Button(this);
            button.setText("A");
            linearLayout.addView(button);
        }
    }
}