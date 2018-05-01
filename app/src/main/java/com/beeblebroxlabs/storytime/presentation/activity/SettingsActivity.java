package com.beeblebroxlabs.storytime.presentation.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Build;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.app.AppCompatDelegate.NightMode;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.beeblebroxlabs.storytime.R;
import com.beeblebroxlabs.storytime.SettingsFragment;

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

  public void restartApp(){
    startActivity(new Intent(this,SettingsActivity.class));
    finish();
  }
}
