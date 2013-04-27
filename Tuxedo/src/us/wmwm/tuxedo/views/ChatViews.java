package us.wmwm.tuxedo.views;

import java.util.Calendar;

import twitter4j.Status;
import us.wmwm.tuxedo.R;
import us.wmwm.tuxedo.app.PendingTweet;
import us.wmwm.tuxedo.services.AdvancedTwitterService;
import android.content.Context;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class ChatViews extends RelativeLayout {

	TextView text;
	EditText edit;
	View send;
	
	AdvancedTwitterService service;
	
	public ChatViews(Context context) {
		super(context);
		
		LayoutInflater.from(context).inflate(R.layout.chatview, this);
		text = (TextView) findViewById(R.id.text);
		edit = (EditText) findViewById(R.id.edit);
		edit.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.showSoftInput(v, 0);
			}
		});
		send = findViewById(R.id.send_button);
		send.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				PendingTweet p = new PendingTweet();
				p.forUserId = service.getApp().getSession().getAuthenticatedUsers().iterator().next().getId();
				p.scheduledFor = Calendar.getInstance();
				p.text = edit.getText().toString();
				service.sendTweet(p);
				onAction.onSend();
			}
		});
		setFocusableInTouchMode(true); // this line is important
		requestFocus();
		setOnKeyListener(new OnKeyListener() {
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if(keyCode==KeyEvent.KEYCODE_BACK) {
					onAction.onBack();
				}
				return false;
			}
		});
	}
	
	public interface OnAction {
		void onSend();
		void onBack();
	}
	
	OnAction onAction;
	
	public void setContext(AdvancedTwitterService service, Status status, OnAction onAction) {
		this.service = service;
		this.onAction = onAction;
		text.setText(status.getText());
		edit.setText("@"+status.getUser().getScreenName());
	}

}
