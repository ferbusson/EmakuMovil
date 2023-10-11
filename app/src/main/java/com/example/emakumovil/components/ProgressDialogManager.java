package com.example.emakumovil.components;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Handler;

public class ProgressDialogManager {
	private static ProgressDialog progressDialog;
	private static ProgressThread progressThread;

	public static ProgressDialog show(Activity activity, Handler handler, String message) {
		progressDialog = null;
		progressDialog = new ProgressDialog(activity);
		progressDialog.setMessage(message);
		progressDialog.show();

		progressThread = new ProgressThread(handler,40,200);
		progressThread.start();

		return progressDialog;
	}

	public static ProgressDialog currentDialog() {
		return progressDialog;
	}

	public static ProgressThread thread() {
		return progressThread;
	}

	public static void dismissCurrent() {
		progressDialog.dismiss();
		progressThread.setState(ProgressThread.DONE);
	}
}
