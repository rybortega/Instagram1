package com.codepath.teleroid.adapters;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.codepath.teleroid.R;
import com.codepath.teleroid.activities.CommentActivity;
import com.codepath.teleroid.activities.SomeonesProfileActivity;
import com.codepath.teleroid.databinding.ItemPostBinding;
import com.codepath.teleroid.models.Comment;
import com.codepath.teleroid.models.Like;
import com.codepath.teleroid.models.Post;
import com.codepath.teleroid.utilities.DateUtility;
import com.parse.DeleteCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.parceler.Parcels;

import java.util.List;

public class DetailedPostsAdapter extends RecyclerView.Adapter<DetailedPostsAdapter.ViewHolder> {

  private static final String TAG = DetailedPostsAdapter.class.getSimpleName(); // logging purposes

  private Context context;
  private List<Post> posts;
  private OnClickListener clickListener;

  public interface OnClickListener {
    void onItemClick(int position);
  }

  public DetailedPostsAdapter(Context context, List<Post> posts, OnClickListener clickListener) {
    this.context = context;
    this.posts = posts;
    this.clickListener = clickListener;
  }

  // Clean all elements of the recycler
  public void clear() {
    posts.clear();
    notifyDataSetChanged();
  }

  // Add a list of items -- change to type used
  public void addAll(List<Post> newPosts) {
    posts.addAll(newPosts);
    notifyDataSetChanged();
    int i = 1;
    for (Post post : posts) {
      Log.d(TAG, "post #" + i + ": " + post.toString());
      i++;
    }
  }

  @NonNull
  @Override
  public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    ItemPostBinding binding =
        ItemPostBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
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

    ItemPostBinding binding;

    public ViewHolder(@NonNull ItemPostBinding binding) {
      super(binding.getRoot());
      this.binding = binding;
    }

    public void bind(final Post post) {

      Resources resources = context.getResources();

      String caption =
          resources.getString(
              R.string.caption_preview, post.getUser().getUsername(), post.getCaption());
      binding.postCaption.setText(caption);
      binding.profileHandle.setText(post.getUser().getUsername());

      // Timestamp
      String relativeDate = DateUtility.getRelativeTimeAgo(post);
      binding.timestamp.setText(relativeDate);

      // LIKES
      List<Like> likes = post.getLikes();
      Like currentUserLike = null;

      // Like status
      binding.actionLike.setSelected(false);
      for (Like like : likes) {
        if (like.getAuthor().getObjectId().equals(ParseUser.getCurrentUser().getObjectId())) {
          currentUserLike = like;
          binding.actionLike.setSelected(true);
        }
      }

      // Like button listener

      final Like finalCurrentUserLike = currentUserLike;
      binding.actionLike.setOnClickListener(
          new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              if (binding.actionLike.isSelected()) {
                try {
                  finalCurrentUserLike.deleteInBackground(
                      new DeleteCallback() {
                        @Override
                        public void done(ParseException e) {
                          if (e != null) {
                            Log.e(TAG, "Error unliking post: " + e);
                            Toast.makeText(context, "Error liking activity", Toast.LENGTH_SHORT);
                            return;
                          }

                          Log.e(TAG, "User unliked a post!");
                          binding.actionLike.setSelected(false);
                        }
                      });
                } catch (NullPointerException e) {
                  Log.e(TAG, "Error in unliking: " + e);
                }
              } else {
                Like newLike = new Like();
                newLike.setAuthor(ParseUser.getCurrentUser());
                newLike.setTarget(post);
                newLike.saveInBackground(
                    new SaveCallback() {
                      @Override
                      public void done(ParseException e) {
                        if (e != null) {
                          Log.e(TAG, "Error in liking post: " + e);
                          Toast.makeText(context, "Error liking activity", Toast.LENGTH_SHORT);
                          return;
                        }

                        Log.e(TAG, "User liked a post!");
                        binding.actionLike.setSelected(true);
                      }
                    });
              }
            }
          });

      // Like numbers
      int numOfLikes = likes.size();
      if (numOfLikes > 0) {
        Like singleLike = post.getLikes().get(0);
        ParseUser authorOfLike = singleLike.getAuthor();
        String nameAuthorOfLike = "";

        if (numOfLikes == 1) { // Only 1 like --> user likes this
          try {
            nameAuthorOfLike = authorOfLike.fetchIfNeeded().getString("username");
            binding.likesText.setVisibility(View.VISIBLE);

            String likeText = resources.getString(R.string.single_like, nameAuthorOfLike);
            binding.likesText.setText(likeText);
          } catch (Exception e) {
            Log.e(TAG, "Couldn't retrieve username: " + e);
            binding.likesText.setVisibility(View.GONE);
          }
        } else { // Multiple likes --> show 1 user and 'and # others like this'
          try {
            nameAuthorOfLike = authorOfLike.fetchIfNeeded().getString("username");
            binding.likesText.setVisibility(View.VISIBLE);

            String likeText =
                resources.getQuantityString(
                    R.plurals.multiple_likes, numOfLikes - 1, nameAuthorOfLike, numOfLikes - 1);
            binding.likesText.setText(likeText);
          } catch (Exception e) {
            Log.e(TAG, "Couldn't retrieve username: " + e);
            binding.likesText.setVisibility(View.GONE);
          }
        }
      } else { // No likes yet
        binding.likesText.setVisibility(View.GONE);
      }

      // COMMENTS
      List<Comment> comments = post.getComments();

      // Comment button listener
      binding.seeMoreButton.setOnClickListener(
          new View.OnClickListener() {
            @Override
            public void onClick(View view) {

              Intent commentOnPostIntent = new Intent(context, CommentActivity.class);

              commentOnPostIntent.putExtra("post_object", Parcels.wrap(post));
              commentOnPostIntent.putExtra("comments_array", Parcels.wrap(post.getComments()));
              context.startActivity(commentOnPostIntent);
            }
          });

      // Comment numbers
      int numOfComments = comments.size();
      if (numOfComments >= 1) {

        binding.commentPreview.setVisibility(View.VISIBLE);
        binding.seeMoreButton.setVisibility(View.VISIBLE);

        Comment singleComment = post.getComments().get(0);
        String bodyOfComment = singleComment.getBody();

        ParseUser authorOfComment = singleComment.getAuthor();
        String nameAuthorOfComment;
        try {
          nameAuthorOfComment = authorOfComment.fetchIfNeeded().getString("username");
        } catch (ParseException e) {
          e.printStackTrace();
          nameAuthorOfComment = "";
          binding.commentPreview.setVisibility(View.GONE);
        }

        String commentText =
            resources.getString(R.string.comment_preview, nameAuthorOfComment, bodyOfComment);
        binding.commentPreview.setText(commentText);

        String viewAllCommentsText = resources.getString(R.string.view_all_comments, numOfComments);
        binding.seeMoreButton.setText(viewAllCommentsText);

        if (numOfComments == 1) {
          binding.seeMoreButton.setVisibility(View.GONE);
        }

      } else {
        binding.commentPreview.setVisibility(View.GONE);
        binding.seeMoreButton.setVisibility(View.GONE);
      }

      // User's profile picture
      ParseFile userProfilePicture = post.getUser().getParseFile("profilePicture");
      if (userProfilePicture != null) {
        Glide.with(context)
            .load(userProfilePicture.getUrl())
            .circleCrop()
            .into(binding.profilePicture);
      } else {
        Log.e(TAG, "No profile picture found");
        Glide.with(context)
            .load(R.drawable.default_profile_picture)
            .circleCrop()
            .into(binding.profilePicture);
      }

      // Post's image
      ParseFile postPhoto = post.getImage();
      if (postPhoto != null) {
        Glide.with(context).load(postPhoto.getUrl()).into(binding.postPhoto);
      }

      // LISTENERS

      binding.userContainer.setOnClickListener(
          new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              Intent someonesProfileIntent = new Intent(context, SomeonesProfileActivity.class);

              // Passing data to the intent
              someonesProfileIntent.putExtra("post_object", Parcels.wrap(post));

              context.startActivity(someonesProfileIntent);
            }
          });
    }
  }
}
