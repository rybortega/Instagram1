package com.codepath.teleroid;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.codepath.teleroid.databinding.ActivityMainBinding;
import com.codepath.teleroid.fragments.CreateFragment;
import com.codepath.teleroid.fragments.HomeFragment;
import com.codepath.teleroid.fragments.ProfileFragment;
import com.codepath.teleroid.login.LoginActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.parse.LogOutCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = MainActivity.class.getSimpleName(); //logging purposes

    private ActivityMainBinding binding;
    private BottomNavigationView bottomMenu;

    final FragmentManager fragmentManager = getSupportFragmentManager();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View mainView = binding.getRoot();
        setContentView(mainView);
        setSupportActionBar(binding.toolbar);
        bottomMenu = findViewById(R.id.bottom_toolbar);

        //Bottom Menu options listener
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
                        fragment = new ProfileFragment();
                        break;
                    default:
                        bottomMenu.setSelectedItemId(R.id.actionHome);
                        fragment = new CreateFragment(); //TODO: better logic for default case.
                        break;
                }
                fragmentManager.beginTransaction().replace(binding.frameContainer.getId(), fragment).commit();
                return true;
            }
        });
        // Set default selection
        bottomMenu.setSelectedItemId(R.id.actionHome);
    }

    // Inflation of Menu icons for toolbar and setup of listeners;
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_top_navigation, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.logoutButton:
                logoutUser();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    //LOGOUT
    private void logoutUser() {
        final String logoutMessage = "Logged out from " + ParseUser.getCurrentUser().getUsername();
        Log.i(TAG, "Logging out user...");
        ParseUser.logOutInBackground(new LogOutCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null){
                    Log.e(TAG, "Error in login: ", e);
                    Toast.makeText(MainActivity.this, "Error logging out", Toast.LENGTH_SHORT).show();
                    return;
                }
                //On successful login
                Toast.makeText(MainActivity.this, logoutMessage, Toast.LENGTH_SHORT).show();
                backToLoginActivity();
                finish();
            }
        });
    }

    private void backToLoginActivity() {
        Intent loginActivity = new Intent(this, LoginActivity.class);
        startActivity(loginActivity);
    }
}


