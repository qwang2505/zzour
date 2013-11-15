package com.zzour.android.views;

import java.util.ArrayList;
import java.util.Iterator;

import com.zzour.android.R;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
 
/**
 * 
 * Create custom Dialog windows for your application
 * Custom dialogs rely on custom layouts wich allow you to 
 * create and use your own look & feel.
 * 
 * Under GPL v3 : http://www.gnu.org/licenses/gpl-3.0.html
 * 
 * @author antoine vianey
 *
 */
public class CancelOrderDialog extends Dialog {
 
    public CancelOrderDialog(Context context, int theme) {
        super(context, theme);
    }
 
    public CancelOrderDialog(Context context) {
        super(context);
    }
 
    /**
     * Helper class for creating a custom dialog
     */
    public static class Builder {
        private Context context;
        private String positiveButtonText;
        private String negativeButtonText;
        private View contentView;

        private DialogInterface.OnClickListener positiveButtonClickListener;
        private DialogInterface.OnClickListener negativeButtonClickListener;
        
        private ArrayList<CheckBox> reasonBoxes = new ArrayList<CheckBox>();
        
        // button ids
        private int positiveButtonId = -1;
        private int negativeButtonId = -1;

        public Builder(Context context) {
            this.context = context;
        }
        
        public String getReason(){
        	Iterator<CheckBox> iter = reasonBoxes.iterator();
        	while (iter.hasNext()){
        		CheckBox box = iter.next();
        		if (box.isChecked()){
        			RelativeLayout parent = (RelativeLayout)box.getParent();
        			TextView reason = (TextView)parent.findViewById(R.id.reason_text);
        			return reason.getText().toString();
        		}
        	}
        	// can not reach here
        	return "";
        }

		public int getPositiveButtonId() {
			return positiveButtonId;
		}

		public void setPositiveButtonId(int positiveButtonId) {
			this.positiveButtonId = positiveButtonId;
		}

		public int getNegativeButtonId() {
			return negativeButtonId;
		}

		public void setNegativeButtonId(int negativeButtonId) {
			this.negativeButtonId = negativeButtonId;
		}
		
		/**
         * Set a custom content view for the Dialog.
         * If a message is set, the contentView is not
         * added to the Dialog...
         * @param v
         * @return
         */
        public Builder setContentView(View v) {
            this.contentView = v;
            return this;
        }

        /**
         * Set the positive button resource and it's listener
         * @param positiveButtonText
         * @param listener
         * @return
         */
        public Builder setPositiveButton(int positiveButtonText,
                DialogInterface.OnClickListener listener) {
            this.positiveButtonText = (String) context.getText(positiveButtonText);
            this.positiveButtonClickListener = listener;
            return this;
        }
 
        /**
         * Set the positive button text and it's listener
         * @param positiveButtonText
         * @param listener
         * @return
         */
        public Builder setPositiveButton(String positiveButtonText,
                DialogInterface.OnClickListener listener) {
            this.positiveButtonText = positiveButtonText;
            this.positiveButtonClickListener = listener;
            return this;
        }
 
        /**
         * Set the negative button resource and it's listener
         * @param negativeButtonText
         * @param listener
         * @return
         */
        public Builder setNegativeButton(int negativeButtonText,
                DialogInterface.OnClickListener listener) {
            this.negativeButtonText = (String) context.getText(negativeButtonText);
            this.negativeButtonClickListener = listener;
            return this;
        }
 
        /**
         * Set the negative button text and it's listener
         * @param negativeButtonText
         * @param listener
         * @return
         */
        public Builder setNegativeButton(String negativeButtonText,
                DialogInterface.OnClickListener listener) {
            this.negativeButtonText = negativeButtonText;
            this.negativeButtonClickListener = listener;
            return this;
        }
 
        /**
         * Create the custom dialog
         */
        public CancelOrderDialog create() {
        	// NOTICE before create, must set layout id, style id, positive button id, 
        	// and negative button id
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            // instantiate the dialog with the custom Theme
            final CancelOrderDialog dialog = new CancelOrderDialog(context, R.style.Dialog);
            View layout = inflater.inflate(R.layout.cancel_order_dialog, null);
            dialog.addContentView(layout, new LayoutParams(
                    LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
            // set the confirm button
            if (positiveButtonText != null) {
                ((Button) layout.findViewById(positiveButtonId))
                        .setText(positiveButtonText);
                if (positiveButtonClickListener != null) {
                    ((Button) layout.findViewById(positiveButtonId))
                            .setOnClickListener(new View.OnClickListener() {
                                public void onClick(View v) {
                                    positiveButtonClickListener.onClick(
                                    		dialog, 
                                            DialogInterface.BUTTON_POSITIVE);
                                }
                            });
                }
            }
            // set the cancel button
            if (negativeButtonText != null) {
                ((Button) layout.findViewById(negativeButtonId))
                        .setText(negativeButtonText);
                if (negativeButtonClickListener != null) {
                    ((Button) layout.findViewById(negativeButtonId))
                            .setOnClickListener(new View.OnClickListener() {
                                public void onClick(View v) {
                                	negativeButtonClickListener.onClick(
                                    		dialog, 
                                            DialogInterface.BUTTON_NEGATIVE);
                                }
                            });
                }
            }
            dialog.setContentView(layout);
            CheckBox r1 = (CheckBox)layout.findViewById(R.id.reason1);
            r1.setChecked(true);
            this.reasonBoxes.add(r1);
            CheckBox r2 = (CheckBox)layout.findViewById(R.id.reason2);
            this.reasonBoxes.add(r2);
            CheckBox r3 = (CheckBox)layout.findViewById(R.id.reason3);
            this.reasonBoxes.add(r3);
            CheckBox r4 = (CheckBox)layout.findViewById(R.id.reason4);
            this.reasonBoxes.add(r4);
            // add click listener for check boxes
            Iterator<CheckBox> it = this.reasonBoxes.iterator();
            while (it.hasNext()){
            	CheckBox box = it.next();
            	box.setOnCheckedChangeListener(new OnCheckedChangeListener() {
					@Override
					public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
						if (!isChecked){
							Iterator<CheckBox> iter = reasonBoxes.iterator();
							while (iter.hasNext()){
								CheckBox box = iter.next();
								if (box != buttonView && box.isChecked()){
									// some other box is checked, allow self uncheck, return
									return;
								}
							}
							// no other box is checked, do not allow uncheck
							buttonView.setChecked(true);
						} else {
							Iterator<CheckBox> iter = reasonBoxes.iterator();
							while (iter.hasNext()){
								CheckBox box = iter.next();
								if (box != buttonView && box.isChecked()){
									box.setChecked(false);
								}
							}
						}
					}
				});
            }
            return dialog;
        }
 
    }
 
}