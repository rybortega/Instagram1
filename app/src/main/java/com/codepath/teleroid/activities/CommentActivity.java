package com.codepath.teleroid.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.codepath.teleroid.R;
import com.codepath.teleroid.adapters.DetailedPostsAdapter;
import com.codepath.teleroid.models.Comment;
import com.codepath.teleroid.models.Post;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;

public class CommentActivity extends AppCompatActivity {

    private static final String TAG = CommentActivity.class.getSimpleName(); //logging purposes

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);
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