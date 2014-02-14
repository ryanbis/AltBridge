package com.xiledsystems.AlternateJavaBridgelib.components.altbridge.util;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;


public class CustomDialog extends Dialog {
	
	private static boolean autoDismiss = true;
	
	
	public static class DialogIds {
		private int contentRes;
		private int positiveRes;
		private int neutralRes;
		private int negativeRes;
		private int buttonLayoutRes;
		private int messageRes;
		private int layoutRes;
		private int titleRes;
		private int style;
		
		public int getContentRes() {
			return contentRes;
		}
		public void setContentRes(int contentRes) {
			this.contentRes = contentRes;
		}
		public int getPositiveRes() {
			return positiveRes;
		}
		public void setPositiveRes(int positiveRes) {
			this.positiveRes = positiveRes;
		}
		public int getNeutralRes() {
			return neutralRes;
		}
		public void setNeutralRes(int neutralRes) {
			this.neutralRes = neutralRes;
		}
		public int getNegativeRes() {
			return negativeRes;
		}
		public void setNegativeRes(int negativeRes) {
			this.negativeRes = negativeRes;
		}
		public int getButtonLayoutRes() {
			return buttonLayoutRes;
		}
		public void setButtonLayoutRes(int buttonLayoutRes) {
			this.buttonLayoutRes = buttonLayoutRes;
		}
		public int getMessageRes() {
			return messageRes;
		}
		public void setMessageRes(int messageRes) {
			this.messageRes = messageRes;
		}
		public int getLayoutRes() {
			return layoutRes;
		}
		public void setLayoutRes(int layoutRes) {
			this.layoutRes = layoutRes;
		}
		public int getTitleRes() {
			return titleRes;
		}
		public void setTitleRes(int titleRes) {
			this.titleRes = titleRes;
		}
		public int getStyle() {
			return style;
		}
		public void setStyle(int style) {
			this.style = style;
		}
		
	}
	
	
	public CustomDialog(Context context, int defStyle) {
		super(context, defStyle);
	}
	
	public CustomDialog(Context context) {
		super(context);
		
	}
	
	public void setAutoDismiss(boolean autodismiss) {
		autoDismiss = autodismiss;
	}
	
	public boolean isAutoDismiss() {
		return autoDismiss;
	}

	public static class Builder {
		
		private Context context;
		private String title;
		private String message;
		private String positiveButtonText;
		private String negativeButtonText;
		private String neutralButtonText;
		private View contentView;
		private String[] choices;
		private int selected;
		private boolean cancelable;
		private Spanned spannedText;
		private DialogIds ids;
		
		private DialogInterface.OnClickListener positiveButtonListener;
		private DialogInterface.OnClickListener neutralButtonListener;
		private DialogInterface.OnClickListener negativeButtonListener;
		
		public Builder(Context context) {
			this.context = context;
		}
		
		/**
		 * Set the Dialog message from String
		 * 
		 * @param title
		 * @return
		 */
		public Builder setMessage(String message) {
			this.message = message;
			return this;
		}
		
		/**
		 * Set the Dialog message from String
		 * 
		 * @param title
		 * @return
		 */
		public Builder setMessage(Spanned message) {
			this.spannedText = message;
			return this;
		}

		public Builder setCancelable(boolean cancel) {
			this.cancelable = cancel;
			return this;
		}

		/**
		 * Set the Dialog message from resource
		 * 
		 * @param title
		 * @return
		 */
		public Builder setMessage(int message) {
			this.message = (String) context.getText(message);
			return this;
		}
		
		public Builder setDialogIds(DialogIds ids) {
			this.ids = ids;
			return this;
		}
		
		
		/**
		 * Set the Dialog title from resource
		 * 
		 * @param title
		 * @return
		 */
		public Builder setTitle(int title) {
			this.title = (String) context.getText(title);
			return this;
		}

		/**
		 * Set the Dialog title from String
		 * 
		 * @param title
		 * @return
		 */
		public Builder setTitle(String title) {
			this.title = title;
			return this;
		}

		/**
		 * Set a custom content view for the Dialog. If a message is set, the
		 * contentView is not added to the Dialog...
		 * 
		 * @param v
		 * @return
		 */
		public Builder setContentView(View v) {
			this.contentView = v;
			return this;
		}

		/**
		 * Set the positive button resource and it's listener
		 * 
		 * @param positiveButtonText
		 * @param listener
		 * @return
		 */
		public Builder setPositiveButton(int positiveButtonText, DialogInterface.OnClickListener listener) {
			this.positiveButtonText = (String) context.getText(positiveButtonText);
			this.positiveButtonListener = listener;
			return this;
		}

		/**
		 * Set the positive button text and it's listener
		 * 
		 * @param positiveButtonText
		 * @param listener
		 * @return
		 */
		public Builder setPositiveButton(String positiveButtonText, DialogInterface.OnClickListener listener) {
			this.positiveButtonText = positiveButtonText;
			this.positiveButtonListener = listener;
			return this;
		}

		/**
		 * Set the neutral button text and it's listener
		 * 
		 * @param neutralButtonText
		 * @param listener
		 * @return
		 */
		public Builder setNeutralButton(String neutralButtonText, DialogInterface.OnClickListener listener) {
			this.neutralButtonText = neutralButtonText;
			this.neutralButtonListener = listener;
			return this;
		}

		/**
		 * Set the neutral button resource and it's listener
		 * 
		 * @param neutralButtonText
		 * @param listener
		 * @return
		 */
		public Builder setNeutralButton(int neutralButtonText, DialogInterface.OnClickListener listener) {
			this.neutralButtonText = (String) context.getText(neutralButtonText);
			this.neutralButtonListener = listener;
			return this;
		}

		/**
		 * Set the negative button resource and it's listener
		 * 
		 * @param negativeButtonText
		 * @param listener
		 * @return
		 */
		public Builder setNegativeButton(int negativeButtonText, DialogInterface.OnClickListener listener) {
			this.negativeButtonText = (String) context.getText(negativeButtonText);
			this.negativeButtonListener = listener;
			return this;
		}

		/**
		 * Set the negative button text and it's listener
		 * 
		 * @param negativeButtonText
		 * @param listener
		 * @return
		 */
		public Builder setNegativeButton(String negativeButtonText, DialogInterface.OnClickListener listener) {
			this.negativeButtonText = negativeButtonText;
			this.negativeButtonListener = listener;
			return this;
		}

		public Builder setSingleChoiceItems(String[] choices, int selected, DialogInterface.OnClickListener listener) {
			this.positiveButtonListener = listener;
			this.choices = choices;
			this.selected = selected;
			return this;
		}

		/**
		 * Create the custom dialog
		 */
		public CustomDialog create() {
			LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			if (ids.style == 0) {
				ids.style = context.getResources().getIdentifier("Dialog", "styleable", context.getPackageName());
			}
			final CustomDialog dialog = new CustomDialog(context, ids.style);
			dialog.setCancelable(cancelable);
			View layout = inflater.inflate(ids.layoutRes, null);
			dialog.addContentView(layout, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
			if (title != null) {
				((TextView) layout.findViewById(ids.titleRes)).setText(title);
			} else {
				((TextView) layout.findViewById(ids.titleRes)).setVisibility(View.GONE);
			}
			if (positiveButtonText != null) {
				((Button) layout.findViewById(ids.positiveRes)).setText(positiveButtonText);
				((Button) layout.findViewById(ids.positiveRes)).setOnClickListener(new View.OnClickListener() {
					public void onClick(View v) {
						if (positiveButtonListener != null) {
							positiveButtonListener.onClick(dialog, DialogInterface.BUTTON_POSITIVE);
						}
						if (autoDismiss) {
							dialog.dismiss();
						}
					}
				});

			} else {
				layout.findViewById(ids.positiveRes).setVisibility(View.GONE);
			}
			// set the cancel button
			if (negativeButtonText != null) {
				((Button) layout.findViewById(ids.negativeRes)).setText(negativeButtonText);
				((Button) layout.findViewById(ids.negativeRes)).setOnClickListener(new View.OnClickListener() {
					public void onClick(View v) {
						if (negativeButtonListener != null) {
							negativeButtonListener.onClick(dialog, DialogInterface.BUTTON_NEGATIVE);
						}
						if (autoDismiss) {
							dialog.dismiss();
						}
					}
				});
			} else {
				// if no confirm button just set the visibility to GONE
				layout.findViewById(ids.negativeRes).setVisibility(View.GONE);
			}
			if (neutralButtonText != null) {
				((Button) layout.findViewById(ids.neutralRes)).setText(neutralButtonText);
				((Button) layout.findViewById(ids.neutralRes)).setOnClickListener(new View.OnClickListener() {
					public void onClick(View v) {
						if (neutralButtonListener != null) {
							neutralButtonListener.onClick(dialog, DialogInterface.BUTTON_NEUTRAL);
						}
						if (autoDismiss) {
							dialog.dismiss();
						}
					}
				});

			} else {
				layout.findViewById(ids.neutralRes).setVisibility(View.GONE);
			}
			if (positiveButtonText == null && neutralButtonText == null && negativeButtonText == null) {
				layout.findViewById(ids.buttonLayoutRes).setVisibility(View.GONE);
			}
			// set the content message
			if (message != null) {
				((TextView) layout.findViewById(ids.messageRes)).setText(message);
			} else {
				if (spannedText != null) {
					((TextView) layout.findViewById(ids.messageRes)).setText(spannedText);
				} else {
					((TextView) layout.findViewById(ids.messageRes)).setVisibility(View.GONE);
				}
			}
			if (choices != null) {
				ListView lView = new ListView(context);
				lView.setAdapter(new ArrayAdapter<String>(context, android.R.layout.simple_list_item_single_choice, choices));
				lView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
				if (selected > -1 && selected < choices.length) {
					lView.setSelection(selected);
				}
				lView.setOnItemClickListener(new OnItemClickListener() {
					public void onItemClick(AdapterView<?> arg0, View arg1, int pos, long arg3) {
						positiveButtonListener.onClick(dialog, pos);
					}
				});
				android.widget.LinearLayout.LayoutParams parms = new android.widget.LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
				parms.bottomMargin = 8;
				((LinearLayout) layout.findViewById(ids.contentRes)).addView(lView, new android.widget.LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT,
						LayoutParams.WRAP_CONTENT, 0));
			}
			if (contentView != null) {
				// if no message set, add the contentView to the dialog body
				// ((LinearLayout)
				// layout.findViewById(R.id.content)).removeAllViews();
				((LinearLayout) layout.findViewById(ids.contentRes)).addView(contentView, new LayoutParams(LayoutParams.MATCH_PARENT,
							LayoutParams.WRAP_CONTENT));				
			}
			dialog.setContentView(layout);
			return dialog;
		}
		
	}

}
