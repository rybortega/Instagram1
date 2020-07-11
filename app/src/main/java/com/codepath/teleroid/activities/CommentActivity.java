package com.codepath.teleroid.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.bumptech.glide.load.engine.Resource;
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
        Resources resources = CommentActivity.this.getResources();
        String caption = resources.getString(R.string.caption_preview, post.getUser().getUsername(), post.getCaption());
        binding.postCaption.setText(caption);

        //Listener
        binding.postButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(binding.inputComment.getText().toString().trim().equals("") || binding.inputComment.getText() == null){
                    Toast.makeText(CommentActivity.this, "Comment can't be empty", Toast.LENGTH_SHORT).show();
                    return;
                }
                String newCommentBody = binding.inputComment.getText().toString();
                appendComment(post, newCommentBody);
            }
        });
    }

    private void appendComment(Post post, String body){
        final Comment newComment = new Comment();
        newComment.setAuthor(ParseUser.getCurrentUser());
        newComment.setTarget(post);
        newComment.setBody(body);
        newComment.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Error making comment: " + e);
                    Toast.makeText(CommentActivity.this, "Error leaving comment", Toast.LENGTH_SHORT);
                    return;
                }
                Log.e(TAG, "User commented on a post!");
                binding.inputComment.setText("");
                commentsInPost.add(0, newComment);
                commentsAdapter.notifyItemInserted(0);
            }
        });
    }
}