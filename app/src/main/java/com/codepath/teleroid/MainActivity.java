package com.codepath.teleroid;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.codepath.teleroid.databinding.ActivityMainBinding;
import com.codepath.teleroid.fragments.CreateFragment;
import com.codepath.teleroid.fragments.HomeFragment;
import com.codepath.teleroid.models.Post;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName(); //logging purposes

    private ActivityMainBinding binding;
    private BottomNavigationView bottomMenu;

    final FragmentManager fragmentManager = getSupportFragmentManager();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View mainView = binding.getRoot();
        setContentView(mainView);
        bottomMenu = binding.bottomMenu;

        bottomMenu.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment fragment;
                switch (item.getItemId()) {
                    case R.id.actionHome:
                        Toast.makeText(MainActivity.this, "Home!", Toast.LENGTH_SHORT).show();
                        fragment = new HomeFragment(); //TODO: create appropriate Home fragment
                        break;
                    case R.id.actionCreate:
                        Toast.makeText(MainActivity.this, "Create!", Toast.LENGTH_SHORT).show();
                        fragment = new CreateFragment();
                        break;
                    case R.id.actionProfile:
                        Toast.makeText(MainActivity.this, "Profile!", Toast.LENGTH_SHORT).show();
                        fragment = new CreateFragment(); //TODO: create appropriate Profile fragment
                        break;
                    default:
                        bottomMenu.setSelectedItemId(R.id.actionHome);
                        fragment = new CreateFragment(); //TODO: better logic for defaul case.
                        Toast.makeText(MainActivity.this, "Default: Home?", Toast.LENGTH_SHORT).show();
                        break;
                }
                fragmentManager.beginTransaction().replace(binding.frameContainer.getId(), fragment).commit();
                return true;
            }
        });
        // Set default selection
        binding.bottomMenu.setSelectedItemId(R.id.actionHome);
    }
}


