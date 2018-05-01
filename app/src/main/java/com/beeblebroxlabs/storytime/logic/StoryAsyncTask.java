package com.beeblebroxlabs.storytime.logic;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;
import com.StoryTimeApplication;
import com.beeblebroxlabs.storytime.R;
import com.beeblebroxlabs.storytime.database.SavedStory;
import com.beeblebroxlabs.storytime.database.SavedStoryDatabase;

/**
 * Created by devgr on 19-Mar-18.
 */

public class StoryAsyncTask extends AsyncTask<Integer, Void, SavedStory> {

  private static final int UPDATE_SAVED_STORY = 1;
  private static final int INSERT_SAVED_STORY = 2;
  private static final String TAG = "ReadStoryActivity";

  SavedStoryDatabase savedStoryDatabase;
  SavedStory savedStory;
  MenuItem item;
  Context context;


  public StoryAsyncTask(MenuItem item, SavedStory savedStory, Context context) {
    this.savedStory = savedStory;
    this.item = item;
    this.context = context;
    this.savedStoryDatabase = SavedStoryDatabase.getInstance(StoryTimeApplication.getAppContext());
  }


  @Override
  protected SavedStory doInBackground(Integer... ids) {
    int id = ids[0];
    SavedStory newSavedStory = savedStory;
    if (id == UPDATE_SAVED_STORY) {
      savedStoryDatabase.savedTimeDao().update(savedStory);
      Log.i(TAG,"story updated");
    } else if (id == INSERT_SAVED_STORY) {
      savedStoryDatabase.savedTimeDao().insert(savedStory);
      Log.i(TAG,"story inserted");
    }
    return newSavedStory;
  }

  @Override
  protected void onPostExecute(SavedStory savedStory) {
    super.onPostExecute(savedStory);
    if (item != null && savedStory!=null) {
      System.out.println("Inside Post Execute");
      if (savedStory.isSaved() == TRUE) {
        item.setIcon(R.drawable.ic_bookmark_white_24dp);
        Toast.makeText(context,context.getString(R.string.bookmark_added), Toast.LENGTH_SHORT).show();
      } else if (savedStory.isSaved() == FALSE) {
        item.setIcon(R.drawable.ic_bookmark_border_white_24dp);
        Toast.makeText(context,context.getString(R.string.bookmark_removed), Toast.LENGTH_SHORT).show();
      }
    }

  }
}
