package com.codepath.teleroid.login;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.codepath.teleroid.MainActivity;
import com.codepath.teleroid.databinding.ActivityLoginBinding;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

public class LoginActivity extends AppCompatActivity {

    public static final String TAG = LoginActivity.class.getSimpleName(); //logging purposes

    private ActivityLoginBinding binding;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        View loginView = binding.getRoot();
        setContentView(loginView);

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

}


