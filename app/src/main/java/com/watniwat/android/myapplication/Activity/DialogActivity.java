package com.watniwat.android.myapplication.Activity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by Niwat on 25-Feb-18.
 */

public class DialogActivity extends AppCompatActivity {
	private ProgressDialog dialog;

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		dialog = new ProgressDialog(this);
	}

	public void showLoading() {
		if (!isLoading()) {
			dialog.setMessage("Loading...");
			dialog.setIndeterminate(true);
			dialog.show();
		}
	}

	public void hideLoading() {
		if (isLoading()) {
			dialog.dismiss();
		}
	}

	public boolean isLoading() {
		return dialog.isShowing();
	}
}
