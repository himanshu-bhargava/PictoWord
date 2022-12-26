package com.sb.play.pictoword;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.DragEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.vit.assignment.pictoword.R;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class EnlargePics extends AppCompatActivity {
    String hintImageString;
    Button hintButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enlarge_pics);
        getSupportActionBar().hide();

        Intent intent = getIntent();
        hintButton = findViewById(R.id.hintButton);
        hintImageString = intent.getStringExtra("ImageName");
        ImageView image = findViewById(R.id.bigImage);
        image.setImageResource(this.getResources().getIdentifier(hintImageString, "drawable", this.getPackageName()));
    }

    public void hint(View view) {
        new AlertDialog.Builder(EnlargePics.this)
                .setMessage("First character: " + Character.toUpperCase(hintImageString.charAt(0)) + " , "
                        + "Last character: " + Character.toUpperCase(hintImageString.charAt(hintImageString.length() - 1)))
                .create()
                .show();
    }
}