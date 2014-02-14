package com.xiledsystems.AlternateJavaBridgelib.components.altbridge.util;

import java.io.File;
import java.io.IOException;
import android.util.Log;
import com.xiledsystems.AlternateJavaBridgelib.components.Component;
import com.xiledsystems.AlternateJavaBridgelib.components.altbridge.AndroidNonvisibleComponent;
import com.xiledsystems.AlternateJavaBridgelib.components.altbridge.ComponentContainer;
import com.xiledsystems.AlternateJavaBridgelib.components.altbridge.SvcComponentContainer;


/**
 * A class for storing string values securely. This class uses the EncryptUtil
 * class to encrypt all values using AES encryption, and a randomly generated
 * string key of 32-64 characters.
 * 
 * This can be expensive, as each time you call StoreValue, it will encrypt the
 * string, then store it in it's own file. Likewise, GetValue runs the
 * decryption after reading the file.
 * 
 * This class isn't meant to store large amounts of data, though there is
 * nothing really stopping you, other than performance concerns.
 * 
 * @author Ryan Bis
 * 
 */
public class SecureTinyDB extends AndroidNonvisibleComponent implements Component {

  // Name the file of the encoding key something generic to steer bad people
  // away. Or at least deflect the attention.
  private final static String ENC_KEY = "UtilFile";  
  private boolean logErrors = true;
  private final String encKey;

  public SecureTinyDB(ComponentContainer container) {
    super(container);
    encKey = checkKey();    
  }

  public SecureTinyDB(SvcComponentContainer container) {
    super(container);
    encKey = checkKey();
  }

  private String checkKey() {
    String key = getKey();
    if (key.equals("")) {
      key = AltRandom.RndString(AltRandom.RndInt(32, 64));
      storeKey(key);
    }
    return key;
  }
  
  private String getKey() {
    try {
      byte[] bytes = FileUtil.readFile(getContext().getFilesDir() + "/" + ENC_KEY);
      return new String(bytes, "UTF-8");
    } catch (IOException e) {
      return "";
    }
  }
  
  /**
   * Store your own encryption key.
   * 
   * @param key
   */
  public void storeKey(String key) {
    try {
      
      FileUtil.writeToFile(getContext(), key.getBytes("UTF-8"), ENC_KEY);
    } catch (IOException e) {
      Log.e("SecureTinyDB", "Unable to save Encryption key!");
    } 
  }
  
  /**
   * 
   * @param tag - The tag name to get the path for
   * @return - The absolute path location of the saved tag. If the
   * tag doesn't exist "" will be returned.
   */
  public String getAbsoluteFilePath(String tag) {    
    File file = new File(getContext().getFilesDir().getAbsolutePath() + ensureStartsWithSep(tag));
    if (file.exists()) {
      return file.getAbsolutePath();
    } else {
      return "";
    }
  }

  /**
   * Default is true, set this to false to disable log errors from
   * showing up.
   * 
   * @param logerrors
   */
  public void LogErrors(boolean logerrors) {
    logErrors = logerrors;
  }

  /**
   * 
   * @return - Whether this component is logging errors
   */
  public boolean LogErrors() {
    return logErrors;
  }

  /**
   * Store string data to it's own file. The string is first encrypted before
   * writing it to it's own file. This forces UTF-8 string encoding.
   * 
   * @param tag
   *          - The name of the tag (also the name of the file)
   * @param data
   *          - The string data to encrypt and save
   * @return - whether or not the save was successful
   */
  public boolean StoreValue(String tag, String data) {

    try {      
      byte[] encData = EncryptUtil.ecrypt(encKey, data.getBytes("UTF-8"));     
      FileUtil.writeToFile(getContext(), encData, tag);
    } catch (IOException e) {
      if (logErrors) {
        Log.e("SecureTinyDB", "Error in creating the file!");
        e.printStackTrace();
      }
      return false;
    }
    return true;
  }

  /**
   * Retrieve string data. This will read the data, then decrypt it for you.
   * 
   * If the tag doesn't exist, "" will be returned. Note how this differs
   * from TinyDB.
   * 
   * @param tag - The tag to retrieve
   * @return - the String data
   */
  public String GetValue(String tag) {
    byte[] data;
    String dataString = "";
    try {
      data = FileUtil.readFile(getContext().getFilesDir() + ensureStartsWithSep(tag));
      byte[] decrypted = EncryptUtil.decrypt(encKey, data);
      dataString = new String(decrypted, "UTF-8");
    } catch (IOException e) {
      if (logErrors) {
        Log.e("SecureTinyDB", "Error in loading the file. Does it exist? Do you have permissions?");
        e.printStackTrace();
        return "";
      }
    }
    return dataString;
  }
  
  private static String ensureStartsWithSep(String filepath) {
    if (!filepath.startsWith("/")) {
      filepath = "/" + filepath;
    }
    return filepath;
  }
}
