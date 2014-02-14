package com.xiledsystems.AlternateJavaBridgelib.components.altbridge.util;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;


public class EncryptUtil {
  
  private static int ENCRYPTION_BITS = 128;

  private EncryptUtil() {
  }
  
  public static void setEncryptionBits(int bits) {
    ENCRYPTION_BITS = bits;
  }
  
  public static int getEncryptionBits() {
    return ENCRYPTION_BITS;
  } 

  /**
   * Encrypts data using the AES algorithm, and the key provided. When
   * decrypting, make sure to use the same key. You are responsible for the key.
   * 
   * @param key
   *          - The key to encrypt the data with
   * @param data
   *          - The data to be encrypted
   * @return - The encrypted data (null will be returned if an error occured,
   *         along with errors being posted in LogCat).
   */
  public static byte[] ecrypt(String key, byte[] data) {
    byte[] encrypted = null;
    try {
      byte[] k = generateKey(key);
      if (k != null) {
        SecretKeySpec skeySpec = new SecretKeySpec(k, "AES");
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, skeySpec);
        encrypted = cipher.doFinal(data);
      }
    } catch (NoSuchAlgorithmException e) {
      e.printStackTrace();
    } catch (NoSuchPaddingException e) {
      e.printStackTrace();
    } catch (InvalidKeyException e) {
      e.printStackTrace();
    } catch (IllegalBlockSizeException e) {
      e.printStackTrace();
    } catch (BadPaddingException e) {
      e.printStackTrace();
    }
    return encrypted;
  }

  /**
   * Decrypts data which was encrypted using the encrypt method in this class. It
   * uses the AES algorithm.
   * 
   * @param key
   *          - The key used to encrypt the data
   * @param encryptedData
   *          - the encrypted data
   * @return - the decrypted data (will be null if an error occured, along with
   *         the stacktrace in the logcat
   */
  public static byte[] decrypt(String key, byte[] encryptedData) {
    byte[] decrypted = null;
    try {
      byte[] k = generateKey(key);
      if (k != null) {
        SecretKeySpec skeySpec = new SecretKeySpec(k, "AES");
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, skeySpec);
        decrypted = cipher.doFinal(encryptedData);
      }
    } catch (NoSuchAlgorithmException e) {
      e.printStackTrace();
    } catch (NoSuchPaddingException e) {
      e.printStackTrace();
    } catch (InvalidKeyException e) {
      e.printStackTrace();
    } catch (IllegalBlockSizeException e) {
      e.printStackTrace();
    } catch (BadPaddingException e) {
      e.printStackTrace();
    }
    return decrypted;
  }

  private static byte[] generateKey(String keyString) {
    byte[] key = null;
    byte[] k1 = keyString.getBytes();
    KeyGenerator kgen;
    try {
      kgen = KeyGenerator.getInstance("AES");
      SecureRandom sr = SecureRandom.getInstance("SHA1PRNG");
      sr.setSeed(k1);
      kgen.init(ENCRYPTION_BITS, sr);
      SecretKey skey = kgen.generateKey();
      key = skey.getEncoded();
    } catch (NoSuchAlgorithmException e) {
      e.printStackTrace();
    }
    return key;
  }

}
