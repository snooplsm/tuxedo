package us.wmwm.tuxedo.app;

import twitter4j.DirectMessage;
import twitter4j.ResponseList;
import twitter4j.TwitterException;
import twitter4j.json.DataObjectFactory;
import android.content.ContentValues;
import android.database.Cursor;

public class DirectMessageDAO {

	private DatabaseHelper helper;

	public DirectMessageDAO(DatabaseHelper helper) {
		this.helper = helper;
	}
	
	public DirectMessage getLastDirectMessage(Long userID) {
		Cursor c = helper
				.getReadableDatabase()
				.rawQuery(
						"select json from direct_messages where for_user_id = ? AND json is not null order by created_at desc",
						new String[] {userID.toString()});
		DirectMessage status = null;
		try {
			while (c.moveToNext()) {
				String json = c.getString(0);
				try {
					status = DataObjectFactory.createDirectMessage(json);
				} catch (TwitterException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		} finally {
			c.close();
		}
		return status;
	}

	public void saveDirectMessage(ResponseList<DirectMessage> statuses) {
		ContentValues cv = new ContentValues();
		for(DirectMessage status : statuses) {
			cv.clear();
			saveDirectMessage(status);
		}
		
	}

	public void saveDirectMessage(DirectMessage dm) {
		saveDirectMessage(new ContentValues(), dm);
		
	}

	private void saveDirectMessage(ContentValues cv,
			DirectMessage status) {
		cv.put("id", status.getId());
		cv.put("created_at", status.getCreatedAt().getTime());
		cv.put("user_id", status.getSenderId());
		cv.put("for_user_id",status.getRecipientId());
		cv.put("text", status.getText());
		cv.put("json", DataObjectFactory.getRawJSON(status));
		int update = helper.getWritableDatabase().update("statuses", cv, "id=? AND for_user_id=?", new String[] {String.valueOf(status.getId()), String.valueOf(status.getRecipientId())});
		if(update==0) {
			helper.getWritableDatabase().insert("direct_messages", null, cv);
		}
		
	}

}
