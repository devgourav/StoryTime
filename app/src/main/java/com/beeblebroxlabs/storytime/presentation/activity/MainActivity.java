package com.beeblebroxlabs.storytime.presentation.activity;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.Settings;
import android.support.annotation.ColorInt;
import android.support.design.widget.NavigationView;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.ImageViewCompat;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AlertDialog.Builder;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.LayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.StoryTimeApplication;
import com.beeblebroxlabs.storytime.R;
import com.beeblebroxlabs.storytime.logic.FirebaseDatabaseUtil;
import com.beeblebroxlabs.storytime.logic.NetworkUtil;
import com.beeblebroxlabs.storytime.logic.Story;
import com.beeblebroxlabs.storytime.presentation.RecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.github.ybq.android.spinkit.SpinKitView;
import com.github.ybq.android.spinkit.style.RotatingCircle;
import com.github.ybq.android.spinkit.style.Wave;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class MainActivity extends AppCompatActivity
    implements NavigationView.OnNavigationItemSelectedListener {

  private static final String TAG = "MainActivity";
  private static final int SETTINGS_REQUEST_CODE = 0;
  private static Bundle mBundleRecyclerViewState;
  private final String LIST_STATE_KEY = "recycler_state";
  private SharedPreferences.OnSharedPreferenceChangeListener prefListener;
  FirebaseRecyclerAdapter adapter;
  private Builder networkAlertDialog;
  private Boolean isFirstRun=false;
  private SharedPreferences prefs;

  @BindView(R.id.story_recycler_view)
  RecyclerView mRecyclerView;

  @BindView(R.id.all_load_spinKitVew)
  ProgressBar progressBar;


  @Override
  protected void onCreate(Bundle savedInstanceState) {
    prefs = android.preference.PreferenceManager.getDefaultSharedPreferences(this);

    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    Toolbar toolbar = findViewById(R.id.main_toolbar);
    toolbar.setTitle("");
    toolbar.setSubtitle("");
    setSupportActionBar(toolbar);
    ButterKnife.bind(this);

    addNavigationDrawer(toolbar);

    NetworkUtil networkUtil = new NetworkUtil(this);

    if(networkUtil.isMobileConnected() || networkUtil.isWifiConnected()){
      Log.i(TAG,"Network is available");
    }else{
      setNetworkAlertDialog();
      networkAlertDialog.show();
    }

    if(prefs.getBoolean("NEW_STORY_NOTIFY",true)){
      FirebaseMessaging.getInstance().subscribeToTopic("new_story");
    }else{
      FirebaseMessaging.getInstance().unsubscribeFromTopic("new_story");
    }




    progressBar.setIndeterminateDrawable(new RotatingCircle());

    FirebaseStorage mFirebaseStorage = FirebaseStorage.getInstance();
    DatabaseReference mStoriesDatabaseReference = FirebaseDatabaseUtil.getDatabase().getReference();
    StorageReference mStoryImageStorageReference = mFirebaseStorage.getReference()
        .child("story_photo");

    Query query = mStoriesDatabaseReference.child("stories");

    query.addListenerForSingleValueEvent(new ValueEventListener() {
      @Override
      public void onDataChange(DataSnapshot dataSnapshot) {
        progressBar.setVisibility(View.GONE);
      }

      @Override
      public void onCancelled(DatabaseError databaseError) {
        Toast.makeText(MainActivity.this, getString(R.string.story_fetch_message_negative),
            Toast.LENGTH_SHORT)
            .show();
        Log.e(TAG, databaseError.getDetails());
      }
    });

    FirebaseRecyclerOptions<Story> options = new FirebaseRecyclerOptions.Builder<Story>()
        .setQuery(query, Story.class)
        .build();

    adapter = new RecyclerAdapter(options, getApplicationContext(), mStoryImageStorageReference);

    LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
    mRecyclerView.setLayoutManager(mLayoutManager);
    mRecyclerView.setAdapter(adapter);

  }


  @Override
  public void onBackPressed() {
    DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
    if (drawer.isDrawerOpen(GravityCompat.START)) {
      drawer.closeDrawer(GravityCompat.START);
    } else {
      super.onBackPressed();
    }
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    return super.onOptionsItemSelected(item);
  }

  @SuppressWarnings("StatementWithEmptyBody")
  @Override
  public boolean onNavigationItemSelected(MenuItem item) {
    // Handle navigation view item clicks here.
    int id = item.getItemId();

    if (id == R.id.nav_share) {
      Intent intent = new Intent(Intent.ACTION_VIEW);
      intent.setData(Uri.parse("market://details?id=com.beeblebroxlabs.storytime"));
      startActivity(intent);
    }
    if (id == R.id.nav_settings) {
      Intent intent = new Intent(this,SettingsActivity.class);
      startActivity(intent);
    } else if (id == R.id.nav_bookmark) {
      Intent intent = new Intent(this, SavedStoriesActivity.class);
      intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
      startActivity(intent);
    } else if (id == R.id.nav_home) {
//      Intent intent = new Intent(getApplicationContext(),MainActivity.class);
//      intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//      startActivity(intent);
    }

    DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
    drawer.closeDrawer(GravityCompat.START);
    return true;
  }

  public void addNavigationDrawer(Toolbar toolbar) {
    DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
    ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
        this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
    drawer.addDrawerListener(toggle);
    toggle.syncState();

    NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
    navigationView.setNavigationItemSelectedListener(this);
  }

  public void setNetworkAlertDialog(){
    networkAlertDialog = new AlertDialog.Builder(this);
    networkAlertDialog.setTitle(getString(R.string.network_alert_dialog_title));
    networkAlertDialog.setMessage(getString(R.string.network_alert_dialog_message));
    networkAlertDialog.setIcon(R.drawable.ic_signal_wifi_off_black_18dp);
    networkAlertDialog.setPositiveButton("SETTINGS", new OnClickListener() {
      @Override
      public void onClick(DialogInterface dialogInterface, int i) {
        startActivityForResult(new Intent(Settings.ACTION_SETTINGS),SETTINGS_REQUEST_CODE);
      }
    });
    networkAlertDialog.setNegativeButton("LATER", new OnClickListener() {
      @Override
      public void onClick(DialogInterface dialogInterface, int i) {
        if(isFirstRun){
          finish();
        }else{
          dialogInterface.cancel();
        }
      }
    });
  }

  @Override
  protected void onPause() {
    super.onPause();

    // save RecyclerView state
    mBundleRecyclerViewState = new Bundle();
    Parcelable listState = mRecyclerView.getLayoutManager().onSaveInstanceState();
    mBundleRecyclerViewState.putParcelable(LIST_STATE_KEY, listState);

  }

  @Override
  protected void onResume() {
    super.onResume();

    if(prefs.getBoolean("first_run",true)){
      prefs.edit().putBoolean("first_run",false).commit();
    }
    if (mBundleRecyclerViewState != null) {
      Parcelable listState = mBundleRecyclerViewState.getParcelable(LIST_STATE_KEY);
      mRecyclerView.getLayoutManager().onRestoreInstanceState(listState);
    }
  }


  @Override
  protected void onStart() {
    super.onStart();
    adapter.startListening();
  }

  @Override
  protected void onStop() {
    super.onStop();
    adapter.stopListening();
  }
}



