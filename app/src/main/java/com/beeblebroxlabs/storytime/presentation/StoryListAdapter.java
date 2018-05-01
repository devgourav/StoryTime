package com.beeblebroxlabs.storytime.presentation;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.beeblebroxlabs.storytime.R;
import com.beeblebroxlabs.storytime.logic.Story;
import com.beeblebroxlabs.storytime.presentation.activity.ReadStoryActivity;
import com.bumptech.glide.Glide;
import com.google.firebase.storage.StorageReference;
import java.util.List;

/**
 * Created by devgr on 01-Mar-18.
 */

public class StoryListAdapter extends RecyclerView.Adapter<StoryListAdapter.ViewHolder> {


  Context context;
  List<Story> storyList;
  private StorageReference mStoryImageStorageReference;


  public StoryListAdapter(Context context,
      List<Story> storyList, StorageReference mStoryImageStorageReference) {
    this.context = context;
    this.storyList = storyList;
    this.mStoryImageStorageReference = mStoryImageStorageReference;
  }

  public class ViewHolder extends RecyclerView.ViewHolder{

    @BindView(R.id.storyImage)
    ImageView storyImage;

    @BindView(R.id.titleText)
    TextView titleText;

    public ViewHolder(View itemView) {
      super(itemView);
      ButterKnife.bind(this,itemView);
    }
  }

  @Override
  public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View view = LayoutInflater.from(parent.getContext())
        .inflate(R.layout.story_row_view, parent, false);


    return new ViewHolder(view);
  }

  @Override
  public void onBindViewHolder(ViewHolder holder, int position) {
    ImageView storyImageView = holder.storyImage;
    TextView titleTextView = holder.titleText;



    Story story = storyList.get(position);

    if(story.getPhotoUrl() != null){
      StorageReference storageReference = mStoryImageStorageReference.child(story.getPhotoUrl());
      Glide.with(context)
          .load(storageReference)
          .into(storyImageView);
    }else{
      storyImageView.setVisibility(View.INVISIBLE);
    }
    titleTextView.setText(story.getTitle());


    storyImageView.setOnHoverListener((view, motionEvent) -> false);
    storyImageView.setOnClickListener(view -> {
      Intent intent = new Intent(context,ReadStoryActivity.class);
      intent.putExtra("storyId",story.getId());
      context.startActivity(intent);
    });
  }

  @Override
  public int getItemCount() {
    return storyList.size();
  }

  public void add(int position, Story story) {
    storyList.add(position, story);
    notifyItemInserted(position);
  }

  public void remove(int position) {
    storyList.remove(position);
    notifyItemRemoved(position);
  }



}
