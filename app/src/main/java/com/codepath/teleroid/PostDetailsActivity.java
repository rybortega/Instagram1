package com.codepath.teleroid;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

import com.codepath.teleroid.fragments.ProfileFragment;
import com.codepath.teleroid.models.Post;

import org.parceler.Parcels;

public class PostDetailsActivity extends AppCompatActivity {

    public static final String TAG = PostDetailsActivity.class.getSimpleName(); //logging purposes

    private Post selectedPost;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_details);

        //Getting parcel delivered from last Activity
        selectedPost = (Post) Parcels.unwrap(getIntent().getParcelableExtra("post_object"));

        Log.d(TAG, "Parcel: " + selectedPost.getCaption());
    }
}