package com.example.akimchukdaniel.findmypuppy;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;

/**
 * Created by akimchukdaniel on 12/6/16.
 */

public class SettingsActivity extends Activity {
    EditText name, phone;
    RadioGroup bgColor, defaultView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings);
        name = (EditText) findViewById(R.id.nameSetting);
        bgColor = (RadioGroup) findViewById(R.id.backgroundRadio);
        defaultView = (RadioGroup) findViewById(R.id.defaultView);
        phone = (EditText) findViewById(R.id.phoneSetting);

        LinearLayout layout = (LinearLayout) findViewById(R.id.settingsLayout);
        SharedPreferences preferences = getSharedPreferences("preferences", MODE_PRIVATE);

        switch (preferences.getString("bgColor", "white")) {
            case "white":
                layout.setBackgroundColor(getResources().getColor(R.color.white));
                break;
            case "magenta":
                layout.setBackgroundColor(getResources().getColor(R.color.magenta));
                break;
            case "blue":
                layout.setBackgroundColor(getResources().getColor(R.color.blue));
                break;
            case "orange":
                layout.setBackgroundColor(getResources().getColor(R.color.orange));
                break;
            case "green":
                layout.setBackgroundColor(getResources().getColor(R.color.green));
                break;
        }

        if (!preferences.getString("name", "").equals("")) {
            name.setText(preferences.getString("name", ""));
        }

        if (!preferences.getString("phone", "").equals("")) {
            phone.setText(preferences.getString("phone", ""));
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        SharedPreferences preferences = getSharedPreferences("preferences", MODE_PRIVATE);
        preferences.edit().putString("name", name.getText().toString())
                .putString("bgColor", ((RadioButton)findViewById(bgColor.getCheckedRadioButtonId())).getContentDescription().toString())
                .putString("defaultView",((RadioButton)findViewById(defaultView.getCheckedRadioButtonId())).getContentDescription().toString())
                .putString("phone", phone.getText().toString())
                .commit();
    }
}
