package com.codepath.teleroid.utilities;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;

import java.io.IOException;

public class BitmapManipulations {

  // scale and keep aspect ratio
  public static Bitmap scaleToFitWidth(Bitmap b, int width) {
    float factor = width / (float) b.getWidth();
    return Bitmap.createScaledBitmap(b, width, (int) (b.getHeight() * factor), true);
  }

  // scale and keep aspect ratio
  public static Bitmap scaleToFitHeight(Bitmap b, int height) {
    float factor = height / (float) b.getHeight();
    return Bitmap.createScaledBitmap(b, (int) (b.getWidth() * factor), height, true);
  }

  // scale and keep aspect ratio
  public static Bitmap scaleToFill(Bitmap b, int width, int height) {
    float factorH = height / (float) b.getHeight();
    float factorW = width / (float) b.getWidth();
    float factorToUse = (factorH > factorW) ? factorW : factorH;
    return Bitmap.createScaledBitmap(
        b, (int) (b.getWidth() * factorToUse), (int) (b.getHeight() * factorToUse), true);
  }

  // scale and don't keep aspect ratio
  public static Bitmap strechToFill(Bitmap b, int width, int height) {
    float factorH = height / (float) b.getHeight();
    float factorW = width / (float) b.getWidth();
    return Bitmap.createScaledBitmap(
        b, (int) (b.getWidth() * factorW), (int) (b.getHeight() * factorH), true);
  }

  /**
   * Method that corrects the orientation of the picture based on the EXIF data from when the
   * picture was taken and the orientation of the device.
   *
   * @param photoFilePath
   * @return Bitmap
   */
  public static Bitmap resizeAndRotateBitmapOrientation(String photoFilePath) {
    // Create and configure BitmapFactory
    BitmapFactory.Options bounds = new BitmapFactory.Options();
    bounds.inJustDecodeBounds = true;
    BitmapFactory.decodeFile(photoFilePath, bounds);
    BitmapFactory.Options opts = new BitmapFactory.Options();
    Bitmap rawTakenImage = BitmapFactory.decodeFile(photoFilePath, opts);
    Bitmap bm = BitmapManipulations.scaleToFitWidth(rawTakenImage, 200);
    ;

    // Read EXIF Data
    ExifInterface exif = null;
    try {
      exif = new ExifInterface(photoFilePath);
    } catch (IOException e) {
      e.printStackTrace();
    }
    String orientString = exif.getAttribute(ExifInterface.TAG_ORIENTATION);
    int orientation =
        orientString != null ? Integer.parseInt(orientString) : ExifInterface.ORIENTATION_NORMAL;
    int rotationAngle = 0;
    if (orientation == ExifInterface.ORIENTATION_ROTATE_90) rotationAngle = 90;
    if (orientation == ExifInterface.ORIENTATION_ROTATE_180) rotationAngle = 180;
    if (orientation == ExifInterface.ORIENTATION_ROTATE_270) rotationAngle = 270;
    // Rotate Bitmap
    Matrix matrix = new Matrix();
    matrix.setRotate(rotationAngle, (float) bm.getWidth() / 2, (float) bm.getHeight() / 2);
    Bitmap rotatedBitmap =
        Bitmap.createBitmap(bm, 0, 0, bounds.outWidth, bounds.outHeight, matrix, true);
    // Return result
    return rotatedBitmap;
  }
}
