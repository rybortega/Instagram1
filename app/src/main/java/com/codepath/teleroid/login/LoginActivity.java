package com.codepath.teleroid.login;

import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.codepath.teleroid.R;
import com.codepath.teleroid.activities.MainActivity;
import com.codepath.teleroid.databinding.ActivityLoginBinding;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

public class LoginActivity extends AppCompatActivity {

    public static final String TAG = LoginActivity.class.getSimpleName(); //logging purposes

    private ActivityLoginBinding binding;

    AnimationDrawable animationDrawable;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        View loginView = binding.getRoot();
        setContentView(loginView);

        //Start background animation
        animationDrawable = (AnimationDrawable) binding.container.getBackground();
        animationDrawable.setEnterFadeDuration(2000);
        animationDrawable.setExitFadeDuration(5000);

        if(ParseUser.getCurrentUser() != null){
            startMainActivity();
            finish();
        }

        binding.loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(TAG, "Button Press: Login Attempt");
                try{
                    String username = binding.usernameField.getText().toString();
                    String password = binding.passwordField.getText().toString();

                    loginUser(username, password);
                }
                catch (Exception e){
                    Log.e(TAG, "Error initiating login: " + e);
                }
            }
        });

        binding.registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(TAG, "Button Press: Starting intent to register");
                startRegisterActivity();
                finish();
            }
        });
    }

    private void loginUser(String username, String password) {
        Log.i(TAG, "Processing user credentials");
        ParseUser.logInInBackground(username, password, new LogInCallback() {
            @Override
            public void done(ParseUser user, ParseException e) {
                if (e != null){
                    //TODO: inform user
                    Log.e(TAG, "Error in login: ", e);
                    Toast.makeText(LoginActivity.this, "Login Failed", Toast.LENGTH_SHORT).show();
                    return;
                }
                //On successful login
                Toast.makeText(LoginActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();
                startMainActivity();
                finish();

            }
        });
    }

    private void startRegisterActivity() {
        Intent registerActivity = new Intent(this, RegisterActivity.class);
        startActivity(registerActivity);
    }

    private void startMainActivity() {
        Intent mainActivity = new Intent(this, MainActivity.class);
        startActivity(mainActivity);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (animationDrawable != null && !animationDrawable.isRunning())
            animationDrawable.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (animationDrawable != null && animationDrawable.isRunning())
            animationDrawable.stop();
    }

}


