package com.beeblebroxlabs.storytime.presentation;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;
import com.StoryTimeApplication;
import com.beeblebroxlabs.storytime.database.SavedStory;
import com.beeblebroxlabs.storytime.database.SavedStoryDatabase;
import java.util.List;

/**
 * Created by devgr on 08-Mar-18.
 */

public class ShowSavedStoriesViewModel extends ViewModel {

  private LiveData<List<SavedStory>> stories;
  private LiveData<SavedStory> story;
  private SavedStoryDatabase savedStoryDatabase;

  public ShowSavedStoriesViewModel() {
    savedStoryDatabase = SavedStoryDatabase.getInstance(StoryTimeApplication.getAppContext());
  }

  public LiveData<List<SavedStory>> getSavedStories(){
    stories = savedStoryDatabase.savedTimeDao().loadAsync();
    return stories;
  }

  public LiveData<SavedStory> getSavedStory(int id){
    story = savedStoryDatabase.savedTimeDao().fetchRecordById(id);
    return story;
  }

}
