package com.codepath.teleroid.utilities;

import android.text.format.DateUtils;
import android.util.Log;

import com.codepath.teleroid.models.Post;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DateUtility {

  public static String getRelativeTimeAgo(Post post) {

    // Fetching timestamp on post from Parse
    Date createdAt = post.getCreatedAt();

    String relativeDate = "";
    try {
      long dateMillis = createdAt.getTime();
      relativeDate =
          DateUtils.getRelativeTimeSpanString(
                  dateMillis,
                  System.currentTimeMillis(),
                  DateUtils.SECOND_IN_MILLIS,
                  DateUtils.FORMAT_ABBREV_RELATIVE)
              .toString();
    } catch (Exception e) {
      e.printStackTrace();
    }

    Log.d("DateUtility", relativeDate);

    return relativeDate;
  }
}
