package com.xiledsystems.AlternateJavaBridgelib.components.altbridge;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

import java.io.File;
import java.util.Date;

import com.xiledsystems.AlternateJavaBridgelib.components.Component;
import com.xiledsystems.AlternateJavaBridgelib.components.events.EventDispatcher;
import com.xiledsystems.AlternateJavaBridgelib.components.events.Events;
import com.xiledsystems.AlternateJavaBridgelib.components.util.ErrorMessages;

/**
 * Camera provides access to the phone's camera
 *
 *  NOTE: DON'T FORGET TO ADD "WRITE_EXTERNAL_STORAGE" permission 
 *  to your manifest if you plan to take a picture
 *
 */

public class Camera extends AndroidNonvisibleComponent
    implements ActivityResultListener, Component {

  private static final String CAMERA_INTENT = "android.media.action.IMAGE_CAPTURE";
  private static final String CAMERA_OUTPUT = "output";  
  private Uri imageFile;

  /* Used to identify the call to startActivityForResult. Will be passed back
  into the resultReturned() callback method. */
  private int requestCode;

    /**
   * Creates a Camera component.
   *
   * @param container container, component will be placed in
   */
  public Camera(ComponentContainer container) {
    super(container);    
  }

  /** Same as the regular TakePicture method, but allows you to replace the
  *   default "app_inventor" prefix with a string of your choosing
  *   If you want to instead set the full path instead of just the prefix
  *   choose true for the fullPath boolean
  *
  *   NOTE: BE CAREFUL using fullPath boolean.  You MUST provide a correct path or
  *   the method will throw an exception.  Make sure to start with a backslash "/"
  *   and DO NOT specify file type.  It is added at the end and is always a .jpg
  *
  *   @param prefix - the name of the picture prefix
  *   @param fullPath - when set to true uses the prefix String as the full
  *                     file path
  */
  public String TakePictureAdvanced(String prefix, boolean fullPath) {
    Date date = new Date();
    String state = Environment.getExternalStorageState();

    if (Environment.MEDIA_MOUNTED.equals(state)) {
      Log.i("CameraComponent", "External storage is available and writable");

      if (fullPath) {
        imageFile = Uri.fromFile(new File(Environment.getExternalStorageDirectory(),
        prefix.trim() + ".jpg"));
      } else {

        imageFile = Uri.fromFile(new File(Environment.getExternalStorageDirectory(),
        "/Pictures/" + prefix + "_" + date.getTime()
        + ".jpg"));
      }

      ContentValues values = new ContentValues();
      values.put(MediaStore.Images.Media.DATA, imageFile.getPath());
      values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
      values.put(MediaStore.Images.Media.TITLE, imageFile.getLastPathSegment());

      if (requestCode == 0) {
        requestCode = container.getRegistrar().registerForActivityResult(this);
      }

      Uri imageUri = container.$context().getContentResolver().insert(
        MediaStore.Images.Media.INTERNAL_CONTENT_URI, values);
      Intent intent = new Intent(CAMERA_INTENT);
      intent.putExtra(CAMERA_OUTPUT, imageUri);
      container.$context().startActivityForResult(intent, requestCode);
    } else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
    	container.getRegistrar().dispatchErrorOccurredEvent(this, "TakePicture",
          ErrorMessages.ERROR_MEDIA_EXTERNAL_STORAGE_READONLY);
    } else {
    	container.getRegistrar().dispatchErrorOccurredEvent(this, "TakePicture",
          ErrorMessages.ERROR_MEDIA_EXTERNAL_STORAGE_NOT_AVAILABLE);
    }
    return imageFile.toString();
  }
  
  public String TakePicture() {
    Date date = new Date();
    String state = Environment.getExternalStorageState();

    if (Environment.MEDIA_MOUNTED.equals(state)) {
      Log.i("CameraComponent", "External storage is available and writable");

      imageFile = Uri.fromFile(new File(Environment.getExternalStorageDirectory(),
        "/Pictures/app_inventor_" + date.getTime()
        + ".jpg"));

      ContentValues values = new ContentValues();
      values.put(MediaStore.Images.Media.DATA, imageFile.getPath());
      values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
      values.put(MediaStore.Images.Media.TITLE, imageFile.getLastPathSegment());

      if (requestCode == 0) {
        requestCode = container.getRegistrar().registerForActivityResult(this);
      }

      Uri imageUri = container.$context().getContentResolver().insert(
        MediaStore.Images.Media.INTERNAL_CONTENT_URI, values);
      Intent intent = new Intent(CAMERA_INTENT);
      intent.putExtra(CAMERA_OUTPUT, imageUri);
      container.$context().startActivityForResult(intent, requestCode);
    } else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
    	container.getRegistrar().dispatchErrorOccurredEvent(this, "TakePicture",
          ErrorMessages.ERROR_MEDIA_EXTERNAL_STORAGE_READONLY);
    } else {
    	container.getRegistrar().dispatchErrorOccurredEvent(this, "TakePicture",
          ErrorMessages.ERROR_MEDIA_EXTERNAL_STORAGE_NOT_AVAILABLE);
    }
    return imageFile.toString();
  }

  @Override
  public void resultReturned(int requestCode, int resultCode, Intent data) {
    Log.i("CameraComponent",
      "Returning result. Request code = " + requestCode + ", result code = " + resultCode);
    if (requestCode == this.requestCode && resultCode == Activity.RESULT_OK) {
      File image = new File(imageFile.getPath());
      if (image.length() != 0) {
        AfterPicture(imageFile.toString(), imageFile);
      } else {
        deleteFile(imageFile);  // delete empty file
        // see if something useful got returned in the data
        if (data != null && data.getData() != null) {
          Uri tryImageUri = data.getData();
          Log.i("CameraComponent", "Calling Camera.AfterPicture with image path "
              + tryImageUri.toString());
          AfterPicture(tryImageUri.toString(), tryImageUri);
        } else {
          Log.i("CameraComponent", "Couldn't find an image file from the Camera result");
          container.getRegistrar().dispatchErrorOccurredEvent(this, "TakePicture",
              ErrorMessages.ERROR_CAMERA_NO_IMAGE_RETURNED);
        }
      }
    } else {
      // delete empty file
      deleteFile(imageFile);
    }
  }

  private void deleteFile(Uri fileUri) {
    File fileToDelete = new File(fileUri.getPath());
    try {
      if (fileToDelete.delete()) {
        Log.i("CameraComponent", "Deleted file " + fileUri.toString());
      } else {
        Log.i("CameraComponent", "Could not delete file " + fileUri.toString());
      }
    } catch (SecurityException e) {
      Log.i("CameraComponent", "Got security exception trying to delete file "
          + fileUri.toString());
    }
  }

  
  public void AfterPicture(String image, Uri imageUri) {
	  if (eventListener != null) {
		  eventListener.eventDispatched(Events.AFTER_PICTURE, image, imageUri);
	  } else {
		  EventDispatcher.dispatchEvent(this, Events.AFTER_PICTURE, image, imageUri);
	  }
  }
}
