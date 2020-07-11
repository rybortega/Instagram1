package com.codepath.teleroid.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import com.bumptech.glide.Glide;
import com.codepath.teleroid.R;
import com.codepath.teleroid.databinding.ActivityPostDetailsBinding;
import com.codepath.teleroid.fragments.CreateFragment;
import com.codepath.teleroid.fragments.HomeFragment;
import com.codepath.teleroid.fragments.ProfileFragment;
import com.codepath.teleroid.models.Post;
import com.codepath.teleroid.utilities.DateUtility;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.parse.ParseFile;
import com.parse.ParseUser;

import org.parceler.Parcels;

public class PostDetailsActivity extends AppCompatActivity {

    public static final String TAG = PostDetailsActivity.class.getSimpleName(); //logging purposes

    private ActivityPostDetailsBinding binding;
    private BottomNavigationView bottomMenu;

    private Post selectedPost;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPostDetailsBinding.inflate(getLayoutInflater());
        View mainView = binding.getRoot();
        setContentView(mainView);

        //BOTTOM NAVIGATION
        bottomMenu = findViewById(R.id.bottom_toolbar);
        // Set default selection
        bottomMenu.setSelectedItemId(R.id.actionProfile);
        //Bottom Menu options listener TODO: Make bottom navigation work from details activity
        bottomMenu.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment fragment;
                switch (item.getItemId()) {
                    case R.id.actionHome:
                        fragment = new HomeFragment();
                        break;
                    case R.id.actionCreate:
                        fragment = new CreateFragment();
                        break;
                    case R.id.actionProfile:
                        fragment = new ProfileFragment(ParseUser.getCurrentUser());
                        break;
                    default:
                        bottomMenu.setSelectedItemId(R.id.actionHome);
                        fragment = new CreateFragment(); //TODO: better logic for default case.
                        break;
                }
                //fragmentManager.beginTransaction().replace(binding.frameContainer.getId(), fragment).commit();
                return true;
            }
        });

        //Getting parcel delivered from last Activity
        selectedPost = (Post) Parcels.unwrap(getIntent().getParcelableExtra("post_object"));
        Log.d(TAG, "Showing details for: " + selectedPost.getCaption());

        //Setting Views to corresponding post
        binding.postCaption.setText(selectedPost.getCaption());
        binding.profileHandle.setText(selectedPost.getUser().getUsername());

        String relativeDate = DateUtility.getRelativeTimeAgo(selectedPost);
        binding.timestamp.setText(relativeDate);


        ParseFile image = selectedPost.getImage();
        if(image != null){
            Glide.with(this)
                    .load(selectedPost.getImage().getUrl())
                    .into(binding.postPhoto);
        }

    }
}