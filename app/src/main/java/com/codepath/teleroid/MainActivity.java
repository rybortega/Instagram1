package com.codepath.teleroid;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.codepath.teleroid.databinding.ActivityMainBinding;
import com.codepath.teleroid.models.Post;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private final String TAG = MainActivity.class.getSimpleName(); //logging purposes

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View loginView = binding.getRoot();
        setContentView(loginView);
    }

    private void queryPosts(){
        ParseQuery<Post> query = ParseQuery.getQuery(Post.class);
        query.include(Post.KEY_USER);
        query.findInBackground(new FindCallback<Post>() {
            @Override
            public void done(List<Post> posts, ParseException e) {
                if (e != null){
                    Log.e(TAG, "Couldn't query posts: " + e);
                    return;
                }
                for(Post post: posts) {
                    Log.i(TAG, "Post retrieved successfully: '" + post.getCaption()
                            + "', by username: " + post.getUser().getUsername());
                }
            }
        });
    }
}