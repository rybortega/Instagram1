package com.codepath.teleroid.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.codepath.teleroid.PostDetailsActivity;
import com.codepath.teleroid.R;
import com.codepath.teleroid.adapters.MinimalPostsAdapter;
import com.codepath.teleroid.databinding.FragmentProfileBinding;
import com.codepath.teleroid.models.Post;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

public class ProfileFragment extends Fragment {

    public static final String TAG = ProfileFragment.class.getSimpleName(); //logging purposes

    protected FragmentProfileBinding binding;

    protected List<Post> posts;
    private MinimalPostsAdapter minimalPostsAdapter;
    private int lastVisitedPost;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(getLayoutInflater(), container, false);
        View view = binding.getRoot();
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        posts = new ArrayList<>();

        //Bind data from User that owns the profile
        bindProfileOwner(ParseUser.getCurrentUser());

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
                R.color.colorSecondary,
                R.color.colorPrimaryDark,
                R.color.colorPrimary,
                R.color.colorAccent);

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

    private void bindProfileOwner(ParseUser user) {
        binding.displayName.setText(user.getUsername());
    }

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
