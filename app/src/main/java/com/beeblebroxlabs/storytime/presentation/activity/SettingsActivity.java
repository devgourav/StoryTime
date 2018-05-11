package com.beeblebroxlabs.storytime.presentation.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.Toolbar;
import com.beeblebroxlabs.storytime.R;

public class SettingsActivity extends AppCompatActivity {


  @Override
  protected void onCreate(Bundle savedInstanceState) {

    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_settings);
    Toolbar toolbar = findViewById(R.id.settings_toolbar);
    toolbar.setSubtitle("");
    toolbar.setTitle("");
    setSupportActionBar(toolbar);

    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
    SharedPreferences.OnSharedPreferenceChangeListener prefListener;

    prefListener = (prefs1, key) -> {
      if(prefs1.getBoolean("NIGHT_MODE",false)){
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        restartApp();
      }else{
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        restartApp();
      }
    };
    prefs.registerOnSharedPreferenceChangeListener(prefListener);

  }

  @Override
  public void onBackPressed() {
    super.onBackPressed();

    Intent intent = new Intent(this, MainActivity.class);
    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
    startActivity(intent);

  }

  public void restartApp(){
    startActivity(new Intent(this,SettingsActivity.class));
    finish();
  }
}
