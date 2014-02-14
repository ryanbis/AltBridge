package com.xiledsystems.AlternateJavaBridgelib.components.altbridge;

import android.view.View;


public class ProgressBar extends AndroidViewComponent implements OnStopListener {

	private final android.widget.ProgressBar view;
	private int max = 100;
	@SuppressWarnings("unused")
	private boolean indeterminate;
	private int progress;
	private boolean enabled;

	/**
	 * 
	 * Non GLE component constructor
	 * 
	 * Note: This will default to a horizontal bar Progress Bar. To change the
	 * style (use the circular version), place the component in the GLE, then
	 * Edit Style. Choose system resources, then look for progressbar
	 * 
	 * @param container
	 *            The container to put the ProgressBar into
	 */
	public ProgressBar(ComponentContainer container) {
		super(container);
		view = new android.widget.ProgressBar(container.$context(), null, android.R.attr.progressBarStyleHorizontal);
		container.$add(this);
		container.getRegistrar().registerForOnStop(this);
	}

	/**
	 * 
	 * GLE component constructor
	 * 
	 * @param container
	 *            The container to put the ProgressBar into
	 * @param resourceId
	 *            The resource Id of the Bar placed in the GLE
	 */
	public ProgressBar(ComponentContainer container, int resourceId) {
		super(container, resourceId);
		view = (android.widget.ProgressBar) container.$context().findViewById(resourceId);
		container.getRegistrar().registerForOnStop(this);
		indeterminate = view.isIndeterminate();
		enabled = view.isEnabled();
		progress = view.getProgress();
		max = view.getMax();
	}

	/**
	 * Set the maximum for this progress bar
	 * 
	 * @param max
	 */
	public void Max(int max) {
		this.max = max;
		view.setMax(max);
	}

	/**
	 * Set the component's enabled status.
	 * 
	 * @param enabled
	 */
	public void Enabled(boolean enabled) {
		this.enabled = enabled;
		view.setEnabled(enabled);
	}

	/**
	 * 
	 * @return This component's enabled status
	 */
	public boolean Enabled() {
		return enabled;
	}

	/**
	 * 
	 * @return What this progress bar's max is set to.
	 */
	public int Max() {
		return max;
	}

	/**
	 * Set the progress of the progressbar. This only applies to
	 * non-indeterminate progressbars (horizontal). It will be ignored
	 * otherwise.
	 * 
	 * @param progress
	 *            Set the progress of the bar
	 */
	public void Progress(int progress) {
		this.progress = progress;
		view.setProgress(progress);
	}

	/**
	 * 
	 * @return What the progress bar is currently set to
	 */
	public int Progress() {
		return progress;
	}

	/**
	 * Sets whether this progressbar for an indeterminate amount of time. This
	 * will run a default animation. This only really applies to the horizontal
	 * bar, as the circular are always indeterminate.
	 * 
	 * @param indeterminate
	 */
	public void Indeterminate(boolean indeterminate) {
		this.indeterminate = indeterminate;
		view.setIndeterminate(indeterminate);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	@Override
	public View getView() {
		return view;
	}

	@Override
	public void onStop() {
		if (view.isIndeterminate()) {
			view.setIndeterminate(false);
		}
	}
	
}