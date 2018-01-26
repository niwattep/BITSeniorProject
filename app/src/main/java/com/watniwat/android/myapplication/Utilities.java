package com.watniwat.android.myapplication;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by Niwat on 20-Jan-18.
 */

public class Utilities {
	public Utilities() {
	}

	public static void showToast(String text, Context context) {
		Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
	}
}
