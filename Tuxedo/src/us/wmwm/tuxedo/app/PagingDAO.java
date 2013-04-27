package us.wmwm.tuxedo.app;

import twitter4j.PagableResponseList;
import twitter4j.User;
import android.content.ContentValues;
import android.database.Cursor;

public class PagingDAO {

	private DatabaseHelper helper;

	public PagingDAO(DatabaseHelper helper) {
		this.helper = helper;
	}
	
	public void save(Type type, Long forUserID, Long from, PagableResponseList<User> followers, boolean isTopOff) {
		ContentValues cv = new ContentValues();
		cv.put("from_cursor", from);
		cv.put("next_cursor", followers.getNextCursor());
		cv.put("previous_cursor", followers.getPreviousCursor());
		cv.put("for_user_id", forUserID);
		long time = System.currentTimeMillis();
		if(isTopOff) {
			cv.put("created", 1);
		} else {
			cv.put("created", time);
		}
		for(User u : followers) {
			System.out.println(u.getScreenName());
			cv.put("user_id", u.getId());
			helper.getWritableDatabase().insert(type.table, null, cv);
		}
		
	}
	
	public static enum Type {
		FOLLOWERS("paging_followers"), FOLLOWING("paging_following");
		
		String table;
		
		private Type(String table) {
			this.table = table;
		}
	}
	
	public void deleteAll() {
		helper.getWritableDatabase().delete(Type.FOLLOWERS.table, null, null);
		helper.getWritableDatabase().delete(Type.FOLLOWING.table, null, null);
	}
	
	public long getNext(Type type, Long userID) {
		Cursor c = 
		helper.getReadableDatabase().query(type.table, new String[] {"next_cursor"}, "user_id=?", new String[] {userID.toString()}, (String)null, (String) null, "id", "1");
		long next = -1;
		if(c.moveToNext()) {
			next = c.getLong(0);
		}
		c.close();
		return next;
	}
	
	public long getPrevious(Type type) {
		Cursor c = helper.getReadableDatabase().query(type.table, new String[] {"previous_cursor"}, null, null, null,null, "id asc", "1");
		long next = -1;
		if(c.moveToNext()) {
			next = c.getLong(0);
		}
		c.close();
		return next;
	}
	
}
