package us.wmwm.tuxedo.app;

import java.util.Calendar;

import android.database.Cursor;

public class PendingTweet {
	
	public String text;
	
	public Long created;
	
	public Calendar scheduledFor;
	
	public Long forUserId;
	
	public Long inReplyTo;
	
	public Long id;
	
	public Long statusId;
	
	public Float latitude;
	
	public Float longitude;
	
	public String image;

	public PendingTweet() {
		
	}
	
	public PendingTweet(Cursor c) {
		text = c.getString(c.getColumnIndex("text"));
		created = c.getLong(c.getColumnIndex("created"));
		if(!c.isNull(c.getColumnIndex("scheduled_for"))) {
			Long sf = c.getLong(c.getColumnIndex("scheduled_for"));
			Calendar cal= Calendar.getInstance();
			cal.setTimeInMillis(sf);
			scheduledFor = cal;
		}
		forUserId = c.getLong(c.getColumnIndex("for_user_id"));
		if(!c.isNull(c.getColumnIndex("inReplyTo"))) {
			inReplyTo = c.getLong(c.getColumnIndex("inReplyTo"));
		}
		if(!c.isNull(c.getColumnIndex("id"))) {
			id = c.getLong(c.getColumnIndex("id"));
		}
		
	}
	
	public static enum Status {
		SUCCESS, FAILURE, NOT_POSSIBLE
	}
}
