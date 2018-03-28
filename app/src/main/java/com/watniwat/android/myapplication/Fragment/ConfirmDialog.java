package com.watniwat.android.myapplication.Fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

/**
 * Created by Niwat on 26-Mar-18.
 */

public class ConfirmDialog extends DialogFragment {
	public enum Button {
		POSITIVE,
		NEGATIVE
	}

	private static final String MESSAGE_KEY = "message-key";
	private static final String NEGATIVE_KEY = "negative-key";
	private static final String POSITIVE_KEY = "positive-key";

	private OnFinishDialogListener mListener;

	public ConfirmDialog() {}

	public static ConfirmDialog newInstance(String msg, String posText, String negText) {
		ConfirmDialog dialog = new ConfirmDialog();
		Bundle args = new Bundle();
		args.putString(MESSAGE_KEY, msg);
		args.putString(NEGATIVE_KEY, negText);
		args.putString(POSITIVE_KEY, posText);
		dialog.setArguments(args);
		return dialog;
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		String msg = getArguments().getString(MESSAGE_KEY);
		String negText = getArguments().getString(NEGATIVE_KEY);
		String posText = getArguments().getString(POSITIVE_KEY);

		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
				.setMessage(msg)
				.setPositiveButton(posText, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialogInterface, int i) {
						if (mListener != null) {
							mListener.onFinish(Button.POSITIVE);
						}
					}
				})
				.setNegativeButton(negText, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialogInterface, int i) {
						if (mListener != null) {
							mListener.onFinish(Button.NEGATIVE);
						}
					}
				});
		return builder.create();
	}

	public void setOnFinishDialogListener(OnFinishDialogListener listener) {
		this.mListener = listener;
	}

	public interface OnFinishDialogListener {
		void onFinish(ConfirmDialog.Button button);
	}
}
