package com.zzour.android.views.tab;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.Toast;

import com.zzour.andoird.base.BaseActivity;
import com.zzour.android.R;
import com.zzour.android.cache.GlobalMemoryCache;
import com.zzour.android.utils.ActivityTool;

public class Tab {
	private int resourceIcon;
	private int resourceIconSelected = 0;

	private final BaseActivity context;
	private Intent intent;
	private Class<?> cls;

	private View view;
	// what's this button?
	private Button btn;
	// name?
	private final String tabTag;

	// what's this?
	public int preferedHeight = -1;
	private boolean isSelected;
	// tab need a dialog?
	private Dialog dialog;
	// request code?
	private int requestCode = -1;
	// tab button text?
	private String btnText;
	// button text color?
	private int textColor;
	// button text color?
	private int selectedTextColor;
	// what's gradient?
	private GradientDrawable btnGradient;
	private GradientDrawable selectedBtnGradient;
	// tab button text size?
	private float btnTextSize = 14;

	// init tab with activity instance and tag
	public Tab(BaseActivity context, String tabTag) {
		if (context == null) {
			throw new IllegalStateException("Context can't be null");
		}
		// tag and category?
		this.tabTag = tabTag;
		this.context = context;
	}

	public void setIcon(int resourceIcon) {
		// set icon of tab
		this.resourceIcon = resourceIcon;
	}

	public void setIconSelected(int resourceIcon) {
		// set selected resource icon.
		this.resourceIconSelected = resourceIcon;
	}
	
	public void setBtnGradient(GradientDrawable btnGradient) {
		this.btnGradient = btnGradient;
	}
	
	public void setSelectedBtnGradient(GradientDrawable btnGradient) {
		this.selectedBtnGradient = btnGradient;
	}
	
	public void setBtnTextColor(int textColor) {
		this.textColor = textColor;
	}
	
	public void setSelectedBtnTextColor(int textColor) {
		this.selectedTextColor = textColor;
	}
	
	public void setBtnTextSize(float btnTextSize) {
		this.btnTextSize = btnTextSize;
	}

	public void setIntent(Intent intent, int requestForResult, Class cls) {
		this.intent = intent;
		this.requestCode = requestForResult;
		this.cls = cls;
	}

	public void setIntent(Intent intent, Class cls) {
		this.intent = intent;
		this.cls = cls;
	}

	public Intent getIntent() {
		return intent;
	}

	public String getTag() {
		return tabTag;
	}

	public void setSelected(boolean isSelected) {
		this.isSelected = isSelected;
	}

	public void setBtnText(String btnText) {
		this.btnText = btnText;
	}

	public void setDialog(Dialog dialog) {
		this.dialog = dialog;
	}
	
	// view? tab need a view?
	public View getView() {
		if (view == null) {
			createView();
		}
		return view;
	}
	
	private void createView() 
	{
		// inflate button with xml
		btn = (Button)(context.getLayoutInflater().inflate(R.layout.bizbutton, null));

		int iconId = resourceIcon;
		GradientDrawable btnBackGrad = btnGradient;
		int btnTextColor = textColor;
		if (isSelected && resourceIconSelected != 0) {
			iconId = resourceIconSelected;
			btnBackGrad = selectedBtnGradient;
			btnTextColor = selectedTextColor;
		}

		// init button
		btn.setCompoundDrawablesWithIntrinsicBounds(0, iconId, 0, 0);
		btn.setText(btnText);
		btn.setTextColor(btnTextColor);
		btn.setTextSize(btnTextSize);
		btn.setBackgroundDrawable(btnBackGrad);
		btn.setMinimumHeight(preferedHeight);
		btn.setPadding(0, 10, 0, 0);
		
		bindListeners();
		// view is the button? interesting.
		view = btn;
	}

	// bind listener
	private void bindListeners() {
		btn.setOnClickListener(new OnClickListener() {
			public void onClick(View view) {
				if (intent == null && dialog == null) {
					Toast.makeText(context,
							"Intent or Dialog not set for '" + tabTag + "'",
							Toast.LENGTH_SHORT).show();

				} else if (intent != null && dialog != null) {
					Toast.makeText(
							context,
							" Only ONE can be set Intent or Dialog for '"
									+ tabTag + "'", Toast.LENGTH_SHORT).show();
				} else {
					if (intent != null) {
						if (requestCode != -1) {
							// This will start activity for result
						} else {
							ActivityTool.startActivity(context, cls, intent);
							//context.finish();
						}
					} else if (dialog != null) {
						dialog.show();
					}
				}
			}
		});

		btn.setOnTouchListener(new OnTouchListener() {
			public boolean onTouch(View view, MotionEvent e) {
				if (e.getAction() == MotionEvent.ACTION_DOWN) {
					btn.setBackgroundColor(Color.rgb(255, 186, 82));
				} else if (e.getAction() == MotionEvent.ACTION_UP) {
					btn.setBackgroundColor(0x00000000);
				}
				return false;
			}
		});
	}
}