package com.xiledsystems.AlternateJavaBridgelib.components.altbridge.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;

import com.xiledsystems.AlternateJavaBridgelib.components.util.ErrorMessages;


/**
 * Utilities for reading and writing files to the external storage.
 * 
 */
public class FileUtil {
  // Note: Some phones come with a "Documents" directory rather than a
  // "My Documents" directory. Should we check for this and try to be
  // consistent with the phone's directory structure or to be consistent
  // in directory names across phones?
  private static final String DOCUMENT_DIRECTORY = "My Documents/";

  private static final String DIRECTORY_RECORDINGS = "Recordings";

  private static final String FILENAME_PREFIX = "app_inventor_";

  // This has the same value as Environment.DIRECTORY_PICTURES, which was
  // not defined until API Level 8 (Android 2.2).
  // If we use Environment.DIRECTORY_PICTURES here, then this class (FileUtil)
  // will be rejected by the dalvikvm class verifier on older versions of
  // Android.
  private static final String DIRECTORY_PICTURES = "Pictures";

  private static final String DIRECTORY_DOWNLOADS = "Downloads";

  private FileUtil() {
  }

  /**
   * Returns the contents of a file as a string.
   * 
   * @param inputFileName
   *          the full path and name of the file
   * @return a string of the contents of the file
   * @throws IOException
   */
  public static String loadFileAsString(String inputFileName) throws IOException {
    String s;
    File file = new File(inputFileName);
    InputStream is = null;
    try {
      is = new FileInputStream(file);
    } finally {
      BufferedReader reader = new BufferedReader(new InputStreamReader(is));
      try {
        String line;
        StringBuilder contents = new StringBuilder();
        while ((line = reader.readLine()) != null) {
          contents.append(line);
        }
        s = contents.toString();
      } finally {
        try {
          is.close();
        } finally {

        }
      }
    }
    return s;
  }
  
  /**
   * 
   * @param context
   * @param content
   * @return a file path from an Image contentUri
   */
  public static String getFilePathFromImageContentUri(Context context, Uri content) {	  
	  String[] proj = { MediaStore.Images.Media.DATA };
	  Cursor cursor = context.getContentResolver().query(content, proj, null, null, null);
	  int column_indx = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
	  cursor.moveToFirst();
	  String path = cursor.getString(column_indx);
	  cursor.close();
	  return path;
  }

  public static String loadInputStreamAsString(InputStream in) throws IOException {
    BufferedReader reader = new BufferedReader(new InputStreamReader(in));
    String s;
    try {
      String line;
      StringBuilder contents = new StringBuilder();
      while ((line = reader.readLine()) != null) {
        contents.append(line);
      }
      s = contents.toString();
    } finally {
      try {
        in.close();
      } finally {

      }
    }
    return s;
  }

  /**
   * Returns an URL for the given local file.
   */
  public static String getFileUrl(String localFileName) {
    File file = new File(localFileName);
    return file.toURI().toString();
  }

  /**
   * Reads the given local file and returns the contents as a byte array.
   * 
   * @param inputFileName
   *          the name of the file to read from
   * @return the file's contents as a byte array
   */
  public static byte[] readFile(String inputFileName) throws IOException {
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    InputStream in = new FileInputStream(inputFileName);
    try {
      copy(in, out);
    } finally {
      in.close();
    }
    return out.toByteArray();
  }

  /**
   * Downloads the resource with the given URL and writes it as a local file.
   * 
   * @param url
   *          the URL to read from
   * @param outputFileName
   *          the name of the file to write to
   * @return the URL for the local file
   */
  public static String downloadUrlToFile(String url, String outputFileName) throws IOException {
    InputStream in = new URL(url).openStream();
    try {
      return writeStreamToFile(in, outputFileName);
    } finally {
      in.close();
    }
  }

  /**
   * Writes the given byte array as a local file. This expects absolute paths.
   * 
   * @param array
   *          the byte array to read from
   * @param outputFileName
   *          the name of the file to write to
   * @return the URL for the local file
   */
  public static String writeFile(byte[] array, String outputFileName) throws IOException {
    InputStream in = new ByteArrayInputStream(array);
    try {
      return writeStreamToFile(in, outputFileName);
    } finally {
      in.close();
    }
  }

  /**
   * Writes the given String of data as an internal file to your application. Do
   * not use absolute paths with this method.
   * 
   * @param context
   *          the Context you are using (Form, or FormService)
   * @param array
   *          the byte array to read from
   * @param outputFileName
   *          the name of the file to write to
   * @return the URL for the local file
   */
  public static String writeToFile(Context context, byte[] data, String outputFileName)
      throws IOException {
    InputStream in = new ByteArrayInputStream(data);
    try {
      return writeStreamToInternalFile(context, in, outputFileName);
    } finally {
      in.close();
    }
  }

  /**
   * Copies the contents of one local file to another local file.
   * 
   * @param inputFileName
   *          the name of the file to read in
   * @param outputFileName
   *          the name of the file to write to
   * @return the URL for the local file
   */
  public static String copyFile(String inputFileName, String outputFileName) throws IOException {
    InputStream in = new FileInputStream(inputFileName);
    try {
      return writeStreamToFile(in, outputFileName);
    } finally {
      in.close();
    }
  }

  /**
   * Copies a file (using the absolute path) to the internal storage directory for the 
   * app calling this method.
   * 
   * @param context - The calling context (Form, or FormService)
   * @param inputFilePath - The absolute path of the file to copy
   * @return - The path of the copied file. "" is returned if an error occured (Check the logcat)
   */
  public static String copyFileToPrivateStorage(Context context, String inputFilePath) {
    try {
      File inFile = new File(inputFilePath);
      String fName = inFile.getName();
      if (!fName.startsWith("/")) {
        fName = "/" + fName;            
      }
      InputStream in = new FileInputStream(inFile);      
      return writeStreamToFile(in, context.getFilesDir() + fName);
    } catch (FileException e) {
      e.printStackTrace();
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
    return "";
  }

  /**
   * Writes the contents from the given input stream to the given file.
   * 
   * @param in
   *          the InputStream to read from
   * @param outputFileName
   *          the name of the file to write to
   * @return the URL for the local file
   */
  public static String writeStreamToFile(InputStream in, String outputFileName) throws IOException {
    File file = new File(outputFileName);

    // Create the parent directory.
    try {
      file.getParentFile().mkdirs();
    } catch (NullPointerException e) {
      // If it's a top level file, this exception
      // will be thrown, so no need to create the
      // parent directory
    }

    OutputStream out = new FileOutputStream(file);
    try {
      copy(in, out);

      // Return the URL to the output file.
      return file.toURI().toString();
    } finally {
      out.flush();
      out.close();
    }
  }

  public static String writeStreamToInternalFile(Context context, InputStream in,
      String outputFileName) throws IOException {

    OutputStream out = context.openFileOutput(outputFileName, Context.MODE_PRIVATE);

    try {
      copy(in, out);

      // Find the name of the file in the private list, and return it
      String[] files = context.fileList();
      for (String f : files) {
        if (f.contains(outputFileName)) {
          if (f.contains("/")) {
            return context.getFilesDir().getAbsolutePath() + f;
          } else {
            return context.getFilesDir().getAbsolutePath() + "/" + f;
          }
        }
      }
      // Return the original filename. Although this would be unusual, as
      // the name should be caught in the loop above.
      return context.getFilesDir().getAbsolutePath() + "/" + outputFileName;
    } finally {
      out.flush();
      out.close();
    }
  }

  private static void copy(InputStream in, OutputStream out) throws IOException {
    out = new BufferedOutputStream(out, 0x1000);
    in = new BufferedInputStream(in, 0x1000);

    // Copy the contents from the input stream to the output stream.
    while (true) {
      int b = in.read();
      if (b == -1) {
        break;
      }
      out.write(b);
    }
    out.flush();
  }

  /**
   * Deletes all files in the cache directory of the calling application.
   * 
   * @param context
   *          - The calling context (Form, or FormService)
   */
  public static void clearCacheDir(Context context) {
    deleteDirectory(context.getCacheDir(), true);
  }

  /**
   * Deletes all files in the calling application's data directory. This is
   * equivalent to hitting the Clear Data button under manage applications.
   * 
   * @param context
   *          - The calling context (Form, or FormService)
   */
  public static void clearDataDir(Context context) {
    deleteDirectory(context.getFilesDir(), true);
  }

  /**
   * Deletes all files in the supplied directory. This also deletes the
   * directory if all files and directories are deleted.
   * 
   * @param directory
   *          - The directory to delete
   * @param recursive
   *          - If false, only files will be deleted in the top directory.
   */
  public static void deleteDirectory(File directory, boolean recursive) {
    File[] files = directory.listFiles();
    for (File f : files) {
      if (f.isDirectory() && recursive) {
        deleteDirectory(f, true);
      } else {
        if (f.isFile()) {
          f.delete();
        }
      }
    }
    directory.delete();
  }

  /**
   * Creates a {@link File} representing the complete path for an image file,
   * creating the enclosing directories if needed. This does not actually open
   * the file. Any component that calls this must have
   * {@link android.Manifest.permission.WRITE_EXTERNAL_STORAGE}.
   * 
   * @param extension
   *          file extension, such as "png" or "jpg"
   * @return the path to the file
   * @throws IOException
   *           if the enclosing directory cannot be created
   * @throws FileException
   *           if external storage is not accessible or not writable with the
   *           appropriate ErrorMessages error code.
   */
  public static File getPictureFile(String extension) throws IOException, FileException {
    return getFile(DIRECTORY_PICTURES, extension);
  }

  /**
   * Creates a {@link File} representing the complete path for a recording,
   * creating the enclosing directories if needed. This does not actually open
   * the file. Any component that calls this must have
   * {@link android.Manifest.permission.WRITE_EXTERNAL_STORAGE}.
   * 
   * @return the path to the file
   * @param extension
   *          file extension, such as "3gp"
   * @throws IOException
   *           if the enclosing directory cannot be created
   * @throws FileException
   *           if external storage is not accessible or not writable with the
   *           appropriate ErrorMessages error code.
   */
  public static File getRecordingFile(String extension) throws IOException, FileException {
    return getFile(DIRECTORY_RECORDINGS, extension);
  }

  /**
   * Creates a {@link File} representing the complete path for a downloaded
   * file, creating the enclosing directories if needed. This does not actually
   * open the file. Any component that calls this must have
   * {@link android.Manifest.permission.WRITE_EXTERNAL_STORAGE}.
   * 
   * @return the path to the file
   * @param extension
   *          file extension, such as "tmp"
   * @throws IOException
   *           if the enclosing directory cannot be created
   * @throws FileException
   *           if external storage is not accessible or not writable with the
   *           appropriate ErrorMessages error code.
   */
  public static File getDownloadFile(String extension) throws IOException, FileException {
    return getFile(DIRECTORY_DOWNLOADS, extension);
  }

  /**
   * Determines the best directory in which to store a file of the given type
   * and creates the directory if it does not exist, generating a full path.
   * 
   * @param category
   *          a descriptive category, such as {@link DIRECTORY_PICTURES} to
   *          include in the path
   * @param extension
   *          the extension for the end of the file, not including the period,
   *          such as "png"
   * @return the full path to the file
   * @throws IOException
   *           if the directory cannot be created
   */
  private static File getFile(String category, String extension) throws IOException, FileException {
    String fileName = DOCUMENT_DIRECTORY + category + "/" + FILENAME_PREFIX
        + System.currentTimeMillis() + "." + extension;
    return getExternalFile(fileName);
  }

  /**
   * Returns the File for fileName in the external storage directory in
   * preparation for writing the file. fileName may contain sub-directories.
   * Ensures that all subdirectories exist and that fileName does not exist
   * (deleting it if necessary).
   * 
   * @param fileName
   *          The path name of the file relative to the external storage
   *          directory
   * @return the File object for creating fileName in the external storage
   * @throws IOException
   *           if we are unable to create necessary parent directories or delete
   *           an existing file
   * @throws FileException
   *           if the external storage is not writeable.
   */
  public static File getExternalFile(String fileName) throws IOException, FileException {
    checkExternalStorageWriteable();
    File file = new File(Environment.getExternalStorageDirectory(), fileName);
    File directory = file.getParentFile();
    if (!directory.exists() && !directory.mkdirs()) {
      throw new IOException("Unable to create directory " + directory.getAbsolutePath());
    }
    if (file.exists()) {
      if (!file.delete()) {
        throw new IOException("Cannot overwrite existing file " + file.getAbsolutePath());
      }
    }
    return file;
  }

  /*
   * Checks that external storage is mounted writeable. If it isn't, throws an
   * exception.
   */
  private static void checkExternalStorageWriteable() throws FileException {
    String state = Environment.getExternalStorageState();
    if (Environment.MEDIA_MOUNTED.equals(state)) {
      return;
    }
    if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
      throw new FileException(ErrorMessages.ERROR_MEDIA_EXTERNAL_STORAGE_READONLY);
    } else {
      throw new FileException(ErrorMessages.ERROR_MEDIA_EXTERNAL_STORAGE_NOT_AVAILABLE);
    }
  }

  /**
   * Exception class for reporting back media-related error numbers from
   * ErrorMessages, which the caller can in turn pass to
   * Form.dispatchErrorOccurredEvent if needed.
   */
  public static class FileException extends RuntimeException {
    /**
     * 
     */
    private static final long serialVersionUID = 4215519677253207750L;
    private final int msgNumber;

    public FileException(int errorMsgNumber) {
      msgNumber = errorMsgNumber;
    }

    public int getErrorMessageNumber() {
      return msgNumber;
    }
  }
}
