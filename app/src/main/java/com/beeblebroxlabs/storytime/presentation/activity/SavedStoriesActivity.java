package com.beeblebroxlabs.storytime.presentation.activity;

import static java.lang.Boolean.TRUE;

import android.annotation.SuppressLint;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.LayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.Toast;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.beeblebroxlabs.storytime.R;
import com.beeblebroxlabs.storytime.database.SavedStory;
import com.beeblebroxlabs.storytime.database.SavedStoryDatabase;
import com.beeblebroxlabs.storytime.logic.FirebaseDatabaseUtil;
import com.beeblebroxlabs.storytime.logic.Story;
import com.beeblebroxlabs.storytime.presentation.listener.DataListenerInterface;
import com.beeblebroxlabs.storytime.presentation.ShowSavedStoriesViewModel;
import com.beeblebroxlabs.storytime.presentation.StoryListAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import java.util.ArrayList;
import java.util.List;

public class SavedStoriesActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

  public static final String TAG = "SavedStoriesActivity";


  @BindView(R.id.saved_story_recycler_view)
  RecyclerView mRecyclerView;


  private Query query;
  StoryListAdapter adapter;
  private LayoutManager mLayoutManager;


//  List<SavedStory> savedStoryList;
  List<Story> storyList;
  List<Integer> savedIdList;


  DataListenerInterface dataListenerInterface;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    SharedPreferences mPrefs =  PreferenceManager.getDefaultSharedPreferences(this);
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_saved_stories);
    Toolbar toolbar = findViewById(R.id.saved_story_toolbar);
    toolbar.setTitle("");
    toolbar.setSubtitle("");
    setSupportActionBar(toolbar);
    addNavigationDrawer(toolbar);

    ButterKnife.bind(this);
    SavedStoryDatabase storyTimeDatabase = SavedStoryDatabase.getInstance(getApplicationContext());


//    savedStoryList = new ArrayList<>();
    storyList = new ArrayList<>();
    savedIdList = new ArrayList<>();

    FirebaseStorage mFirebaseStorage = FirebaseStorage.getInstance();
    DatabaseReference mStoriesDatabaseReference = FirebaseDatabaseUtil.getDatabase().getReference();
    StorageReference mStoryImageStorageReference = mFirebaseStorage.getReference()
        .child("story_photo");

    adapter = new StoryListAdapter(getApplicationContext(), storyList,
        mStoryImageStorageReference);
    mLayoutManager = new LinearLayoutManager(getApplicationContext());

    query = mStoriesDatabaseReference.child("stories");


    ShowSavedStoriesViewModel viewModel = ViewModelProviders.of(this).get(ShowSavedStoriesViewModel.class);
    viewModel.getSavedStories()
        .observe(this, savedStories -> {
          if (savedStories.size() > 0) {
            System.out.println("Stories in DB:"+savedStories.size());
            savedIdList.clear();
            for(int i=0;i<savedStories.size();i++){
              if(savedStories.get(i).isSaved() == TRUE){
                savedIdList.add(savedStories.get(i).getId());
              }
            }
            query.addListenerForSingleValueEvent(new ValueEventListener() {
              @Override
              public void onDataChange(DataSnapshot dataSnapshot) {
                System.out.println("onDataChange:");
                dataListenerInterface.onSuccess(dataSnapshot);
              }
              @Override
              public void onCancelled(DatabaseError databaseError) {
                System.out.println("OnCancelled:"+databaseError);
                dataListenerInterface.onFailure(databaseError);
              }
            });
          } else {
            System.out.println("No stories saved");
          }
        });

    dataListenerInterface = new DataListenerInterface() {
      @Override
      public void onSuccess(DataSnapshot dataSnapshot) {
        for (DataSnapshot storySnapshot : dataSnapshot.getChildren()) {
          Story story = storySnapshot.getValue(Story.class);
          if (savedIdList.contains(story.getId())) {
            storyList.add(story);
          }
        }
        System.out.println("Nos of saved Stories:" + adapter.getItemCount());
        adapter.notifyDataSetChanged();
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(adapter);
      }

      @Override
      public void onFailure(DatabaseError databaseError) {
        Toast.makeText(SavedStoriesActivity.this, getString(R.string.database_rw_error_message)+databaseError, Toast.LENGTH_SHORT).show();
      }
    };
  }

  public void addNavigationDrawer(Toolbar toolbar){
    DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
    ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
        this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
    drawer.addDrawerListener(toggle);
    toggle.syncState();
    NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
    navigationView.setNavigationItemSelectedListener(this);
  }

  @Override
  public boolean onNavigationItemSelected(@NonNull MenuItem item) {
    int id = item.getItemId();

    if(id == R.id.nav_share) {
      Intent intent = new Intent(Intent.ACTION_VIEW);
      intent.setData(Uri.parse("market://details?id=com.beeblebroxlabs.storytime"));
      startActivity(intent);
    }if (id == R.id.nav_settings) {
      Intent intent = new Intent(getApplicationContext(),SettingsActivity.class);
      startActivity(intent);

    }else if(id == R.id.nav_bookmark){
//      Intent intent = new Intent(getApplicationContext(),SavedStoriesActivity.class);
//      intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//      startActivity(intent);
    }else if(id == R.id.nav_home){
      Intent intent = new Intent(getApplicationContext(),MainActivity.class);
      intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
      startActivity(intent);
    }

    DrawerLayout drawer = findViewById(R.id.drawer_layout);
    drawer.closeDrawer(GravityCompat.START);
    return true;
  }

  @Override
  protected void onResume() {
    super.onResume();
    storyList.clear();
    adapter.notifyDataSetChanged();
  }

}
