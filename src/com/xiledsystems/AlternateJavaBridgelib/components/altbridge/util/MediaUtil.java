package com.xiledsystems.AlternateJavaBridgelib.components.altbridge.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources.NotFoundException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.net.Uri;
import android.os.Environment;
import android.provider.Contacts;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;
import android.widget.VideoView;
import java.io.File;
import java.io.FileInputStream;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import com.xiledsystems.AlternateJavaBridgelib.components.altbridge.FormService;

/**
 * Utilities for loading media.
 * 
 */
@SuppressWarnings("deprecation")
public class MediaUtil {

	private enum MediaSource {
		ASSET, REPL_ASSET, SDCARD, FILE_URL, URL, CONTENT_URI, CONTACT_URI
	}

	private static final String LOG_TAG = "MediaUtil";
	private static final String REPL_ASSET_DIR = Environment.getExternalStorageDirectory() + "/AppInventor/assets/";

	// tempFileMap maps cached media (assets, etc) to their respective temp
	// files.
	private static final Map<String, File> tempFileMap = new HashMap<String, File>();

	private MediaUtil() {
	}

	private static String replAssetPath(String assetName) {
		return REPL_ASSET_DIR + assetName;
	}

	public static String fileUrlToFilePath(String mediaPath) throws IOException {
		try {
			return new File(new URL(mediaPath).toURI()).getAbsolutePath();
		} catch (IllegalArgumentException e) {
			throw new IOException("Unable to determine file path of file url " + mediaPath);
		} catch (Exception e) {
			throw new IOException("Unable to determine file path of file url " + mediaPath);
		}
	}

	/**
	 * Determines the appropriate MediaSource for the given mediaPath.
	 * 
	 * <p>
	 * If <code>mediaPath</code> begins with "/sdcard/", or begins with the path
	 * given by {@link Environment#getExternalStorageDirectory()}, it is the
	 * name of a file on the SD card.
	 * <p>
	 * Otherwise, if <code>mediaPath</code> starts with "content://contacts", it
	 * is the content URI of a contact.
	 * <p>
	 * Otherwise, if <code>mediaPath</code> starts with "content://", it is a
	 * content URI.
	 * <p>
	 * Otherwise, if <code>mediaPath</code> is a well-formed URL and it starts
	 * with "file:", it is a file URL.
	 * <p>
	 * Otherwise, if <code>mediaPath</code> is a well-formed URL, it is an URL.
	 * <p>
	 * Otherwise, if <code>mediaPath</code> it is assumed to be the name of an
	 * asset.
	 * 
	 * @param form
	 *            the Form
	 * @param mediaPath
	 *            the path to the media
	 */
	@SuppressLint("SdCardPath")
	private static MediaSource determineMediaSource(String mediaPath) {
		if (mediaPath.startsWith("/sdcard/") || mediaPath.startsWith(Environment.getExternalStorageDirectory().getAbsolutePath())) {
			return MediaSource.SDCARD;

		} else if (mediaPath.startsWith("content://contacts/")) {
			return MediaSource.CONTACT_URI;

		} else if (mediaPath.startsWith("content://com.android.contacts/")) {
			return MediaSource.CONTACT_URI;

		} else if (mediaPath.startsWith("content://")) {
			return MediaSource.CONTENT_URI;
		}

		try {
			new URL(mediaPath);			
			// It's a well formed URL.
			if (mediaPath.startsWith("file:")) {
				return MediaSource.FILE_URL;
			}

			return MediaSource.URL;

		} catch (MalformedURLException e) {
			// It's not a well formed URL!
		}

		/*
		 * if (form instanceof ReplForm) { return MediaSource.REPL_ASSET; }
		 */

		return MediaSource.ASSET;
	}

	@SuppressLint("SdCardPath")
	private static MediaSource determineMediaSource(FormService formservice, String mediaPath) {
		if (mediaPath.startsWith("/sdcard/") || mediaPath.startsWith(Environment.getExternalStorageDirectory().getAbsolutePath())) {
			return MediaSource.SDCARD;

		} else if (mediaPath.startsWith("content://contacts/")) {
			return MediaSource.CONTACT_URI;

		} else if (mediaPath.startsWith("content://")) {
			return MediaSource.CONTENT_URI;
		}

		try {
			new URL(mediaPath);
			// It's a well formed URL.
			if (mediaPath.startsWith("file:")) {
				return MediaSource.FILE_URL;
			}

			return MediaSource.URL;

		} catch (MalformedURLException e) {
			// It's not a well formed URL!
		}

		/*
		 * if (form instanceof ReplForm) { return MediaSource.REPL_ASSET; }
		 */

		return MediaSource.ASSET;
	}

	private static InputStream openMedia(Context context, String mediaPath, MediaSource mediaSource) throws IOException {
		switch (mediaSource) {
			case ASSET:
				return context.getAssets().open(mediaPath);

			case REPL_ASSET:
				return new FileInputStream(replAssetPath(mediaPath));

			case SDCARD:
				return new FileInputStream(mediaPath);

			case FILE_URL:
			case URL:
				return new URL(mediaPath).openStream();

			case CONTENT_URI:
				return context.getContentResolver().openInputStream(Uri.parse(mediaPath));

			case CONTACT_URI:
				// Open the photo for the contact.				
				InputStream is = ContactsContract.Contacts.openContactPhotoInputStream(context.getContentResolver(), Uri.parse(mediaPath));
				if (is != null) {
					return is;
				}
				// There's no photo for the contact.
				throw new IOException("Unable to open contact photo " + mediaPath + ".");
		}
		throw new IOException("Unable to open media " + mediaPath + ".");
	}

	public static InputStream openMedia(Context context, String mediaPath) throws IOException {
		return openMedia(context, mediaPath, determineMediaSource(mediaPath));
	}

	private static InputStream openMedia(FormService formservice, String mediaPath, MediaSource mediaSource) throws IOException {
		switch (mediaSource) {
			case ASSET:
				return formservice.getAssets().open(mediaPath);

			case REPL_ASSET:
				return new FileInputStream(replAssetPath(mediaPath));

			case SDCARD:
				return new FileInputStream(mediaPath);

			case FILE_URL:
			case URL:
				return new URL(mediaPath).openStream();

			case CONTENT_URI:
				return formservice.getContentResolver().openInputStream(Uri.parse(mediaPath));

			case CONTACT_URI:
				// Open the photo for the contact.
				InputStream is = Contacts.People.openContactPhotoInputStream(formservice.getContentResolver(), Uri.parse(mediaPath));
				if (is != null) {
					return is;
				}
				// There's no photo for the contact.
				throw new IOException("Unable to open contact photo " + mediaPath + ".");
		}
		throw new IOException("Unable to open media " + mediaPath + ".");
	}

	public static InputStream openMedia(FormService formservice, String mediaPath) throws IOException {
		return openMedia(formservice, mediaPath, determineMediaSource(formservice, mediaPath));
	}

	/**
	 * Copies the media specified by mediaPath to a temp file and returns the
	 * File.
	 * 
	 * @param form
	 *            the Form
	 * @param mediaPath
	 *            the path to the media
	 */
	public static File copyMediaToTempFile(Context context, String mediaPath) throws IOException {
		MediaSource mediaSource = determineMediaSource(mediaPath);
		return copyMediaToTempFile(context, mediaPath, mediaSource);
	}

	private static File copyMediaToTempFile(Context context, String mediaPath, MediaSource mediaSource) throws IOException {
		InputStream in = openMedia(context, mediaPath, mediaSource);
		File file = null;
		try {
			file = File.createTempFile("AI_Media_", null);
			file.deleteOnExit();
			FileUtil.writeStreamToFile(in, file.getAbsolutePath());
			return file;

		} catch (IOException e) {
			if (file != null) {
				Log.e(LOG_TAG, "Could not copy media " + mediaPath + " to temp file " + file.getAbsolutePath());
				file.delete();
			} else {
				Log.e(LOG_TAG, "Could not copy media " + mediaPath + " to temp file.");
			}
			// TODO(user) - figure out how much space is left on the SD card and
			// log that
			// information.
			throw e;

		} finally {
			in.close();
		}
	}

	public static File copyMediaToTempFile(FormService formservice, String mediaPath) throws IOException {
		MediaSource mediaSource = determineMediaSource(formservice, mediaPath);
		return copyMediaToTempFile(formservice, mediaPath, mediaSource);
	}

	private static File copyMediaToTempFile(FormService formService, String mediaPath, MediaSource mediaSource) throws IOException {
		InputStream in = openMedia(formService, mediaPath, mediaSource);
		File file = null;
		try {
			file = File.createTempFile("AI_Media_", null);
			file.deleteOnExit();
			FileUtil.writeStreamToFile(in, file.getAbsolutePath());
			return file;

		} catch (IOException e) {
			if (file != null) {
				Log.e(LOG_TAG, "Could not copy media " + mediaPath + " to temp file " + file.getAbsolutePath());
				file.delete();
			} else {
				Log.e(LOG_TAG, "Could not copy media " + mediaPath + " to temp file.");
			}
			// TODO(user) - figure out how much space is left on the SD card and
			// log that
			// information.
			throw e;

		} finally {
			in.close();
		}
	}

	private static File cacheMediaTempFile(Context context, String mediaPath, MediaSource mediaSource) throws IOException {
		File tempFile = tempFileMap.get(mediaPath);
		// If the map didn't contain an entry for mediaPath, or if the temp file
		// no longer exists,
		// copy the file to a new temp file.
		if (tempFile == null || !tempFile.exists()) {
			Log.i(LOG_TAG, "Copying media " + mediaPath + " to temp file...");
			tempFile = copyMediaToTempFile(context, mediaPath, mediaSource);
			Log.i(LOG_TAG, "Finished copying media " + mediaPath + " to temp file " + tempFile.getAbsolutePath());
			tempFileMap.put(mediaPath, tempFile);
		}
		return tempFile;
	}

	private static File cacheMediaTempFile(FormService formservice, String mediaPath, MediaSource mediaSource) throws IOException {
		File tempFile = tempFileMap.get(mediaPath);
		// If the map didn't contain an entry for mediaPath, or if the temp file
		// no longer exists,
		// copy the file to a new temp file.
		if (tempFile == null || !tempFile.exists()) {
			Log.i(LOG_TAG, "Copying media " + mediaPath + " to temp file...");
			tempFile = copyMediaToTempFile(formservice, mediaPath, mediaSource);
			Log.i(LOG_TAG, "Finished copying media " + mediaPath + " to temp file " + tempFile.getAbsolutePath());
			tempFileMap.put(mediaPath, tempFile);
		}
		return tempFile;
	}

	// Image related methods

	/**
	 * Loads the image specified by mediaPath and returns a Drawable.
	 * 
	 * <p/>
	 * If mediaPath is null or empty, null is returned.
	 * 
	 * @param form
	 *            the Form
	 * @param mediaPath
	 *            the path to the media
	 * @return a Drawable or null
	 */
	public static Drawable getDrawable(Context context, String mediaPath) throws IOException {
		if (mediaPath == null || mediaPath.length() == 0) {
			return null;
		}

		MediaSource mediaSource = determineMediaSource(mediaPath);

		if (mediaSource == MediaSource.ASSET) {
			if (mediaPath.contains(".")) {
				mediaPath = mediaPath.split("\\.")[0];
			}
			int temp = context.getResources().getIdentifier(mediaPath, "drawable", context.getPackageName());
			try {
				return context.getResources().getDrawable(temp);
			} catch (NotFoundException e) {
				Log.e(LOG_TAG, "Drawable resource " + mediaPath + "could not be found!");
			}

		}
		// Unlike other types of media, we don't cache image files from the
		// internet to temp files.
		// The image at a particular URL, such as an image from a web cam, may
		// change over time.
		// When the app says to fetch the image, we need to get the latest
		// image, not one that we
		// cached previously.

		InputStream is1;
		try {
			is1 = openMedia(context, mediaPath, mediaSource);
		} catch (IOException e) {
			if (mediaSource == MediaSource.CONTACT_URI) {
				// There's no photo for this contact, return a placeholder
				// image.
				return new BitmapDrawable(BitmapFactory.decodeResource(context.getResources(), android.R.drawable.picture_frame, null));
			}
			throw e;
		}

		BitmapFactory.Options options;
		try {
			options = getBitmapOptions(context, is1);
		} finally {
			is1.close();
		}

		InputStream is2 = openMedia(context, mediaPath, mediaSource);
		try {
			return new BitmapDrawable(decodeStream(is2, null, options));
		} finally {
			is2.close();
		}
	}

	private static Bitmap decodeStream(InputStream is, Rect outPadding, BitmapFactory.Options opts) {
		// We wrap a FlushedInputStream around the given InputStream. This works
		// around a problem in
		// BitmapFactory.decodeStream where it fails to load the image if the
		// InputStream's skip method
		// doesn't skip the requested number of bytes.
		return BitmapFactory.decodeStream(new FlushedInputStream(is), outPadding, opts);
	}

	// This class comes from
	// http://android-developers.blogspot.com/2010/07/multithreading-for-performance.html
	// written by Googler Gilles Debunne.
	private static class FlushedInputStream extends FilterInputStream {
		public FlushedInputStream(InputStream inputStream) {
			super(inputStream);
		}

		@Override
		public long skip(long n) throws IOException {
			long totalBytesSkipped = 0;
			while (totalBytesSkipped < n) {
				long bytesSkipped = in.skip(n - totalBytesSkipped);
				if (bytesSkipped == 0L) {
					if (read() < 0) {
						break; // we reached EOF
					} else {
						bytesSkipped = 1; // we read one byte
					}
				}
				totalBytesSkipped += bytesSkipped;
			}
			return totalBytesSkipped;
		}
	}

	private static BitmapFactory.Options getBitmapOptions(Context context, InputStream is) {
		// Get the size of the image.
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		decodeStream(is, null, options);
		int imageWidth = options.outWidth;
		int imageHeight = options.outHeight;

		// Get the screen size.
		Display display = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();

		// Set the sample size so that we scale down any image that is larger
		// than twice the
		// width/height of the screen.
		// The goal is to never make an image that is actually larger than the
		// screen end up appearing
		// smaller than the screen.
		int maxWidth = 2 * display.getWidth();
		int maxHeight = 2 * display.getHeight();
		int sampleSize = 1;
		while ((imageWidth / sampleSize > maxWidth) && (imageHeight / sampleSize > maxHeight)) {
			sampleSize *= 2;
		}
		options = new BitmapFactory.Options();
		options.inSampleSize = sampleSize;
		return options;
	}

	// SoundPool related methods

	/**
	 * Loads the audio specified by mediaPath into the given SoundPool and
	 * returns the sound id.
	 * 
	 * Note that if the mediaPath is a content URI or an URL, the audio must be
	 * copied to a temp file and then loaded from there. This could have
	 * performance implications.
	 * 
	 * @param soundPool
	 *            the SoundPool
	 * @param form
	 *            the Form
	 * @param mediaPath
	 *            the path to the media
	 */
	public static int loadSoundPool(SoundPool soundPool, Context context, String mediaPath) throws IOException {
		MediaSource mediaSource = determineMediaSource(mediaPath);
		switch (mediaSource) {
			case ASSET:
				// return soundPool.load(form.getAssets().openFd(mediaPath), 1);
				int temp = context.getResources().getIdentifier(mediaPath, "raw", context.getPackageName());
				return soundPool.load(context, temp, 1);

			case REPL_ASSET:
				return soundPool.load(replAssetPath(mediaPath), 1);

			case SDCARD:
				return soundPool.load(mediaPath, 1);

			case FILE_URL:
				return soundPool.load(fileUrlToFilePath(mediaPath), 1);

			case CONTENT_URI:
			case URL:
				File tempFile = cacheMediaTempFile(context, mediaPath, mediaSource);
				return soundPool.load(tempFile.getAbsolutePath(), 1);

			case CONTACT_URI:
				throw new IOException("Unable to load audio for contact " + mediaPath + ".");
		}

		throw new IOException("Unable to load audio " + mediaPath + ".");
	}

	public static int loadSoundPool(SoundPool soundPool, FormService formService, String mediaPath) throws IOException {
		MediaSource mediaSource = determineMediaSource(formService, mediaPath);
		switch (mediaSource) {
			case ASSET:
				// return soundPool.load(form.getAssets().openFd(mediaPath), 1);
				int temp = formService.getResources().getIdentifier(mediaPath, "raw", formService.getPackageName());
				return soundPool.load(formService, temp, 1);

			case REPL_ASSET:
				return soundPool.load(replAssetPath(mediaPath), 1);

			case SDCARD:
				return soundPool.load(mediaPath, 1);

			case FILE_URL:
				return soundPool.load(fileUrlToFilePath(mediaPath), 1);

			case CONTENT_URI:
			case URL:
				File tempFile = cacheMediaTempFile(formService, mediaPath, mediaSource);
				return soundPool.load(tempFile.getAbsolutePath(), 1);

			case CONTACT_URI:
				throw new IOException("Unable to load audio for contact " + mediaPath + ".");
		}

		throw new IOException("Unable to load audio " + mediaPath + ".");
	}
	

	// MediaPlayer related methods
	
	/**
	 * Loads the audio or video specified by mediaPath into the given
	 * MediaPlayer.
	 * 
	 * Note that if the mediaPath is an URL, the audio or video must be copied
	 * to a temp file and then loaded from there. This could have performance
	 * implications.
	 * 
	 * @param mediaPlayer
	 *            the MediaPlayer
	 * @param form
	 *            the Form
	 * @param mediaPath
	 *            the path to the media
	 */
	
	@SuppressWarnings("static-access")
	public static boolean loadMediaPlayer(MediaPlayer mediaPlayer, Context context, String mediaPath) throws IOException {
		MediaSource mediaSource = determineMediaSource(mediaPath);
		switch (mediaSource) {
			case ASSET:
				/*
				 * AssetFileDescriptor afd = form.getAssets().openFd(mediaPath);
				 * try { FileDescriptor fd = afd.getFileDescriptor(); long
				 * offset = afd.getStartOffset(); long length = afd.getLength();
				 * mediaPlayer.setDataSource(fd, offset, length); } finally {
				 * afd.close(); } return;
				 */
				String p = mediaPath;
				if (p.contains(".")) {
					p = p.split("\\.")[0];
				}
				int temp = context.getResources().getIdentifier(p, "raw", context.getPackageName());
				mediaPlayer.create(context, temp);
				return false;

			case REPL_ASSET:
				mediaPlayer.setDataSource(replAssetPath(mediaPath));
				return true;

			case SDCARD:
				mediaPlayer.setDataSource(mediaPath);
				return true;

			case FILE_URL:
				mediaPlayer.setDataSource(fileUrlToFilePath(mediaPath));
				return true;

			case URL:
				File tempFile = cacheMediaTempFile(context, mediaPath, mediaSource);
				mediaPlayer.setDataSource(tempFile.getAbsolutePath());
				return true;

			case CONTENT_URI:
				mediaPlayer.setDataSource(context, Uri.parse(mediaPath));
				return true;

			case CONTACT_URI:
				throw new IOException("Unable to load audio or video for contact " + mediaPath + ".");
		}
		throw new IOException("Unable to load audio or video " + mediaPath + ".");
	}	
	
	public static MediaPlayer createMediaPlayer(Context context, String sourcePath) {
		String p = sourcePath;
		if (p.contains(".")) {
			p = p.split("\\.")[0];
		}
		int temp = context.getResources().getIdentifier(p, "raw", context.getPackageName());
		return MediaPlayer.create(context, temp);
	}
	
	@SuppressWarnings("static-access")
	public static boolean loadMediaPlayer(MediaPlayer mediaPlayer, FormService formservice, String mediaPath) throws IOException {
		MediaSource mediaSource = determineMediaSource(formservice, mediaPath);
		switch (mediaSource) {
			case ASSET:
				/*
				 * AssetFileDescriptor afd = form.getAssets().openFd(mediaPath);
				 * try { FileDescriptor fd = afd.getFileDescriptor(); long
				 * offset = afd.getStartOffset(); long length = afd.getLength();
				 * mediaPlayer.setDataSource(fd, offset, length); } finally {
				 * afd.close(); } return;
				 */
				String p = mediaPath;
				if (p.contains(".")) {
					p = p.split("\\.")[0];
				}
				int temp = formservice.getResources().getIdentifier(p, "raw", formservice.getPackageName());
				mediaPlayer.create(formservice, temp);
				return false;

			case REPL_ASSET:
				mediaPlayer.setDataSource(replAssetPath(mediaPath));
				return true;

			case SDCARD:
				mediaPlayer.setDataSource(mediaPath);
				return true;

			case FILE_URL:
				mediaPlayer.setDataSource(fileUrlToFilePath(mediaPath));
				return true;

			case URL:
				File tempFile = cacheMediaTempFile(formservice, mediaPath, mediaSource);
				mediaPlayer.setDataSource(tempFile.getAbsolutePath());
				return true;

			case CONTENT_URI:
				mediaPlayer.setDataSource(formservice, Uri.parse(mediaPath));
				return true;

			case CONTACT_URI:
				throw new IOException("Unable to load audio or video for contact " + mediaPath + ".");
		}
		throw new IOException("Unable to load audio or video " + mediaPath + ".");
	}

	// VideoView related methods

	/**
	 * Loads the video specified by mediaPath into the given VideoView.
	 * 
	 * Note that if the mediaPath is an asset or an URL, the video must be
	 * copied to a temp file and then loaded from there. This could have
	 * performance implications.
	 * 
	 * @param videoView
	 *            the VideoView
	 * @param form
	 *            the Form
	 * @param mediaPath
	 *            the path to the media
	 */
	public static void loadVideoView(VideoView videoView, Context context, String mediaPath) throws IOException {
		MediaSource mediaSource = determineMediaSource(mediaPath);
		switch (mediaSource) {
			case ASSET:
			case URL:
				File tempFile = cacheMediaTempFile(context, mediaPath, mediaSource);
				videoView.setVideoPath(tempFile.getAbsolutePath());
				return;

			case REPL_ASSET:
				videoView.setVideoPath(replAssetPath(mediaPath));
				return;

			case SDCARD:
				videoView.setVideoPath(mediaPath);
				return;

			case FILE_URL:
				videoView.setVideoPath(fileUrlToFilePath(mediaPath));
				return;

			case CONTENT_URI:
				videoView.setVideoURI(Uri.parse(mediaPath));
				return;

			case CONTACT_URI:
				throw new IOException("Unable to load video for contact " + mediaPath + ".");
		}
		throw new IOException("Unable to load video " + mediaPath + ".");
	}
}
