package us.wmwm.tuxedo.activities.adapters;

import java.util.List;

import twitter4j.TwitterException;
import twitter4j.User;
import twitter4j.json.DataObjectFactory;
import us.wmwm.tuxedo.app.PagingDAO.Type;
import us.wmwm.tuxedo.app.SessionAT;
import us.wmwm.tuxedo.services.AdvancedTwitterService;
import us.wmwm.tuxedo.views.LeftMenuUserView;
import android.database.Cursor;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;


public class FollowersAdapter extends AdvancedTwitterAdapter<User> {

	private Long userID;

	private AdvancedTwitterService service;

	public FollowersAdapter(AdvancedTwitterService service, Long userID,
			Handler handlerr, SessionAT session) {
		super(service.getApplicationContext(), handlerr, session);
		this.userID = userID;
		this.service = service;
	}

	ListView list;

	@Override
	public View getViewFromChild(int pos, View c, ViewGroup parent) {
		LeftMenuUserView v;
		if (c == null) {
			c = new LeftMenuUserView(parent.getContext());
		}
		v = (LeftMenuUserView) c;

		User user = null;
		try {
			user = DataObjectFactory.createUser(((Cursor)getItem(pos)).getString(0));
		} catch (TwitterException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		v.setUser(user);

		return c;
	}

	@Override
	protected List<User> loadInitial() {
		List<User> users = session.getUserDAO().loadFollowers(userID, 50);
		if (users.isEmpty()) {
			users = service.loadFollowers(userID, userID);
		}
		return users;
	}
	
	@Override
	protected List<User> loadAfter(User data) {
		List<User> after = session.getUserDAO().getFollowersAfter(userID, data);
		if(after.isEmpty()) {
			after = service.loadMoreFollowers(userID, userID, data.getId()); 
		}
		return after;
	}

	@Override
	protected boolean canLoadBefore(User data) {
		return true;
	}
	
	protected boolean canLoadAfter(User data) {
		if(data==null) {
			return false;
		}
		boolean has = session.getUserDAO().getFollowerCountAfter(userID, data)>0;
		if(!has) {
			long cursor = session.getPagingDAO().getNext(Type.FOLLOWERS, userID);
			return cursor!=0;
		}
		return has;
	}
}
