package com.beeblebroxlabs.storytime.presentation.activity;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.beeblebroxlabs.storytime.R;
import com.beeblebroxlabs.storytime.database.SavedStory;
import com.beeblebroxlabs.storytime.logic.Story;
import com.beeblebroxlabs.storytime.presentation.ShowSavedStoriesViewModel;
import com.beeblebroxlabs.storytime.logic.StoryAsyncTask;
import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class ReadStoryActivity extends AppCompatActivity {

  private static final int UPDATE_SAVED_STORY = 1;
  private static final int INSERT_SAVED_STORY = 2;

  private static final String TAG = "ReadStoryActivity";


  private Query query;
  private FirebaseDatabase mFirebaseDatabase;
  private FirebaseStorage mFirebaseStorage;
  private DatabaseReference mStoriesDatabaseReference;
  private StorageReference mStoryImageStorageReference;
  private int storyId;

  @BindView(R.id.contentTextView)
  TextView contentTextView;

  @BindView(R.id.contentImageView)
  ImageView contentImageView;

  SavedStory savedStory;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    SharedPreferences mPrefs =  PreferenceManager.getDefaultSharedPreferences(this);
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_read_story);
    Toolbar storyToolbar = findViewById(R.id.story_toolbar);
    TextView toolbarTitle = findViewById(R.id.toolbar_title);
    storyToolbar.setTitle("");
    storyToolbar.setSubtitle("");
    setSupportActionBar(storyToolbar);
    ActionBar actionBar = getSupportActionBar();
    if (actionBar != null) {
      actionBar.setDisplayHomeAsUpEnabled(true);
    }

    ButterKnife.bind(this);

    Intent intent = getIntent();
    savedStory = new SavedStory(-1,FALSE);
    mFirebaseDatabase = FirebaseDatabase.getInstance();
    mFirebaseStorage = FirebaseStorage.getInstance();

    storyId = intent.getIntExtra("storyId", -1);
    if (storyId < 0) {
      Toast.makeText(this, getString(R.string.story_fetch_message_negative), Toast.LENGTH_SHORT)
          .show();
      Log.e(TAG, getString(R.string.story_fetch_message_negative));
      startMainActivity();
    }

    mStoriesDatabaseReference = mFirebaseDatabase.getReference();
    mStoryImageStorageReference = mFirebaseStorage.getReference().child("story_photo");

    query = mStoriesDatabaseReference.child("stories").child(
        Integer.toString(storyId));

    query.addListenerForSingleValueEvent(new ValueEventListener() {
      @Override
      public void onDataChange(DataSnapshot dataSnapshot) {
        Story story = dataSnapshot.getValue(Story.class);

        contentTextView.setText(story.getContent().replace("_n", "\n"));
        toolbarTitle.setText(story.getTitle());

        StorageReference storageReference = mStoryImageStorageReference.child(story.getPhotoUrl());
        Glide.with(getApplicationContext())
            .load(storageReference)
            .into(contentImageView);
      }

      @Override
      public void onCancelled(DatabaseError databaseError) {
        Toast.makeText(ReadStoryActivity.this, getString(R.string.story_fetch_message_negative),
            Toast.LENGTH_SHORT)
            .show();
        Log.e(TAG, databaseError.getDetails());
        startMainActivity();
      }
    });
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.menu_story, menu);
    setBookmarkIcon(menu.findItem(R.id.action_bookmark));
    return true;
  }

  @Override
  protected void onResume() {
    super.onResume();
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    int id = item.getItemId();
    if (id == R.id.action_bookmark) {
      if (savedStory.getId() < 0) {
        Log.i(TAG, "savedStory null");
        SavedStory newSavedStory;
        newSavedStory = new SavedStory(storyId, TRUE);
        new StoryAsyncTask(item, newSavedStory, this).execute(INSERT_SAVED_STORY);
      } else {
        Log.i(TAG, "savedStory not null"+savedStory.getId());
        if (savedStory.isSaved() == TRUE) {
          savedStory.setSaved(FALSE);
        } else if (savedStory.isSaved() == FALSE) {
          savedStory.setSaved(TRUE);
        }
        Log.i(TAG, "savedStory not null:" + savedStory.isSaved());
        new StoryAsyncTask(item, savedStory, this).execute(UPDATE_SAVED_STORY);
      }
      return true;
    } else if (id == android.R.id.home) {
      finish();
      return true;
    }

    return super.onOptionsItemSelected(item);
  }


  @Override
  public void onBackPressed() {
    finish();
    super.onBackPressed();
  }

  public void startMainActivity() {
    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
    startActivity(intent);
  }

  public void setBookmarkIcon(MenuItem item) {
    ShowSavedStoriesViewModel viewModel;
    viewModel = ViewModelProviders.of(this).get(ShowSavedStoriesViewModel.class);
    viewModel.getSavedStory(storyId).observe(this, savedStory -> {
      if (savedStory != null) {
        this.savedStory = savedStory;
        if (savedStory.isSaved() == TRUE) {
          item.setIcon(R.drawable.ic_bookmark_white_24dp);
        } else if (savedStory.isSaved() == FALSE) {
          item.setIcon(R.drawable.ic_bookmark_border_white_24dp);
        }
      }
    });
  }


}
