package com.idan.jimwendlercalc;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

public class Calc extends Activity {

    private SharedPreferences sharedPref;
    private Editor editor;

    int textFields[][] = {
            { R.id.squatMax, R.id.pressMax, R.id.deadliftMax, R.id.benchMax },
            { R.string.saved_squat_settings, R.string.saved_press_settings, R.string.saved_deadlift_settings, R.string.saved_bench_settings }
    };

    public static int[][][] setTexts = {
            { // Squat
                    { R.id.a1, R.id.a2, R.id.a3 },
                    { R.id.a4, R.id.a5, R.id.a6 },
                    { R.id.a7, R.id.a8, R.id.a9 },
                    { R.id.a10, R.id.a11, R.id.a12 }
            },
            { // Press
                    { R.id.b1, R.id.b2, R.id.b3 },
                    { R.id.b4, R.id.b5, R.id.b6 },
                    { R.id.b7, R.id.b8, R.id.b9 },
                    { R.id.b10, R.id.b11, R.id.b12 }
            },
            { // Deadlift
                    { R.id.c1, R.id.c2, R.id.c3 },
                    { R.id.c4, R.id.c5, R.id.c6 },
                    { R.id.c7, R.id.c8, R.id.c9 },
                    { R.id.c10, R.id.c11, R.id.c12 }
            },
            { // Bench
                    { R.id.d1, R.id.d2, R.id.d3 },
                    { R.id.d4, R.id.d5, R.id.d6 },
                    { R.id.d7, R.id.d8, R.id.d9 },
                    { R.id.d10, R.id.d11, R.id.d12 }
            }
    };

    int percs[][] = {
            { 65, 75, 85 },
            { 70, 80, 90 },
            { 75, 85, 95 },
            { 40, 50, 60 }
    };

    String reps[][] = {
            { "5", "5", "5+" },
            { "3", "3", "3+" },
            { "5", "3", "1+" },
            { "5", "5", "5" }
    };

    // bool for red color texts
    int RedStrings = 0;

    boolean Flag = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.calc);

        sharedPref = getPreferences(Context.MODE_PRIVATE);
        editor = sharedPref.edit();

        // get default values for each field
        float squatMaxVal       = sharedPref.getFloat(getString(R.string.saved_squat_settings), 0f);
        float pressMaxVal       = sharedPref.getFloat(getString(R.string.saved_press_settings), 0f);
        float deadLiftMaxVal    = sharedPref.getFloat(getString(R.string.saved_deadlift_settings), 0f);
        float benchMaxVal       = sharedPref.getFloat(getString(R.string.saved_bench_settings), 0f);

        // declare text fields
        EditText squatMax       = (EditText)findViewById(R.id.squatMax);
        EditText pressMax       = (EditText)findViewById(R.id.pressMax);
        EditText deadLiftMax    = (EditText)findViewById(R.id.deadliftMax);
        EditText benchMax       = (EditText)findViewById(R.id.benchMax);

        // focus on first field
        squatMax.requestFocus();

        // populate fields with values
        squatMax.setText(Float.toString(squatMaxVal));
        pressMax.setText(Float.toString(pressMaxVal));
        deadLiftMax.setText(Float.toString(deadLiftMaxVal));
        benchMax.setText(Float.toString(benchMaxVal));

        // press OK when needed
        if (squatMaxVal > 0f)       onPressOK(findViewById(R.id.squatMaxOK));
        if (pressMaxVal > 0f)       onPressOK(findViewById(R.id.pressMaxOK));
        if (deadLiftMaxVal > 0f)    onPressOK(findViewById(R.id.deadliftMaxOK));
        if (benchMaxVal > 0f)       onPressOK(findViewById(R.id.benchMaxOK));
    }

    public void onPressOK(View view) {

        int index = 0;
        switch (view.getId()) {
            case (R.id.squatMaxOK) :
                index = 0;
                break;
            case (R.id.pressMaxOK) :
                index = 1;
                break;
            case (R.id.deadliftMaxOK) :
                index = 2;
                break;
            case (R.id.benchMaxOK) :
                index = 3;
                break;
        }

        EditText textField = (EditText)findViewById(textFields[0][index]);
        Float baseWeight = Float.valueOf(textField.getText().toString());

        // Save value to memory in appropriate field
        // I used textFields to ease the corresponding strings to save from the array instead of using the case
        editor.putFloat(getString(textFields[1][index]), baseWeight);
        editor.commit();
        builder(baseWeight,index);
        // closes keyboard
        InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
    }

    // The formula for a correct set weight

    public float setForm(Float baseWeight,int percs[][], int i, int j) {

        float set = (float) Math.round((baseWeight * 0.9) * (percs[i][j]) / 100);

        if (set >= 20f) {
            set -= 20f;
            set /= 2f;
        } else {
            Flag = true;
        }

        set /= 10f;
        set = (float)Math.round(set * 8f) / 8f;
        set *= 10f;
        if (set <= 1.25f)
            Flag = false;

        return set;
    }

    // puts the weights into the textview

    public void builder (float baseWeight, int index) {
        for (int i = 0; i < setTexts[0].length; i++) {
            for (int j = 0; j < setTexts[0][0].length; j++) {
                float thisSet = setForm(baseWeight, percs, i, j);
                TextView Cell = (TextView)findViewById(setTexts[index][i][j]);
                if (j == 0)
                {
                    Cell.setText(Float.toString(thisSet) + " x " + reps[i][j]);
                }
                else {
                    // if not the 1st set, make the set the delta.
                    float delta = thisSet - setForm(baseWeight, percs, i, j-1);
                    if (delta>0)
                    Cell.setText(Float.toString(delta) + " x " + reps[i][j]);
                    else Cell.setText("0" + " x " + reps[i][j]);
                    }
                // paints the weight red if it is a weight to be lifted without the bar
                if (Flag) {
                    Cell.setTextColor(Color.RED);
                    Cell.setTypeface(null, Typeface.BOLD_ITALIC);
                } else {
                    Cell.setTextColor(Color.BLACK);
                    Cell.setTypeface(null, Typeface.NORMAL);
                }
                CountReds();
                TextView WarningText = (TextView)findViewById(R.id.textWarning);
                WarningText.setVisibility(this.RedStrings > 0 ? View.VISIBLE : View.INVISIBLE);
            }
        }
    }


    private void CountReds() {
        TextView text;
        this.RedStrings = 0;
        for (int i = 0; i < setTexts.length; i++) {
            for (int j = 0; j < setTexts[0].length; j++) {
                for (int k = 0; k < setTexts[0][0].length; k++) {
                    text = (TextView)findViewById(setTexts[i][j][k]);
                    if (text.getCurrentTextColor() == Color.RED)
                        this.RedStrings++;
                }
            }
        }
    }

    public void onRedTextClick(View view) {

        TextView text = (TextView)findViewById(view.getId());

        if (text.getCurrentTextColor() == Color.RED) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Total free weight (no bar)")
                    .setCancelable(false)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.dismiss();
                        }
                    });
            AlertDialog alert = builder.create();
            alert.show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.calc, menu);
        return true;
    }
}


