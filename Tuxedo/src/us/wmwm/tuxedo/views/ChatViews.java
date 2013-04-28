package us.wmwm.tuxedo.views;

import java.util.Calendar;

import com.loopj.android.image.SmartImageView;

import twitter4j.MediaEntity;
import twitter4j.Status;
import us.wmwm.tuxedo.R;
import us.wmwm.tuxedo.app.PendingTweet;
import us.wmwm.tuxedo.services.AdvancedTwitterService;
import us.wmwm.tuxedo.util.Views;
import android.content.Context;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.RelativeLayout;

public class ChatViews extends RelativeLayout {

	TimelineView text;
	EditText edit;
	View send;
	
	AdvancedTwitterService service;
	
	SmartImageView smart;
	
	public ChatViews(Context context) {
		super(context);
		
		LayoutInflater.from(context).inflate(R.layout.chatview, this);
		text = (TimelineView) findViewById(R.id.text);
		edit = (EditText) findViewById(R.id.edit);
		edit.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.showSoftInput(v, 0);
			}
		});
		send = findViewById(R.id.send_button);
		smart = Views.findView(this, R.id.icon2);
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
		
		send.setOnFocusChangeListener(new OnFocusChangeListener() {
			
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if(!hasFocus) {
					ChatViews.this.requestFocus();
				}
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
		text.setStatus(service.getApp().getSession(), status);
		edit.setText("@"+status.getUser().getScreenName()+ " ");
		if(status.getMediaEntities()!=null && status.getMediaEntities().length>0) {
			MediaEntity e = status.getMediaEntities()[0];
			if("photo".equals(e.getType())) {
				smart.setImageUrl(e.getMediaURL());
				smart.setVisibility(View.VISIBLE);
			}
		}
	}

}
