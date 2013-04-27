package us.wmwm.tuxedo.app;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import twitter4j.ResponseList;
import twitter4j.Status;
import twitter4j.TwitterException;
import twitter4j.User;
import twitter4j.auth.AccessToken;
import twitter4j.json.DataObjectFactory;
import us.wmwm.tuxedo.app.PagingDAO.Type;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;


public class UserDAO {

	private DatabaseHelper helper;

	public UserDAO(DatabaseHelper helper) {
		this.helper = helper;
	}

	public void saveAuthedUser(AccessToken token) {
		ContentValues cv = new ContentValues(4);
		cv.put("id", token.getUserId());
		cv.put("screen_name", token.getScreenName());
		cv.put("oauth_token", token.getToken());
		cv.put("oauth_token_secret", token.getTokenSecret());
		helper.getWritableDatabase().insertWithOnConflict("users", null, cv,
				SQLiteDatabase.CONFLICT_REPLACE);		
	}

	public Map<Long, AccessToken> getAuthedUserIDs() {
		Cursor c = helper
				.getReadableDatabase()
				.rawQuery(
						"select id, oauth_token, oauth_token_secret from users where oauth_token is not null AND oauth_token_secret is not null",
						null);
		Map<Long, AccessToken> users = new HashMap<Long, AccessToken>();
		try {
			while (c.moveToNext()) {
				Long id = c.getLong(0);
				String oauthToken = c.getString(1);
				String oauthTokenSecret = c.getString(2);
				AccessToken token = new AccessToken(oauthToken,
						oauthTokenSecret);
				users.put(id, token);
			}
		} finally {
			c.close();
		}
		return users;
	}

	public Map<User, AccessToken> getAuthedUsers() {
		Map<Long, AccessToken> userIDs = getAuthedUserIDs();
		Map<User, AccessToken> users = new HashMap<User, AccessToken>();
		for (Long id : userIDs.keySet()) {
			Cursor c = helper.getReadableDatabase().rawQuery(
					"select json from users where json is not null AND id=" + id, null);
			if (c.moveToNext()) {
				try {
					User user = DataObjectFactory.createUser(c.getString(0));
					users.put(user, userIDs.get(id));
				} catch (TwitterException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			c.close();
		}
		return users;
	}

	public void saveUser(User user, AccessToken token) {
		// TODO Auto-generated method stub
		ContentValues cv = new ContentValues();
		cv.put("screen_name", user.getScreenName());
		cv.put("id", user.getId());
		cv.put("created_at", user.getCreatedAt().getTime());
		cv.put("name", user.getName());
		cv.put("description", user.getDescription());
		cv.put("big_profile_image", user.getBiggerProfileImageURL());
		cv.put("json", DataObjectFactory.getRawJSON(user));		
		if (token != null) {
			cv.put("oauth_token", token.getToken());
			cv.put("oauth_token_secret", token.getTokenSecret());
		}
		if (helper.getWritableDatabase().update("users", cv,
				"id=" + user.getId(), null) == 0) {
			helper.getWritableDatabase().insertWithOnConflict("users", null,
					cv, SQLiteDatabase.CONFLICT_REPLACE);
		}
	}
	
	public void saveUsers(Iterable<User> iterable) {
		for(User user : iterable) {
			saveUser(user, null);
		}
	}
	
	public void saveUsers(ResponseList<Status> statuses) {
		for(Status status : statuses) {
			saveUser(status.getUser(), null);
			if(status.getRetweetedStatus()!=null) {
				saveUser(status.getRetweetedStatus().getUser(), null);
			}
		}
	}

	public List<User> loadFollowers(Long userID, int limit) {
		return load(Type.FOLLOWERS, userID, limit);
	}
	
	public List<User> load(Type type, Long userID, int limit) {
		Cursor c = helper.getReadableDatabase().rawQuery("select u.json from users u, :table p where u.json is not null AND p.for_user_id=? AND p.user_id=u.id order by p.id asc limit ".replaceAll(":table", type.table) + limit, new String[] { userID.toString()});
		List<User> users = new ArrayList<User>(c.getCount());
		while(c.moveToNext()) {
			String json = c.getString(0);
			User u;
			try {
				u = DataObjectFactory.createUser(json);
				users.add(u);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}		
		}
		c.close();
		return users;
	}
	
	public int getFollowerCountBefore(Long userID, User data) {
		Cursor c = helper.getReadableDatabase().rawQuery("select count(*) from paging_followers where for_user_id = ? AND id < (select id from paging_followers where for_user_id=? AND user_id=?)", new String[] { userID.toString(), userID.toString(), String.valueOf(data.getId()) });
		c.moveToNext();
		try {
			return c.getInt(0);
		} finally {
			c.close();
		}
	}


	public int getFollowerCountAfter(Long userID, User data) {
		return getCountAfter(Type.FOLLOWERS, userID, data);
	}
	
	public int getFollowingCountAfter(Long userID, User data) {
		return getCountAfter(Type.FOLLOWING, userID, data);
	}
	
	private int getCountAfter(Type type, Long userID, User data) {
		Cursor c = helper.getReadableDatabase().rawQuery("select count(*) from :table where for_user_id = ? AND id > (select id from :table where for_user_id=? AND user_id=?)".replaceAll(":table", type.table), new String[] { userID.toString(), userID.toString(), String.valueOf(data.getId()) });
		c.moveToNext();
		try {
			return c.getInt(0);
		} catch (Exception e) { 
			e.printStackTrace();
			throw new RuntimeException(e);
		}
		finally {
			c.close();
		}
	}

	public List<User> getFollowersAfter(Long userID, User data) {
		return getAfter(Type.FOLLOWERS, userID, data);
	}
	
	public List<User> getFollowingAfter(Long userID, User data) {
		return getAfter(Type.FOLLOWING, userID, data);
	}
	
	private List<User> getAfter(Type type, Long userID, User data) {
		Cursor c = helper.getReadableDatabase().rawQuery("select json from :table p, users u where u.id=p.for_user_id= ? AND p.for_user_id = ? AND p.id > (select k.id from :table k where k.for_user_id=? AND k.user_id=?)".replaceAll(":table", type.table), new String[] { userID.toString(), userID.toString(), userID.toString(), String.valueOf(data.getId()) });
		List<User> users = new ArrayList<User>();
		while(c.moveToNext()) {
			String json = c.getString(0);
			try {
				users.add(DataObjectFactory.createUser(json));
			} catch (TwitterException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		c.close();
		return users;
	}

	public List<User> loadFollowing(Long userID, int limit) {
		return load(Type.FOLLOWING, userID, limit);
	}

	public User getUser(Long userId) {
		Cursor c = helper.getReadableDatabase().rawQuery("select json from users where id=?", new String[] {String.valueOf(userId)});
		User user = null;
		if(c.moveToNext()) {
			try {
				user = DataObjectFactory.createUser(c.getString(0));
			} catch (TwitterException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		c.close();
		return user;
	}

}
