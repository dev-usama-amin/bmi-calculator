package com.oneedge.bmicalculator;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.shashank.sony.fancytoastlib.FancyToast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import in.galaxyofandroid.spinerdialog.OnSpinerItemClick;
import in.galaxyofandroid.spinerdialog.SpinnerDialog;

public class MainActivity extends AppCompatActivity {

    private EditText gender,age,height,weight;
    private TextView bmi,bmr,us_unit,us_metric,bmi_status,weight_tv,height_tv;
    private DataBaseHelper helper;
    private String unit_type = "US";
    private AdView mAdView;
    private Double bmi_value;
    private long bmr_value;
    private AlertDialog dialog;
    private static final String TAG = "MainActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });

        init();

        findViewById(R.id.relativeLayout).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                Log.d(TAG, "onClick: ");
                InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                return true;
            }
        });

        us_unit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!unit_type.equals("US")){
                    unit_type = "US";
                    gender.setText("");
                    age.setText("");
                    height.setText("");
                    weight.setText("");
                    bmi.setText("BMI = 0");
                    bmr.setText("BMR = 0");
                    height_tv.setText("Height (feet)");
                    weight_tv.setText("Weight (pounds)");
                    bmi_status.setText("Normal");
                    us_metric.setBackground(null);
                    us_unit.setBackground(getResources().getDrawable(R.drawable.btn_bg_white));
                }
            }
        });

        us_metric.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (unit_type.equals("US")){
                    unit_type = "Metric";
                    gender.setText("");
                    age.setText("");
                    height.setText("");
                    weight.setText("");
                    bmi.setText("BMI = 0");
                    bmr.setText("BMR = 0");
                    height_tv.setText("Height (cm)");
                    weight_tv.setText("Weight (kg)");
                    bmi_status.setText("Normal");
                    us_unit.setBackground(null);
                    us_metric.setBackground(getResources().getDrawable(R.drawable.btn_bg_white));
                }
            }
        });

        gender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showGenderDialogue();
            }
        });

        findViewById(R.id.calculate).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                calculateData();
            }
        });

        findViewById(R.id.bmr_card).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),CaloriesPerDay.class);
                intent.putExtra("bmr",bmr_value+"");
                startActivity(intent);
                overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
            }
        });

        findViewById(R.id.history).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(),History.class));
                overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
            }
        });

        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

    }

    private void init(){
        gender = findViewById(R.id.gender);
        age = findViewById(R.id.age);
        height = findViewById(R.id.height);
        weight = findViewById(R.id.weight);
        bmi = findViewById(R.id.bmi);
        bmr = findViewById(R.id.bmr);
        helper = new DataBaseHelper(getApplicationContext());
        us_metric = findViewById(R.id.us_metric);
        us_unit = findViewById(R.id.us_unit);
        bmi_status = findViewById(R.id.bmi_status);
        height_tv = findViewById(R.id.height_tv);
        weight_tv = findViewById(R.id.weight_tv);
    }

    private String getDate(){
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat mdformat = new SimpleDateFormat("yyyy-MM-dd");
        String outputText = mdformat.format(calendar.getTime());
        return outputText;
    }

    private void calculateData(){
        if (age.getText().toString().isEmpty() || height.getText().toString().isEmpty() || weight.getText().toString().isEmpty()||gender.getText().toString().isEmpty()) {
            FancyToast.makeText(getApplicationContext(), "You must fill in all of the fields.", 1, FancyToast.ERROR, false).show();
            return;
        }

        int a = Integer.parseInt(age.getText().toString().trim());
        Double h = Double.parseDouble(height.getText().toString().trim());
        Double w = Double.parseDouble(weight.getText().toString().trim());

        if (a < 15 || a > 80){
            FancyToast.makeText(getApplicationContext(), "Please enter an age between 15 & 80.", 1, FancyToast.ERROR, false).show();
            return;
        }


        calculateBMI(h,w);
        calculateBMR(a,gender.getText().toString().trim(),h,w);
        saveBMIANDBMRData();
    }

    private void saveBMIANDBMRData() {
        HistoryModel model = new HistoryModel();
        model.setDate(getDate());
        model.setBmr(bmr_value+"");
        model.setBmi(bmi_value+"");
        helper.addToDB(model);
    }

    private void calculateBMI( Double h, Double w) {
        if (unit_type.equals("US")) {
            double heightInInches = h * 12;
            double result = w / Math.pow(heightInInches,2);
            Log.d(TAG, "calculateBMI:result "+result);
            double finalResult = result * 703;
            Log.d(TAG, "calculateBMI:finalResult "+finalResult);
            Log.d(TAG, "calculateBMI:format "+String.format("%.1f",finalResult));
            bmi.setText("BMI = "+String.format("%.1f",finalResult));
            bmi_value = Double.parseDouble(String.format("%.1f",finalResult));
        } else {
            double heightInMeters = h / 100;
            double wDividedByh = w / heightInMeters;
            double finalResult = wDividedByh / heightInMeters;
            bmi.setText("BMI = "+String.format("%.1f",finalResult));
            bmi_value = Double.parseDouble(String.format("%.1f",finalResult));
        }
        if (bmi_value <= 18.5) {
            bmi_status.setText("Underweight");
        }
        if (bmi_value >= 18.5 && bmi_value <= 24.9 ){
            bmi_status.setText("Normal Weight");
        }
        if (bmi_value >= 25.0 && bmi_value <= 29.9) {
            bmi_status.setText("Overweight");
        }
        if (bmi_value >= 30.0 ){
            bmi_status.setText("Obese");
        }
    }

    private void calculateBMR(int a, String g, Double h, Double w){

        if (unit_type.equals("US")) {
            if (g.equals("Male")) {
                double heightInInches = h * 12;
                double result = (4.536 * w) + (15.88 * heightInInches) - (double) (5 * a) + 5;
                bmr.setText("BMR = "+Math.round(result));
                bmr_value = Math.round(result);
            } else {
                double heightInInches = h * 12;
                double result = (4.536 * w) + (15.88 * heightInInches) - (double) (5 * a) - 161;
                bmr.setText("BMR = "+Math.round(result));
                bmr_value = Math.round(result);
            }
        }else{
            if (g.equals("Male")) {
                double result = (10 * w) + (6.25 * h) - (double)(5 * a) + 5;
                bmr.setText("BMR = "+Math.round(result));
                bmr_value = Math.round(result);
            } else {
                double result = (10 * w) + (6.25 * h) - (double)(5 * a) - 161;
                bmr.setText("BMR = "+Math.round(result));
                bmr_value = Math.round(result);
            }
        }
    }

    private void showGenderDialogue(){

        View view = LayoutInflater.from(getApplicationContext()).inflate(R.layout.select_gender_dialogue,null);

        view.findViewById(R.id.male).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gender.setText("Male");
                dialog.cancel();
            }
        });

        view.findViewById(R.id.female).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gender.setText("Female");
                dialog.cancel();
            }
        });

        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setView(view);
        builder.setCancelable(false);
        dialog = builder.create();
        dialog.show();
    }
}