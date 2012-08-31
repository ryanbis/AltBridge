package com.xiledsystems.AlternateJavaBridgelib.components.altbridge;

import android.app.Activity;
import android.support.v4.app.Fragment;

import com.xiledsystems.AlternateJavaBridgelib.components.Component;
import com.xiledsystems.AlternateJavaBridgelib.components.HandlesEventDispatching;

public class FormFragment extends Fragment implements ComponentContainer, Component {

	@Override
	public HandlesEventDispatching getDispatchDelegate() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Activity $context() {
		// TODO Auto-generated method stub
		return getActivity();
	}

	@Override
	public Form $form() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void $add(AndroidViewComponent component) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setChildWidth(AndroidViewComponent component, int width) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setChildHeight(AndroidViewComponent component, int height) {
		// TODO Auto-generated method stub
		
	}

}
