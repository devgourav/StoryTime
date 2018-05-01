package com.beeblebroxlabs.storytime.database;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;

/**
 * Created by devgr on 21-Feb-18.
 */

@Entity(tableName = "savedStories")
public class SavedStory {

  @PrimaryKey
  private int id;
  private boolean isSaved;

  public SavedStory() {
  }


  @Ignore
  public SavedStory(int id, boolean isSaved) {
    this.id = id;
    this.isSaved = isSaved;
  }

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public boolean isSaved() {
    return isSaved;
  }

  public void setSaved(boolean saved) {
    isSaved = saved;
  }
}
