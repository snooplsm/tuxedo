package us.wmwm.tuxedo.views;

import java.util.Calendar;
import java.util.Map;

import twitter4j.Status;
import us.wmwm.tuxedo.R;
import us.wmwm.tuxedo.app.SessionAT;
import us.wmwm.tuxedo.util.Views;
import android.content.Context;
import android.database.Cursor;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.loopj.android.image.SmartImageView;

public class TimelineView extends RelativeLayout {
	
	private TextView screenName, name, text, date;
	
	private SmartImageView image;


	public TimelineView(Context context, AttributeSet attrs) {
		super(context,attrs);
		LayoutInflater.from(context).inflate(R.layout.view_timeline,this);
		screenName = Views.findView(this,R.id.screen_name);
		name = Views.findView(this, R.id.name);
		text = Views.findView(this, R.id.text2);
		image = Views.findView(this, R.id.image);
		date = Views.findView(this, R.id.date);
	}

	public TimelineView(Context context) {
		this(context,null);
	}

	public void setStatus(SessionAT session, Status status) {
		screenName.setText("@"+status.getUser().getScreenName());
		name.setText(status.getUser().getName());
		text.setText(status.getText());
		image.setImageUrl(status.getUser().getBiggerProfileImageURL());
		date.setText(session.getDateFormat().format(status.getCreatedAt()));
	}
	
	public void setStatus(SessionAT session, Cursor c, Map<String,Integer> keys) {
		screenName.setText("@"+c.getString(keys.get("screen_name")));
		name.setText(c.getString(keys.get("name")));
		text.setText(c.getString(keys.get("text")));
		image.setImageUrl(c.getString(keys.get("big_profile_image")));
		Calendar created = Calendar.getInstance();
		created.setTimeInMillis(c.getLong(keys.get("created_at")));		
		date.setText(session.getDateFormat().format(created.getTime()));
	}

}
