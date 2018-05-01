package com.beeblebroxlabs.storytime.database;

import static android.arch.persistence.room.OnConflictStrategy.IGNORE;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;
import java.util.List;

/**
 * Created by devgr on 20-Feb-18.
 */

@Dao
public interface SavedStoryDao {

  @Query("select * from savedStories")
  LiveData<List<SavedStory>> loadAsync();

  @Query("select * from savedStories where id = :id")
  LiveData<SavedStory> fetchRecordById(int id);

  @Insert(onConflict = IGNORE)
  long insert(SavedStory savedStory);

  @Update
  void update(SavedStory savedStory);

}
