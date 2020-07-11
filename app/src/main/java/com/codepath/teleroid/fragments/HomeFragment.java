package com.codepath.teleroid.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.codepath.teleroid.R;
import com.codepath.teleroid.adapters.DetailedPostsAdapter;
import com.codepath.teleroid.databinding.FragmentHomeBinding;
import com.codepath.teleroid.models.Comment;
import com.codepath.teleroid.models.Like;
import com.codepath.teleroid.models.Post;
import com.codepath.teleroid.utilities.EndlessRecyclerViewScrollListener;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {

    public static final String TAG = HomeFragment.class.getSimpleName(); //logging purposes

    public static final int NUMBER_OF_POSTS_TO_LOAD = 4;

    private FragmentHomeBinding binding;

    private List<Post> posts;
    private DetailedPostsAdapter detailedPostsAdapter;
    private EndlessRecyclerViewScrollListener scrollListener;
    private LinearLayoutManager linearLayoutManager;


    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(getLayoutInflater(), container, false);
        View view = binding.getRoot();
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        posts = new ArrayList<>();
        detailedPostsAdapter = new DetailedPostsAdapter(getContext(), posts);
        linearLayoutManager = new LinearLayoutManager(getContext());
        binding.postsRecycler.setAdapter(detailedPostsAdapter);
        binding.postsRecycler.setLayoutManager(linearLayoutManager);


        queryPosts();

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

                //Reset endless scroller state
                scrollListener.resetState();

                //Turn off the reload signal
                binding.swipeRefreshContainer.setRefreshing(false);
            }
        });

        scrollListener = new EndlessRecyclerViewScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                // Triggered only when new data needs to be appended to the list
                // Add whatever code is needed to append new items to the bottom of the list
                Log.i(TAG, "Gotta retrieve more! To page: " + page);
                queryMorePosts(page);
            }
        };
        // Adds the scroll listener to RecyclerView
        binding.postsRecycler.addOnScrollListener(scrollListener);
    }

    protected void queryPosts() {
        Log.i(TAG, "Retrieving posts...");
        ParseQuery<Post> query = ParseQuery.getQuery(Post.class);
        query.include(Post.KEY_USER);
        query.setLimit(NUMBER_OF_POSTS_TO_LOAD); //Limits number of results.
        query.addDescendingOrder(Post.KEY_TIME);
        query.findInBackground(new FindCallback<Post>() {
            @Override
            public void done(List<Post> newPosts, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Couldn't query posts: " + e);
                    return;
                }

                //Fetch likes and comments
                for(Post post : newPosts) {
                    fetchLikes(post);
                    fetchComments(post);
                }

                Log.i(TAG, "Posts retrieved successfully!");
                detailedPostsAdapter.clear();
                detailedPostsAdapter.addAll(newPosts);
            }
        });
    }

    public void queryMorePosts(int offset) {
        Log.i(TAG, "Retrieving MORE posts...");

        ParseQuery<Post> query = ParseQuery.getQuery(Post.class);
        query.include(Post.KEY_USER);
        query.whereLessThan(Post.KEY_TIME, posts.get(posts.size()-1).getCreatedAt());
        query.setLimit(NUMBER_OF_POSTS_TO_LOAD);
        query.addDescendingOrder(Post.KEY_TIME);
        query.findInBackground(new FindCallback<Post>() {
            @Override
            public void done(List<Post> morePosts, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Couldn't query posts: " + e);
                    return;
                }

                //Fetch likes and comments
                for(Post post : morePosts) {
                    fetchLikes(post);
                    fetchComments(post);

                }

                Log.i(TAG, "Posts retrieved successfully!");
                detailedPostsAdapter.addAll(morePosts);
            }
        });
    }

    public void fetchLikes(final Post post){
        Log.i(TAG, "Trying to retrieve likes");
        Log.i(TAG, "Retrieving likes for: " + post);
        ParseQuery<Like> query = ParseQuery.getQuery(Like.class);
        query.addDescendingOrder(Like.KEY_TIME);
        query.whereEqualTo(Like.KEY_TARGET, post);
        query.findInBackground(new FindCallback<Like>() {
            @Override
            public void done(List<Like> likes, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Couldn't query posts: " + e);
                    return;
                }
                post.setLikes(likes);
                Log.i(TAG, "Likes for: " + post.getCaption() + " are " + post.getLikes().toString());
            }
        });
    }

    private void fetchComments(final Post post) {
        Log.i(TAG, "Trying to retrieve comments");
        Log.i(TAG, "Retrieving comments for: " + post);
        ParseQuery<Comment> query = ParseQuery.getQuery(Comment.class);
        query.addDescendingOrder(Comment.KEY_TIME);
        query.whereEqualTo(Comment.KEY_TARGET, post);
        query.findInBackground(new FindCallback<Comment>() {
            @Override
            public void done(List<Comment> comments, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Couldn't query posts: " + e);
                    return;
                }
                post.setComments(comments);
                Log.i(TAG, "Comments for: " + post.getCaption() + " are " + post.getComments().toString());
            }
        });
    }
}