package com.oneedge.bmicalculator;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class CaloriesPerDay extends AppCompatActivity {

    private TextView no_exercise,exercise_1_3,exercise_3_5,exercise_5_7,exercise_hard;
    private double bmr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calories_per_day);

        bmr = Double.parseDouble(getIntent().getExtras().getString("bmr"));
        no_exercise = findViewById(R.id.no_exercise);
        exercise_1_3 = findViewById(R.id.exercise_1_3);
        exercise_3_5 = findViewById(R.id.exercise_3_5);
        exercise_5_7 = findViewById(R.id.exercise_5_7);
        exercise_hard = findViewById(R.id.exercise_hard);

        double cal1 = bmr * 1.2;
        double cal2 = bmr * 1.375;
        double cal3 = bmr * 1.55;
        double cal4 = bmr * 1.725;
        double cal5 = bmr * 1.9;

        no_exercise.setText(Math.round(cal1) + " Calories/Day");
        exercise_1_3.setText(Math.round(cal2) + " Calories/Day");
        exercise_3_5.setText(Math.round(cal3) + " Calories/Day");
        exercise_5_7.setText(Math.round(cal4) + " Calories/Day");
        exercise_hard.setText(Math.round(cal5) + " Calories/Day");

        findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
                finish();
            }
        });

    }

    @Override
    public void onBackPressed() {
        overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
        finish();
    }
}