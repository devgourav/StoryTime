package com.beeblebroxlabs.storytime.logic;

import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by devgr on 21-Feb-18.
 */

public class FirebaseDatabaseUtil {
  private static FirebaseDatabase mDatabase;

  public static FirebaseDatabase getDatabase() {
    if (mDatabase == null) {
      mDatabase = FirebaseDatabase.getInstance();
      mDatabase.setPersistenceEnabled(true);
    }
    return mDatabase;
  }

}