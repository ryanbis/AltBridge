package com.xiledsystems.AlternateJavaBridgelib.components.altbridge;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Vibrator;
import android.util.Log;
import java.io.IOException;
import com.xiledsystems.AlternateJavaBridgelib.components.Component;
import com.xiledsystems.AlternateJavaBridgelib.components.altbridge.util.MediaUtil;
import com.xiledsystems.AlternateJavaBridgelib.components.events.EventDispatcher;
import com.xiledsystems.AlternateJavaBridgelib.components.events.Events;
import com.xiledsystems.AlternateJavaBridgelib.components.util.ErrorMessages;
import com.xiledsystems.altbridge.BuildConfig;


// TODO: This implementation does nothing about releasing the Media
// Player resources when the application stops.  This needs to be handled
// at the application level, not just at the component level.
// We do release a previously used MediaPlayer before creating a new one.
//
// TODO: This implementation fails when there are multiple media
// players in an application.  This appears to be a bug in the
// Android SDK, or possibly in ODE, but we need to investigate more
// fully.
//
// TODO: Do more extensive testing of how state is handled here to see
// if the state restrictions are adequate given the API, and prove that
// there can't be deadlock or starvation.
// TODO: Remove writes of state debugging info to the log after we're
// sure things are working solidly.
/**
 * Multimedia component that plays audio or video and optionally vibrates. It is
 * built on top of {@link android.media.MediaPlayer}.
 * 
 */

public final class Player extends AndroidNonvisibleComponent implements OnCompletionListener, Component, Deleteable, OnDestroySvcListener {

	private MediaPlayer mp;
	private final Vibrator vibe;

	private int playerState;
	private String sourcePath;

	private final boolean isService;
	private boolean notRawResouce = false;

	/*
	 * playerState encodes a simplified version of the full MediaPlayer state
	 * space, that should be adequate, given this API: 0: player initial state
	 * 1: player prepared but not started 2: player started or paused The
	 * allowable transitions are: Start: must be called in state 1 or 2, results
	 * in state 2 Pause: must be called in state 2, results in state 2 Stop:
	 * must be called in state 1 or 2, results in state 1 We can simplify this
	 * to remove state 0 and use a simple boolean after we're more confident
	 * that there are no start-up problems.
	 */

	/**
	 * Creates a new Player component.
	 * 
	 * @param container
	 */
	public Player(ComponentContainer container) {
		super(container);
		sourcePath = "";
		vibe = (Vibrator) container.$context().getSystemService(Context.VIBRATOR_SERVICE);
		this.isService = false;
	}

	public Player(SvcComponentContainer container) {
		super(container);
		sourcePath = "";
		vibe = (Vibrator) container.$formService().getSystemService(Context.VIBRATOR_SERVICE);
		this.isService = true;
		container.$formService().registerForOnDestroy(this);
	}

	/**
	 * Returns the path to the audio or video source
	 */

	public String Source() {
		return sourcePath;
	}

	/**
	 * Sets the audio or video source.
	 * 
	 * <p/>
	 * See {@link MediaUtil#determineMediaSource} for information about what a
	 * path can be.
	 * 
	 * @param path
	 *            the path to the audio or video source
	 */

	public void Source(String path) {		
		sourcePath = (path == null) ? "" : path;

		// Clear the previous MediaPlayer.
		if (playerState == 1 || playerState == 2) {
			mp.stop();
		}
		playerState = 0;
		if (mp != null) {
			mp.release();
			mp = null;
		}

		if (sourcePath.length() > 0) {
			if (BuildConfig.DEBUG) {
				Log.i("Player", "Source path is " + sourcePath);
			}
			mp = new MediaPlayer();
			mp.setOnCompletionListener(this);
			try {
				if (isService) {
					notRawResouce = MediaUtil.loadMediaPlayer(mp, sContainer.$formService(), sourcePath);
				} else {
					notRawResouce = MediaUtil.loadMediaPlayer(mp, container.$context(), sourcePath);
				}
				if (!notRawResouce) {
					mp = MediaUtil.createMediaPlayer(getContext(), sourcePath);
					mp.setOnCompletionListener(this);
				}

			} catch (IOException e) {
				mp.release();
				mp = null;
				if (isService) {
					sContainer.$formService().dispatchErrorOccurredEvent(this, "Source", ErrorMessages.ERROR_UNABLE_TO_LOAD_MEDIA, 
							sourcePath);
				} else {
					container.getRegistrar().dispatchErrorOccurredEvent(this, "Source", ErrorMessages.ERROR_UNABLE_TO_LOAD_MEDIA, 
							sourcePath);
				}
				return;
			}

			mp.setAudioStreamType(AudioManager.STREAM_MUSIC);
			if (BuildConfig.DEBUG) {
				Log.i("Player", "Successfully loaded source path " + sourcePath);
			}

			// The Simple API is set up so that the user never has
			// to call prepare.

			// If the file was loaded from a raw resource, then the action
			// of creating the MediaPlayer automatically prepares it, so there
			// is no need to prepare, in fact, it will cause an exception to be
			// thrown.
			if (notRawResouce) {
				prepare();
			} else {
				playerState = 1;
			}
			// Player should now be in state 1. (If prepare failed, we are in
			// state 0.)
		}
	}

	// AJB change - Add an easy way to loop a sound source

	public void Loop(boolean looping) {

		mp.setLooping(looping);

	}

	/**
	 * Plays the media. If it was previously paused, the playing is resumed.
	 */

	public void Start() {
		if (BuildConfig.DEBUG) {
			Log.i("Player", "Calling Start -- State=" + playerState);
		}
		if (playerState == 1 || playerState == 2) {
			mp.start();
			playerState = 2;
		}
	}

	/**
	 * Suspends playing the media if it is playing.
	 */

	public void Pause() {
		if (BuildConfig.DEBUG) {
			Log.i("Player", "Calling Pause -- State=" + playerState);
		}
		if (playerState == 2) {
			mp.pause();
			playerState = 2;
			// Player should now be in state 2.
		}
	}

	/**
	 * Stops playing the media
	 */

	public void Stop() {
		if (BuildConfig.DEBUG) {
			Log.i("Player", "Calling Stop -- State=" + playerState);
		}
		if (playerState == 1 || playerState == 2) {
			mp.stop();
			prepare();
			// Player should now be in state 1. (If prepare failed, we are in
			// state 0.)
		}
	}

	// TODO: Reconsider whether vibrate should be here or in a separate
	// component.
	/**
	 * Vibrates for specified number of milliseconds.
	 */

	public void Vibrate(long milliseconds) {
		vibe.vibrate(milliseconds);
	}

	public void PlayerError(String message) {
	}

	private void prepare() {
		// This should be called only after mp.stop() or directly after
		// initialization
		try {
			mp.prepare();
			playerState = 1;
			notRawResouce = true;
			if (BuildConfig.DEBUG) {
				Log.i("Player", "Successfully prepared");
			}

		} catch (IOException ioe) {
			mp.release();
			mp = null;
			playerState = 0;
			if (isService) {
				sContainer.$formService().dispatchErrorOccurredEvent(this, "Source", ErrorMessages.ERROR_UNABLE_TO_PREPARE_MEDIA, sourcePath);
			} else {
				container.getRegistrar().dispatchErrorOccurredEvent(this, "Source", ErrorMessages.ERROR_UNABLE_TO_PREPARE_MEDIA, sourcePath);
			}
		}
	}
	
	private void Completed() {
		EventDispatcher.dispatchEvent(this, Events.COMPLETED);
	}

	// Deleteable implementation

	@Override
	public void onDelete() {
		mp.stop();
		mp.release();
		vibe.cancel();
	}

	@Override
	public void onDestroy() {

		mp.stop();
		mp.release();
		vibe.cancel();

	}

	@Override
	public void onCompletion(MediaPlayer mp) {
		Completed();
	}
	
}
