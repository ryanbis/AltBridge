package com.xiledsystems.AlternateJavaBridgelib.components.altbridge.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;


public class Zip {

  private final static int BUFFER = 2048;

  private Zip() {
  }

  /**
   * This will zip all the files into one zip file (all within the
   * root of the zip).
   * 
   * @param files - String array of the paths to the files to zip.
   * @param zipfile - The path of the zip file to create
   * @return - whether or not the zipping succeeded
   */
  public static boolean ZipFiles(String[] files, String zipfile) {
    try {
      BufferedInputStream origin = null;
      FileOutputStream destination = new FileOutputStream(zipfile);
      ZipOutputStream out = new ZipOutputStream(new BufferedOutputStream(destination));

      byte data[] = new byte[BUFFER];

      for (int i = 0; i < files.length; i++) {
        FileInputStream fis = new FileInputStream(files[i]);
        origin = new BufferedInputStream(fis, BUFFER);
        ZipEntry entry = new ZipEntry(files[i].substring(files[i].lastIndexOf("/") + 1));
        out.putNextEntry(entry);
        int count;
        while ((count = origin.read(data, 0, BUFFER)) != -1) {
          out.write(data, 0, count);
        }
        out.closeEntry();
        origin.close();
      }
      out.close();
      return true;
    } catch (IOException e) {
      e.printStackTrace();
      return false;
    }
  }
  
  /**
   * This will zip up the input files into a zip file.
   * 
   * @param filestoZip - The paths of the files you wish to put into the zip file.
   * 
   * @param filesInZip - The paths the files should have within the zip file. These two
   * arrays are linked. The first index of this array represents the filepath within the zip
   * of the first path in the first array.
   * 
   * @param zipfile - The path to the outputted zip file.
   * 
   * @return - whether or not the zipping succeeded.
   */
  public static boolean ZipFiles(String[] filestoZip, String[] filesInZip, String zipfile) {
    try {
      BufferedInputStream origin = null;
      FileOutputStream destination = new FileOutputStream(zipfile);
      ZipOutputStream out = new ZipOutputStream(new BufferedOutputStream(destination));

      byte data[] = new byte[BUFFER];

      for (int i = 0; i < filestoZip.length; i++) {
        FileInputStream fis = new FileInputStream(filestoZip[i]);
        origin = new BufferedInputStream(fis, BUFFER);
        ZipEntry entry = new ZipEntry(filesInZip[i]);
        out.putNextEntry(entry);
        int count;
        while ((count = origin.read(data, 0, BUFFER)) != -1) {
          out.write(data, 0, count);
        }
        out.closeEntry();
        origin.close();
      }
      out.close();
      return true;
    } catch (IOException e) {
      e.printStackTrace();
      return false;
    }
  }
  
  /**
   * Unzips a zip file to the provided destination directory.
   * 
   * @param zipFile - The absolute path to the zip file
   * @param destDirectory - The absolute path to the destination directory
   * @return - whether or not the unzipping was successful. If false, check logcat for
   * the stacktrace.
   */
  public static boolean Unzip(String zipFile, String destDirectory) {
    try {
      File destDir = new File(destDirectory);
      if (!destDir.exists()) {
        destDir.mkdirs();
      }
      ZipInputStream zipIn = new ZipInputStream(new FileInputStream(zipFile));
      ZipEntry entry = zipIn.getNextEntry();
      while (entry != null) {
        String filePath = destDirectory + File.separator + entry.getName();
        if (!entry.isDirectory()) {
          extractFile(zipIn, filePath);
        } else {
          File dir = new File(filePath);
          dir.mkdirs();
        }
        zipIn.closeEntry();
        entry = zipIn.getNextEntry();
      }
      zipIn.close();
    } catch (IOException e) {
      e.printStackTrace();
      return false;
    }
    return true;
  }
  
  private static void extractFile(ZipInputStream zipIn, String path) throws IOException {
    BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(path));
    byte[] bytesIn = new byte[BUFFER];
    int read = 0;
    while ((read = zipIn.read(bytesIn)) != -1) {
      bos.write(bytesIn, 0, read);
    }
    bos.close();
  }

}
