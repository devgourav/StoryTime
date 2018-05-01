package com;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatDelegate;
/**
 * Created by devgr on 07-Feb-18.
 */

public class StoryTimeApplication extends Application {

  private static Context context;

  private boolean isNightModeEnabled = false;


  public void onCreate() {
    super.onCreate();
    StoryTimeApplication.context = getApplicationContext();

    //Night Mode
    SharedPreferences mPrefs =  PreferenceManager.getDefaultSharedPreferences(this);
    isNightModeEnabled = mPrefs.getBoolean("NIGHT_MODE", false);
    if (isNightModeEnabled) {
      AppCompatDelegate.setDefaultNightMode(
          AppCompatDelegate.MODE_NIGHT_YES);
    } else {
      AppCompatDelegate.setDefaultNightMode(
          AppCompatDelegate.MODE_NIGHT_NO);
    }
  }

  public static Context getAppContext() {
    return StoryTimeApplication.context;
  }

}
