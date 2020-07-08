package com.codepath.teleroid.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.codepath.teleroid.R;
import com.codepath.teleroid.adapters.DetailedPostsAdapter;
import com.codepath.teleroid.databinding.FragmentHomeBinding;
import com.codepath.teleroid.models.Post;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {

    public static final String TAG = HomeFragment.class.getSimpleName(); //logging purposes

    private FragmentHomeBinding binding;

    private List<Post> posts;
    private DetailedPostsAdapter detailedPostsAdapter;

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

        binding.postsRecycler.setAdapter(detailedPostsAdapter);
        binding.postsRecycler.setLayoutManager(new LinearLayoutManager(getContext()));

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

                //Turn off the reload signal
                binding.swipeRefreshContainer.setRefreshing(false);
            }
        });
    }

    protected void queryPosts() {
        ParseQuery<Post> query = ParseQuery.getQuery(Post.class);
        query.include(Post.KEY_USER);
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
                detailedPostsAdapter.clear();
                posts.addAll(newPosts);
                detailedPostsAdapter.notifyDataSetChanged();
            }
        });
    }
}