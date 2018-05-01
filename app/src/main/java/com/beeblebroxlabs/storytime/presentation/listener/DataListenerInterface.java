package com.beeblebroxlabs.storytime.presentation.listener;

import com.beeblebroxlabs.storytime.logic.Story;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import java.util.List;

/**
 * Created by devgr on 03-Mar-18.
 */

public interface DataListenerInterface {
  void onSuccess(DataSnapshot dataSnapshot);
  void onFailure(DatabaseError databaseError);

}
