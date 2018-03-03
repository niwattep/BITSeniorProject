package com.watniwat.android.myapplication;

import android.app.Application;
import android.app.ProgressDialog;
import android.content.Context;

/**
 * Created by Niwat on 25-Feb-18.
 */

public class LoadingDialog extends Application {
	private static LoadingDialog dialog = null;
	private ProgressDialog progressDialog = null;
	private Context context = null;

	private LoadingDialog() {
		this.context = getApplicationContext();
		this.progressDialog = new ProgressDialog(context);
		this.progressDialog.setMessage("Loading...");
		this.progressDialog.setIndeterminate(true);
		this.progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
	}

	public static LoadingDialog getInstance() {
		if (dialog == null) {
			dialog = new LoadingDialog();
			return dialog;
		}

		return dialog;
	}

	public void showLoading() {
		if (!progressDialog.isShowing()) {
			progressDialog.show();
		}
	}

	public void hideLoading() {
		if (progressDialog.isShowing()) {
			progressDialog.dismiss();
		}
	}
}
