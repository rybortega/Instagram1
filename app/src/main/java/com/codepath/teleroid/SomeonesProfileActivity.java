package com.codepath.teleroid;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.util.Log;

import com.codepath.teleroid.fragments.ProfileFragment;
import com.codepath.teleroid.models.Post;
import com.parse.ParseUser;

import org.parceler.Parcels;

public class SomeonesProfileActivity extends MainActivity {

    ParseUser someUser;
    Post usersPost;
    Fragment profileFragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Getting parcel delivered from last Activity
        usersPost = (Post) Parcels.unwrap(getIntent().getParcelableExtra("post_object"));
        someUser = usersPost.getUser();
        Log.d(TAG, "Showing profile for: " + someUser.getUsername());

        profileFragment = new ProfileFragment(someUser);
        fragmentManager.beginTransaction().replace(binding.frameContainer.getId(), profileFragment).commit();
    }
}