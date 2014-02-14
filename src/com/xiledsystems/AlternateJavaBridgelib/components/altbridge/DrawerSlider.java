package com.xiledsystems.AlternateJavaBridgelib.components.altbridge;

import com.xiledsystems.AlternateJavaBridgelib.components.events.EventDispatcher;
import com.xiledsystems.AlternateJavaBridgelib.components.events.Events;
import android.view.View;
import com.xiledsystems.altbridge.eclipse.AB_DrawerSlider;

/**
 * Sliding drawer visible component. This is designed to only work with the GLE component. Drop the drawer into a
 * layout. Then instantiate with the drawer's id.
 * 
 * For any components in the content of the drawer, you can instantiate them as if they were in the Form calling them.
 * 
 * @author Ryan Bis
 * 
 */
public class DrawerSlider extends AndroidViewComponent implements AB_DrawerSlider.OnDrawerCloseListener, AB_DrawerSlider.OnDrawerOpenListener {

	private final AB_DrawerSlider view;
	private boolean animate = true;

	public DrawerSlider(ComponentContainer container, int resId) {
		super(container);
		view = (AB_DrawerSlider) container.getRegistrar().findViewById(resId);
		view.setOnDrawerOpenListener(this);
		view.setOnDrawerCloseListener(this);
	}

	/**
	 * Use this method to turn the animation on/off when opening or closing the drawer.
	 * 
	 * @param animate
	 */
	public void AnimateOnOpenClose(boolean animate) {
		this.animate = animate;
		view.setAnimateOnClick(animate);
	}

	/**
	 * 
	 * @return whether this drawer is using animation on open/close
	 */
	public boolean AnimateOnOpenClose() {
		return animate;
	}

	/**
	 * Manually open the drawer through code.
	 */
	public void Open() {
		if (animate) {
			view.animateOpen();
		} else {
			view.open();
		}
	}

	/**
	 * Manually close the drawer through code.
	 */
	public void Close() {
		if (animate) {
			view.animateClose();
		} else {
			view.close();
		}
	}

	@Override
	public View getView() {
		return view;
	}

	@Override
	public void postAnimEvent() {
		if (eventListener != null) {
			eventListener.eventDispatched(Events.ANIM_MIDDLE);
		} else {
			EventDispatcher.dispatchEvent(this, Events.ANIM_MIDDLE);
		}
	}

	@Override
	public void onDrawerOpened() {
		if (eventListener != null) {
			eventListener.eventDispatched(Events.DRAWER_OPENED);
		} else {
			EventDispatcher.dispatchEvent(this, Events.DRAWER_OPENED);
		}
	}

	@Override
	public void onDrawerClosed() {
		if (eventListener != null) {
			eventListener.eventDispatched(Events.DRAWER_CLOSED);
		} else {
			EventDispatcher.dispatchEvent(this, Events.DRAWER_CLOSED);
		}
	}

}
