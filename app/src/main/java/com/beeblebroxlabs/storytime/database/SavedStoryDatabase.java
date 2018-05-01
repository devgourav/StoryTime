package com.beeblebroxlabs.storytime.database;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

/**
 * Created by devgr on 20-Feb-18.
 */

@Database(entities = {SavedStory.class},version = 1,exportSchema = false)
public abstract class SavedStoryDatabase extends RoomDatabase {

  private static SavedStoryDatabase sInstance;

 public abstract SavedStoryDao savedTimeDao();

  public static synchronized SavedStoryDatabase getInstance(Context context) {
    if (sInstance == null) {
      sInstance = Room
          .databaseBuilder(context.getApplicationContext(), SavedStoryDatabase.class, "test_db")
          .build();
    }
    return sInstance;
  }


}
