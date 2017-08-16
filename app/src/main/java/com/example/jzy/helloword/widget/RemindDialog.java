package com.example.jzy.helloword.widget;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.example.jzy.helloword.R;


public class RemindDialog extends Dialog implements View.OnClickListener {
	private TextView sureTv;
	private TextView tvMessage;

	public RemindDialog(Context context) {
		super(context, R.style.device_normal_dialog);
		setContentView(R.layout.dialog_remind_layout);
		sureTv = (TextView) findViewById(R.id.dialog_sure_bt);
		tvMessage = (TextView) findViewById(R.id.tv_dialog_message);
		sureTv.setOnClickListener(this);
		show();
	}

	public RemindDialog(Context context, String message) {
		super(context, R.style.device_normal_dialog);
		setContentView(R.layout.dialog_remind_layout);
		sureTv = (TextView) findViewById(R.id.dialog_sure_bt);
		tvMessage = (TextView) findViewById(R.id.tv_dialog_message);
		tvMessage.setText(message);
		sureTv.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		dismiss();
	}

	public void showDialog() {
		show();
	}
}
