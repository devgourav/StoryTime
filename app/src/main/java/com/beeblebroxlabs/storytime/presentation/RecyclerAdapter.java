package com.beeblebroxlabs.storytime.presentation;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.beeblebroxlabs.storytime.R;
import com.beeblebroxlabs.storytime.logic.Story;
import com.beeblebroxlabs.storytime.presentation.activity.ReadStoryActivity;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.github.ybq.android.spinkit.style.DoubleBounce;
import com.github.ybq.android.spinkit.style.Wave;
import com.google.firebase.database.Query;
import com.google.firebase.storage.StorageReference;

/**
 * Created by devgr on 01-Mar-18.
 */

public class RecyclerAdapter extends FirebaseRecyclerAdapter<Story,RecyclerAdapter.ViewHolder> {
  private Context context;
  private StorageReference mStoryImageStorageReference;

  public RecyclerAdapter(
      @NonNull FirebaseRecyclerOptions<Story> options, Context context,
      StorageReference mStoryImageStorageReference) {
    super(options);
    this.context = context;
    this.mStoryImageStorageReference = mStoryImageStorageReference;
  }

  /**
   * Initialize a {@link RecyclerView.Adapter} that listens to a Firebase query. See
   * {@link FirebaseRecyclerOptions} for configuration options.
   */
  public RecyclerAdapter(@NonNull FirebaseRecyclerOptions options) {
    super(options);
  }

  @Override
  protected void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull Story model) {
    ImageView storyImageView = holder.storyImage;
    TextView titleTextView = holder.titleText;
    ProgressBar progressBar = holder.progressBar;

    if(model.getPhotoUrl() != null){
      StorageReference storageReference = mStoryImageStorageReference.child(model.getPhotoUrl());
      Glide.with(context)
          .load(storageReference)
          .listener(new RequestListener<Drawable>() {
            @Override
            public boolean onLoadFailed(@Nullable GlideException e, Object model,
                Target<Drawable> target,
                boolean isFirstResource) {
              progressBar.setVisibility(View.GONE);
              return false;
            }

            @Override
            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target,
                DataSource dataSource, boolean isFirstResource) {
              progressBar.setVisibility(View.GONE);
              return false;
            }
          })
          .into(storyImageView);
    }else{
      storyImageView.setVisibility(View.INVISIBLE);
      titleTextView.setVisibility(View.INVISIBLE);
    }
    titleTextView.setText(model.getTitle());
    storyImageView.setOnHoverListener((view, motionEvent) -> false);
    storyImageView.setOnClickListener(view -> {
      Intent intent = new Intent(context,ReadStoryActivity.class);
      intent.putExtra("storyId",model.getId());
      context.startActivity(intent);
    });
  }

  @Override
  public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View view = LayoutInflater.from(parent.getContext())
        .inflate(R.layout.story_row_view, parent, false);

    return new ViewHolder(view);
  }


  public class ViewHolder extends RecyclerView.ViewHolder{

    @BindView(R.id.storyImage)
    ImageView storyImage;

    @BindView(R.id.titleText)
    TextView titleText;

    @BindView(R.id.item_load_spinKitView)
    ProgressBar progressBar;

    public ViewHolder(View itemView) {
      super(itemView);
      ButterKnife.bind(this,itemView);
      progressBar.setIndeterminateDrawable(new DoubleBounce());
    }
  }

}
