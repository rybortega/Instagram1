package com.codepath.teleroid.utilities;

import android.content.Context;
import android.content.DialogInterface;

import androidx.appcompat.app.AlertDialog;

import com.codepath.teleroid.login.RegisterActivity;

public class AlertUtilities {

  public static void alertDisplayer(String title, String message, Context context) {
    AlertDialog.Builder builder =
        new AlertDialog.Builder(context)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton(
                "OK",
                new DialogInterface.OnClickListener() {
                  @Override
                  public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                  }
                });
    AlertDialog ok = builder.create();
    ok.show();
  }
}
