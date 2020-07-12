package com.codepath.teleroid.adapters;

import android.content.Context;
import android.content.res.Resources;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.codepath.teleroid.R;
import com.codepath.teleroid.activities.CommentActivity;
import com.codepath.teleroid.databinding.ItemCommentBinding;
import com.codepath.teleroid.databinding.ItemPostBinding;
import com.codepath.teleroid.models.Comment;
import com.codepath.teleroid.models.Post;
import com.parse.ParseException;

import java.util.List;

public class CommentsAdapter extends RecyclerView.Adapter<CommentsAdapter.ViewHolder> {

  private static final String TAG = CommentsAdapter.class.getSimpleName(); // logging purposes

  private Context context;
  private List<Comment> comments;

  public CommentsAdapter(Context context, List<Comment> comments) {
    this.context = context;
    this.comments = comments;
  }

  @NonNull
  @Override
  public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    ItemCommentBinding binding =
        ItemCommentBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
    return new CommentsAdapter.ViewHolder(binding);
  }

  @Override
  public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
    Comment comment = comments.get(position);
    holder.bind(comment);
  }

  @Override
  public int getItemCount() {
    return comments.size();
  }

  class ViewHolder extends RecyclerView.ViewHolder {

    ItemCommentBinding binding;

    public ViewHolder(@NonNull ItemCommentBinding binding) {
      super(binding.getRoot());
      this.binding = binding;
    }

    public void bind(Comment comment) {
      Resources resources = context.getResources();
      String commentBody = null;
      try {
        commentBody =
            resources.getString(
                R.string.comment_preview,
                comment.getAuthor().fetchIfNeeded().getUsername(),
                comment.getBody());
      } catch (ParseException e) {
        Log.e(TAG, "Couldn't fetch comment");
        commentBody = "";
      }
      binding.body.setText(commentBody);
    }
  }
}
