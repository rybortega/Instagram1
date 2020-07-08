package com.codepath.teleroid.login;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.codepath.teleroid.MainActivity;
import com.codepath.teleroid.databinding.ActivityRegisterBinding;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

public class RegisterActivity extends AppCompatActivity {

    private static final String TAG = RegisterActivity.class.getSimpleName(); //logging purposes

    private ProgressDialog progressDialog;

    private ActivityRegisterBinding binding;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        View loginView = binding.getRoot();
        setContentView(loginView);

        progressDialog = new ProgressDialog(RegisterActivity.this);

        binding.registerButton.setOnClickListener(new View.OnClickListener() {
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
                }
                catch (Exception e){
                    Log.e(TAG, "Error initiating registration: " + e);
                }
            }
        });

        binding.signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(TAG, "Button Press: Starting intent to login");
                startLoginActivity();
                finish();
            }
        });
    }

    private void registerUser(final String username, final String password){
        if (ParseUser.getCurrentUser() != null) {
            ParseUser.getCurrentUser().logOut();
        }
        ParseUser user = new ParseUser();
        user.setUsername(username);
        user.setPassword(password);
        user.signUpInBackground(new SignUpCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    progressDialog.dismiss();
                    alertDisplayer("Register Successful", "Please login to " + username);
                    startLoginActivity();
                    finish();
                } else {
                    progressDialog.dismiss();
                    alertDisplayer("Registration Fail", e.getMessage()+" Please Try Again");
                }
            }
        });
    }

    private void alertDisplayer(String title, String message){
        AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        AlertDialog ok = builder.create();
        ok.show();
    }

    private void startLoginActivity() {
        Intent loginActivity = new Intent(this, LoginActivity.class);
        startActivity(loginActivity);
    }
}