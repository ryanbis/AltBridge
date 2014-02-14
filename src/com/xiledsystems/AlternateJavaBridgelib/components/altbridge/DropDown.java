package com.xiledsystems.AlternateJavaBridgelib.components.altbridge;

import java.util.ArrayList;
import java.util.List;

import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;

import com.xiledsystems.AlternateJavaBridgelib.components.events.EventDispatcher;
import com.xiledsystems.AlternateJavaBridgelib.components.events.Events;
import com.xiledsystems.altbridge.components.view.BridgeSpinner;


public class DropDown extends AndroidViewComponent implements OnItemSelectedListener, OnInitializeListener {

	private final BridgeSpinner view;
	private List<String> elements;
	private String current;
	private boolean initialized;
	private boolean manual;

	/**
	 * Constructor for the DropDown visible component. Use this constructor if
	 * you are doing your UI design in code.
	 * 
	 * @param container
	 *            - The container this visible component will reside in
	 */
	public DropDown(ComponentContainer container) {
		super(container);
		view = new BridgeSpinner(container.$context());
		view.setOnItemSelectedAlwaysListener(this);
		container.getRegistrar().registerForOnInitialize(this);
		container.$add(this);
		elements = new ArrayList<String>();
	}

	/**
	 * 
	 * Constructor for the DropDown visible component. Use this constructor when
	 * you've placed the DropDown in the GLE.
	 * 
	 * @param container
	 *            - Always use "this" (without the quotes) for this parameter.
	 * @param resourceId
	 *            - The resource id of the DropDown you placed in the GLE (ex.
	 *            R.id.aBdropDown1)
	 */
	public DropDown(ComponentContainer container, int resourceId) {
		super(container, resourceId);
		view = (BridgeSpinner) container.getRegistrar().findViewById(resourceId);
		view.setOnItemSelectedAlwaysListener(this);
		container.getRegistrar().registerForOnInitialize(this);
		elements = new ArrayList<String>();
	}

	/**
	 * 
	 * @return String array of the elements in the DropDown list.
	 */
	public String[] getElements() {
		String[] temp = (String[]) elements.toArray();
		return temp;
	}

	/**
	 * Sets the elements in the Dropdown list
	 * 
	 * @param elements
	 *            - String array of elements
	 */
	public void Elements(String[] elements) {
		if (elements.length > 0) {
			manual = true;
			this.elements.clear();
			for (String element : elements) {
				this.elements.add(element);
			}
			ArrayAdapter<CharSequence> adapter = new ArrayAdapter<CharSequence>(container.$context(), 
					android.R.layout.simple_spinner_item, elements);
			adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			view.setAdapter(adapter);
		}
	}

	public void Elements(ArrayList<String> elements) {
		if (elements.size() > 0) {
			manual = true;
			this.elements.clear();
			this.elements = elements;
			String[] elems = new String[this.elements.size()];
			for (int i = 0; i < elements.size(); i++) {
				elems[i] = this.elements.get(i);
			}
			ArrayAdapter<CharSequence> adapter = new ArrayAdapter<CharSequence>(container.$context(), 
					android.R.layout.simple_spinner_item, elems);
			adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			view.setAdapter(adapter);
		}
	}

	/**
	 * Set the prompt to display before user makes a choice.
	 * 
	 * @param prompt
	 */
	public void Prompt(String prompt) {
		view.setPrompt(prompt);
	}

	/**
	 * Simulates a click of the dropdown button (useful if you want to use the
	 * context picker that comes up without the dropdown button).
	 */
	public void PerformClick() {
		view.performClick();
		view.setOnItemSelectedListener(this);
	}

	/**
	 * Get the current prompt, if any.
	 * 
	 * @return
	 */
	public String Prompt() {
		return view.getPrompt().toString();
	}

	/**
	 * 
	 * @return the current selection.
	 */
	public String getCurrentSelection() {
		return this.current;
	}

	/**
	 * Set what item the DropDown list is set on.
	 * 
	 * @param element
	 *            the item to set the selection to.
	 */
	@SuppressWarnings("unchecked")
	public void setSelection(String element) {
		ArrayAdapter<CharSequence> adapter;
		manual = true;
		adapter = (ArrayAdapter<CharSequence>) view.getAdapter();
		int position = adapter.getPosition(element);
		view.setSelection(position);
		if (position != -1) {
			current = element;
		}
	}

	/**
	 * This allows you to set the layout type for the Dropdown view. You can
	 * design your own layot in the GLE, then pass the layout into the DropDown.
	 * You must have a listview in there. This hasn't been tested very much/at
	 * all.
	 * 
	 * @param layoutid
	 *            resource Id of the layout
	 * @param textviewid
	 *            the textview resource id (this is where the elements are
	 *            displayed)
	 *            
	 */
	public void SelectorLayout(int buttonLayoutId, int textviewid, int dropdownItemId) {
		String[] things = new String[elements.size()];
		manual = true;
		for (int i = 0; i < elements.size(); i++) {
			things[i] = elements.get(i);
		}
		ArrayAdapter<CharSequence> adapter = new ArrayAdapter<CharSequence>(container.$context(), buttonLayoutId, textviewid, things);
		adapter.setDropDownViewResource(dropdownItemId);
		view.setAdapter(adapter);
	}
	
	public void UnsetManual() {
		manual = false;
	}

	/**
	 * This is for more advanced users who want to be able to tap into the
	 * android view this component is based on. This allows you more flexibility
	 * with the bridge, should you need it.
	 */
	@Override
	public View getView() {
		return view;
	}

	/**
	 * This will override the current event dispatcher! Use this only if you
	 * know how to use it!
	 * 
	 * @param onItemSelectedListener
	 */
	public void setSelectedListener(OnItemSelectedListener listener) {
		view.setOnItemSelectedListener(listener);
	}

	/**
	 * This is used internally, you should never have to call it yourself.
	 */
	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
		if (initialized && !manual) {
			String item = elements.get(pos);
			if (eventListener != null) {
				eventListener.eventDispatched(Events.AFTER_SELECTION, item, pos);
			} else {
				EventDispatcher.dispatchEvent(this, Events.AFTER_SELECTION, item, pos);
			}
			current = item;
		}
		if (manual) {
			manual = false;
		}
	}

	/**
	 * Internal method. No need to call this.
	 */
	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
		// Do nothing

	}

	/**
	 * Internal method. No need to call this.
	 */
	@Override
	public void onInitialize() {
		super.onInitialize();
		initialized = true;
	}
		
}
