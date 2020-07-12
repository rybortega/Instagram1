package com.codepath.teleroid.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.codepath.teleroid.databinding.ItemPreviewPostBinding;
import com.codepath.teleroid.models.Post;
import com.parse.ParseFile;

import java.util.List;

public class PreviewPostsAdapter extends RecyclerView.Adapter<PreviewPostsAdapter.ViewHolder> {

  protected Context context;
  protected List<Post> posts;

  private OnClickListener clickListener;

  public interface OnClickListener {
    void onItemClick(int position);
  }

  public PreviewPostsAdapter(Context context, List<Post> posts, OnClickListener clickListener) {
    this.context = context;
    this.posts = posts;
    this.clickListener = clickListener;
  }

  // Clean all elements of the recycler
  public void clear() {
    posts.clear();
    notifyDataSetChanged();
  }

  @NonNull
  @Override
  public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    ItemPreviewPostBinding binding =
        ItemPreviewPostBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
    return new ViewHolder(binding);
  }

  @Override
  public int getItemCount() {
    return posts.size();
  }

  @Override
  public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
    Post post = posts.get(position);
    holder.bind(post);
  }

  class ViewHolder extends RecyclerView.ViewHolder {

    ItemPreviewPostBinding binding;

    public ViewHolder(@NonNull ItemPreviewPostBinding binding) {
      super(binding.getRoot());
      this.binding = binding;
    }

    public void bind(Post post) {
      ParseFile image = post.getImage();
      if (image != null) {
        Glide.with(context).load(post.getImage().getUrl()).into(binding.postPhoto);
      }

      // LISTENERS
      // Post item itself
      binding.postPreview.setOnClickListener(
          new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              /*Sets listener on actual tweet item -> get position
              and initiate detail-activity from timeline*/
              clickListener.onItemClick(getAdapterPosition());
            }
          });
    }
  }
}
