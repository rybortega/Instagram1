package com.codepath.teleroid.fragments;

import android.util.Log;

import com.codepath.teleroid.models.Post;
import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.List;

public class ProfileFragment extends HomeFragment{

    public static final String TAG = ProfileFragment.class.getSimpleName(); //logging purposes

    @Override
    protected void queryPosts() {
        ParseQuery<Post> query = ParseQuery.getQuery(Post.class);
        query.include(Post.KEY_USER);
        query.whereEqualTo(Post.KEY_USER, ParseUser.getCurrentUser());
        query.setLimit(20); //Limits number of results.
        query.addDescendingOrder(Post.KEY_TIME);
        query.findInBackground(new FindCallback<Post>() {
            @Override
            public void done(List<Post> newPosts, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Couldn't query posts: " + e);
                    return;
                }
                Log.i(TAG, "Posts retrieved successfully!");
                posts.addAll(newPosts);
                adapter.notifyDataSetChanged();
            }
        });
    }
}
