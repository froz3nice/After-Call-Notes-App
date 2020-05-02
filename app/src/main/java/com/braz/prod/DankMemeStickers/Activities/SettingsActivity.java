package com.braz.prod.DankMemeStickers.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.appcompat.widget.Toolbar;
import androidx.preference.PreferenceManager;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;

import com.braz.prod.DankMemeStickers.R;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        Toolbar toolbar = findViewById(R.id.toolbar);
        RelativeLayout snoop = findViewById(R.id.snoop);
        RelativeLayout joint = findViewById(R.id.joint);
        RelativeLayout glasses = findViewById(R.id.glasses);
        AppCompatCheckBox s_snoop = findViewById(R.id.s_snoop);
        AppCompatCheckBox s_joint = findViewById(R.id.s_joint);
        AppCompatCheckBox s_glasses = findViewById(R.id.s_glasses);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        setSupportActionBar(toolbar);
        // add back arrow to toolbar
        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setTitle("Settings");
        }
        toolbar.setTitle("Settings");
        s_snoop.setChecked(prefs.getBoolean("s_snoop",true));
        snoop.setOnClickListener(view -> {
            s_snoop.setChecked(!s_snoop.isChecked());
            prefs.edit().putBoolean("s_snoop",s_snoop.isChecked()).apply();
        });
        s_joint.setChecked(prefs.getBoolean("s_joint",true));
        joint.setOnClickListener(view -> {
            s_joint.setChecked(!s_joint.isChecked());
            prefs.edit().putBoolean("s_joint",s_joint.isChecked()).apply();
        });
        s_glasses.setChecked(prefs.getBoolean("s_glasses",true));
        glasses.setOnClickListener(view -> {
            s_glasses.setChecked(!s_glasses.isChecked());
            prefs.edit().putBoolean("s_glasses",s_glasses.isChecked()).apply();
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle arrow click here
        if (item.getItemId() == android.R.id.home) {
            finish(); // close this activity and return to preview activity (if there is any)
        }

        return super.onOptionsItemSelected(item);
    }
}
