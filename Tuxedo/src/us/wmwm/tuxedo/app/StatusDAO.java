package us.wmwm.tuxedo.app;

import java.util.ArrayList;
import java.util.List;

import twitter4j.GeoLocation;
import twitter4j.MediaEntity;
import twitter4j.Place;
import twitter4j.ResponseList;
import twitter4j.Status;
import twitter4j.TwitterException;
import twitter4j.URLEntity;
import twitter4j.json.DataObjectFactory;
import android.content.ContentValues;
import android.database.Cursor;

public class StatusDAO {

	private DatabaseHelper helper;

	public StatusDAO(DatabaseHelper helper) {
		this.helper = helper;
	}

	public Status getLastStatus(Long userID) {
		Cursor c = helper
				.getReadableDatabase()
				.rawQuery(
						"select json from statuses where for_user_id = ? AND json is not null order by created_at desc",
						new String[] { userID.toString() });
		Status status = null;
		try {
			while (c.moveToNext()) {
				String json = c.getString(0);
				try {
					status = DataObjectFactory.createStatus(json);
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

	public void saveStatuses(Long forUserID, ResponseList<Status> statuses) {
		ContentValues cv = new ContentValues();
		for (Status status : statuses) {
			cv.clear();
			saveStatus(forUserID, cv, status);
		}
	}

	public void saveStatus(Long forUserID, Status status) {
		saveStatus(forUserID, new ContentValues(), status);
	}

	private void saveStatus(Long forUserID, ContentValues cv, Status status) {
		cv.put("id", status.getId());
		cv.put("in_reply_to_screen_name", status.getInReplyToScreenName());
		cv.put("in_reply_to_user_id", status.getInReplyToUserId());
		cv.put("in_reply_to_status_id", status.getInReplyToStatusId());
		cv.put("current_user_retweet_id", status.getCurrentUserRetweetId());
		cv.put("created_at", status.getCreatedAt().getTime());
		cv.put("user_id", status.getUser().getId());
		cv.put("retweet_count", status.getRetweetCount());
		cv.put("for_user_id", forUserID);
		StringBuilder b = new StringBuilder(status.getText());
		MediaEntity[] media = status.getMediaEntities();
		for(int i = status.getURLEntities().length-1; i>=0; i--) {
			URLEntity e  = status.getURLEntities()[i];
			b.delete(e.getStart(), e.getEnd());
			b.insert(e.getStart(), e.getDisplayURL());
		}
		cv.put("is_retweet", status.isRetweet());
		cv.put("is_retweeted_by_me", status.isRetweetedByMe());
		cv.put("is_favorited", status.isFavorited());
		cv.put("text", b.toString());
		cv.put("source", status.getSource());
		if (status.getRetweetedStatus()!=null) {
			cv.put("retweeted_user_id", status.getRetweetedStatus().getUser().getId());
		}
		if (status.getGeoLocation() != null) {
			GeoLocation g = status.getGeoLocation();
			cv.put("lat", g.getLatitude());
			cv.put("lng", g.getLongitude());
		}
		if(status.getPlace()!=null) {
			Place p = status.getPlace();
			cv.put("place_country",p.getCountry());
			cv.put("place_street_address", p.getStreetAddress());
			cv.put("place_type", p.getPlaceType());
			cv.put("place_name", p.getName());
			cv.put("place_full_name", p.getFullName());
			cv.put("place_url",p.getURL());
		}
		cv.put("json", DataObjectFactory.getRawJSON(status));
		int update = helper.getWritableDatabase().update(
				"statuses",
				cv,
				"id=? AND for_user_id=?",
				new String[] { String.valueOf(status.getId()),
						forUserID.toString() });
		if (update == 0) {
			helper.getWritableDatabase().insert("statuses", null, cv);
		}
	}

	public List<Status> getNewestStatuses(int limit) {
		Cursor c = helper
				.getReadableDatabase()
				.rawQuery(
						"select json from statuses where json is not null order by created_at desc limit "
								+ limit, null);
		List<Status> statuses = new ArrayList<Status>();
		while (c.moveToNext()) {
			String json = c.getString(0);
			try {
				Status s = DataObjectFactory.createStatus(json);
				statuses.add(s);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		c.close();
		return statuses;
	}

	public int deleteStatus(Long statusId) {
		return helper.getWritableDatabase().delete("statuses", "id = ?",
				new String[] { statusId.toString() });
	}

	public List<Status> getMentions(int limit) {
		Cursor c = helper
				.getReadableDatabase()
				.rawQuery(
						"select json from statuses where json is not null order by created_at desc limit "
								+ limit, null);
		List<Status> statuses = new ArrayList<Status>(c.getCount());
		while (c.moveToNext()) {
			String json = c.getString(0);
			try {
				Status s = DataObjectFactory.createStatus(json);
				statuses.add(s);
			} catch (Exception e) {
				// TODO Auto-generated catch blockw
				e.printStackTrace();
			}
		}
		c.close();
		return statuses;
	}

	public List<Status> getRetweets(Long userID, int limit) {
		Cursor c = helper
				.getReadableDatabase()
				.rawQuery(
						"select json from statuses where json is not null AND user_id = ? AND retweet_count > 0 order by created_at desc limit "
								+ limit, new String[] { userID.toString() });
		List<Status> statuses = new ArrayList<Status>(c.getCount());
		while (c.moveToNext()) {
			String json = c.getString(0);
			try {
				Status s = DataObjectFactory.createStatus(json);
				statuses.add(s);
			} catch (Exception e) {
				// TODO Auto-generated catch blockw
				e.printStackTrace();
			}
		}
		c.close();
		return statuses;
	}

	public void savePending(PendingTweet pending) {
		// TODO Auto-generated method stub
		ContentValues cv = new ContentValues();
		cv.put("for_user_id", pending.forUserId);
		if (pending.created == null) {
			pending.created = System.currentTimeMillis();
			cv.put("created", pending.created);
		}
		cv.put("image", pending.image);
		cv.put("text", pending.text);
		if (pending.scheduledFor != null) {
			cv.put("scheduled_for", pending.scheduledFor.getTimeInMillis());
		} else {
			cv.put("scheduled_for", (Long) null);
		}
		if(pending.statusId!=null) {
			cv.put("status_id", pending.statusId);
		}
		cv.put("id", pending.id);
		if (pending.id != null) {
			helper.getWritableDatabase().update("pending_tweets", cv, "id=?",
					new String[] { pending.id.toString() });
		} else {
			pending.id = helper.getWritableDatabase().insert("pending_tweets", null, cv);
		}
	}

	public PendingTweet getPending(Long id) {
		Cursor c = helper.getReadableDatabase().rawQuery(
				"select * from pending_tweets where id=?",
				new String[] { String.valueOf(id) });
		try {
			if (c.moveToNext()) {
				PendingTweet t = new PendingTweet(c);
				return t;
			}
		} finally {
			c.close();
		}
		return null;
	}

	public int getPendingTweetsCount(Long key, Long before) {
		Cursor c = helper.getReadableDatabase().rawQuery("select count(*) from pending_tweets where status_id is null and for_user_id=? and (scheduled_for is null or scheduled_for < ?)", new String[] {String.valueOf(key), String.valueOf(before)});
		c.moveToNext();
		int v = c.getInt(0);
		c.close();
		return v;
		
	}

	public List<PendingTweet> getPendingTweets(Long userID, Long before) {
		Cursor c = helper.getReadableDatabase().rawQuery("select * from pending_tweets where status_id is null and for_user_id=? and (scheduled_for is null or scheduled_for < ?)", new String[] {String.valueOf(userID), String.valueOf(before)});
		List<PendingTweet> pending = new ArrayList<PendingTweet>(c.getCount());
		while(c.moveToNext()) {
			pending.add(new PendingTweet(c));
		}
		c.close();
		return pending;
	}

	public Cursor getStatuses() {
		return helper.getReadableDatabase().rawQuery("select s.id as _id, s.*, u.* from statuses s join users u on (s.user_id=u.id) where s.json is not null order by s.created_at desc", null);
	}

	public long getLastStatusId(long id) {
		Cursor cursor = helper.getReadableDatabase().rawQuery("select s.id from statuses s where s.user_id=? order by s.id desc limit 1", new String[]{String.valueOf(id)});
		long ids = -1;
		if(cursor.moveToNext()) {
			ids = cursor.getLong(0);
		}
		return ids;
		
	}

}
