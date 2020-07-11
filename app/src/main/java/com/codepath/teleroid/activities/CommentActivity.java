package com.codepath.teleroid.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.codepath.teleroid.R;
import com.codepath.teleroid.adapters.CommentsAdapter;
import com.codepath.teleroid.adapters.DetailedPostsAdapter;
import com.codepath.teleroid.databinding.ActivityCommentBinding;
import com.codepath.teleroid.databinding.ActivityMainBinding;
import com.codepath.teleroid.models.Comment;
import com.codepath.teleroid.models.Post;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

public class CommentActivity extends AppCompatActivity {

    private static final String TAG = CommentActivity.class.getSimpleName(); //logging purposes

    private ActivityCommentBinding binding;
    private CommentsAdapter commentsAdapter;
    private LinearLayoutManager linearLayoutManager;

    private Post post;
    private List<Comment> commentsInPost;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);
        binding = ActivityCommentBinding.inflate(getLayoutInflater());
        View mainView = binding.getRoot();
        setContentView(mainView);

        //Getting data through parcel from previous activity
        post = Parcels.unwrap(getIntent().getParcelableExtra("post_object"));
        commentsInPost = Parcels.unwrap(getIntent().getParcelableExtra("comments_array"));

        //Set adapter
        commentsAdapter = new CommentsAdapter(CommentActivity.this, commentsInPost);
        linearLayoutManager = new LinearLayoutManager(CommentActivity.this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        binding.commentsRecycler.setAdapter(commentsAdapter);
        binding.commentsRecycler.setLayoutManager(linearLayoutManager);

        //Set data into views
        binding.postCaption.setText(post.getCaption());

    }

    private void appendComment(Post post){

        Comment newComment = new Comment();
        newComment.setAuthor(ParseUser.getCurrentUser());
        newComment.setTarget(post);
        newComment.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Error making comment: " + e);
                    Toast.makeText(CommentActivity.this, "Error leaving comment", Toast.LENGTH_SHORT);
                    return;
                }

                Log.e(TAG, "User commented on a post!");
            }
        });

    }
}