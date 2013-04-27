package us.wmwm.tuxedo.app;

import twitter4j.ResponseList;
import twitter4j.Status;
import twitter4j.UserMentionEntity;
import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

public class MentionDAO {

	private DatabaseHelper helper;

	public MentionDAO(DatabaseHelper helper) {
		this.helper = helper;
	}
	
	public void saveMentions(Long forUserID, ResponseList<Status> statuses) {
		ContentValues cv = new ContentValues();
		for(Status status : statuses) {
			UserMentionEntity[] ume = status.getUserMentionEntities();
			if(ume==null || ume.length==0) {
				continue;
			}			
			cv.put("status_id", status.getId());
			for(UserMentionEntity e : ume) {
				cv.put("user_id", e.getId());
				helper.getWritableDatabase().insertWithOnConflict("mentions", null, cv, SQLiteDatabase.CONFLICT_REPLACE);
			}
		}
	}

	
}
