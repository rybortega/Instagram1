package com.codepath.teleroid.login;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.codepath.teleroid.databinding.ActivityRegisterBinding;
import com.codepath.teleroid.utilities.AlertUtilities;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

public class RegisterActivity extends AppCompatActivity {

  private static final String TAG = RegisterActivity.class.getSimpleName(); // logging purposes

  private ProgressDialog progressDialog;

  private ActivityRegisterBinding binding;

  AnimationDrawable animationDrawable;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    binding = ActivityRegisterBinding.inflate(getLayoutInflater());
    View loginView = binding.getRoot();
    setContentView(loginView);

    // Start background animation
    animationDrawable = (AnimationDrawable) binding.container.getBackground();
    animationDrawable.setEnterFadeDuration(2000);
    animationDrawable.setExitFadeDuration(5000);

    progressDialog = new ProgressDialog(RegisterActivity.this);

    binding.registerButton.setOnClickListener(
        new View.OnClickListener() {
          @Override
          public void onClick(View view) {
            Log.i(TAG, "Button Press: Register Attempt");
            try {
              String username = binding.usernameField.getText().toString();
              String password = binding.passwordField.getText().toString();

              progressDialog.setMessage("Please Wait");
              progressDialog.setTitle("Registering");
              progressDialog.show();
              registerUser(username, password);
            } catch (Exception e) {
              Log.e(TAG, "Error initiating registration: " + e);
            }
          }
        });

    binding.signupButton.setOnClickListener(
        new View.OnClickListener() {
          @Override
          public void onClick(View view) {
            Log.i(TAG, "Button Press: Starting intent to login");
            startLoginActivity();
            finish();
          }
        });
  }

  private void registerUser(final String username, final String password) {
    if (ParseUser.getCurrentUser() != null) {
      ParseUser.getCurrentUser().logOut();
    }
    ParseUser user = new ParseUser();
    user.setUsername(username);
    user.setPassword(password);
    user.signUpInBackground(
        new SignUpCallback() {
          @Override
          public void done(ParseException e) {
            if (e == null) {
              progressDialog.dismiss();
              AlertUtilities.alertDisplayer(
                  "Register Successful", "Please login to " + username, RegisterActivity.this);
              startLoginActivity();
              finish();
            } else {
              progressDialog.dismiss();
              AlertUtilities.alertDisplayer(
                  "Registration Fail", e.getMessage() + " Please Try Again", RegisterActivity.this);
            }
          }
        });
  }

  private void startLoginActivity() {
    Intent loginActivity = new Intent(this, LoginActivity.class);
    startActivity(loginActivity);
  }

  @Override
  protected void onResume() {
    super.onResume();
    if (animationDrawable != null && !animationDrawable.isRunning()) animationDrawable.start();
  }

  @Override
  protected void onPause() {
    super.onPause();
    if (animationDrawable != null && animationDrawable.isRunning()) animationDrawable.stop();
  }
}
