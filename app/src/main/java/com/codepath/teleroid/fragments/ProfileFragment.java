package com.codepath.teleroid.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.codepath.teleroid.MainActivity;
import com.codepath.teleroid.PostDetailsActivity;
import com.codepath.teleroid.R;
import com.codepath.teleroid.adapters.DetailedPostsAdapter;
import com.codepath.teleroid.adapters.MinimalPostsAdapter;
import com.codepath.teleroid.models.Post;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

public class ProfileFragment extends HomeFragment{

    public static final String TAG = ProfileFragment.class.getSimpleName(); //logging purposes

    private MinimalPostsAdapter minimalPostsAdapter;
    private int lastVisitedPost;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        posts = new ArrayList<>();

        //ADAPTER

        //Post preview listener --> Details
        MinimalPostsAdapter.OnClickListener onClickListener = new MinimalPostsAdapter.OnClickListener() {
            @Override
            public void onItemClick(int position) {

                Intent postDetailsIntent = new Intent(getContext(),
                        PostDetailsActivity.class);

                /* Storing position so we can refresh recycle view
                and smooth scroll back to where we were */
                lastVisitedPost = position;

                //Passing data to the intent
                postDetailsIntent.putExtra("post_object", Parcels.wrap(posts.get(position)));
                startActivity(postDetailsIntent);
            }
        };

        minimalPostsAdapter = new MinimalPostsAdapter(getContext(), posts, onClickListener);
        binding.postsRecycler.setAdapter(minimalPostsAdapter);
        binding.postsRecycler.setLayoutManager(new GridLayoutManager(getContext(), 3));
        queryPosts();

        //REFRESHER

        //Swipe Refresh loading indicator colors
        binding.swipeRefreshContainer.setColorSchemeResources(
                R.color.colorAccent,
                R.color.colorPrimary,
                R.color.colorPrimaryDark,
                R.color.colorSecondary);

        //Swipe Refresh listener
        binding.swipeRefreshContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Log.d(TAG, "Refresh: Fetching fresh data!");

                //Clears and repopulates
                queryPosts();

                //Turn off the reload signal
                binding.swipeRefreshContainer.setRefreshing(false);
            }
        });
    }

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
                posts.clear();
                posts.addAll(newPosts);
                minimalPostsAdapter.notifyDataSetChanged();
            }
        });
    }
}
