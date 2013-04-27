package us.wmwm.tuxedo.activities.adapters;

import java.util.ArrayList;
import java.util.List;

import twitter4j.User;
import us.wmwm.tuxedo.app.SessionAT;
import us.wmwm.tuxedo.views.LeftMenuFollowerView;
import us.wmwm.tuxedo.views.LeftMenuFollowingView;
import us.wmwm.tuxedo.views.LeftMenuLabelView;
import us.wmwm.tuxedo.views.LeftMenuUserView;
import us.wmwm.tuxedo.views.UserView;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import us.wmwm.tuxedo.R;

public class LeftMenuAdapter extends BaseAdapter {

	public static final int USER_VIEW_POS = 0;

	public static final int FOLLOWING_VIEW_POS = 1;

	public static final int FOLLOWERS_VIEW_POS = 2;

	public static final int TIMELINE_VIEW_POS = 3;

	public static final int MENTIONS_VIEW_POS = 4;

	public static final int DM_VIEW_POS = 5;

	public static final int RETWEETS_VIEW_POS = 6;

	public static final int FAVORITES_VIEW_POS = 7;

	public static final int SEARCH_VIEW_POS = 8;

	public static final int SETTINGS_VIEW_POS = 9;

	private SessionAT session;

	private Context ctx;

	private List<User> users;
	
	public User getUser() {
		return users.get(0);
	}

	public LeftMenuAdapter(Context ctx, SessionAT session) {
		this.ctx = ctx;
		this.session = session;
		users = new ArrayList<User>(session.getAuthenticatedUsers());
	}

	@Override
	public void notifyDataSetChanged() {
		super.notifyDataSetChanged();
		users.clear();
		users.addAll(session.getAuthenticatedUsers());
	}

	@Override
	public int getCount() {
		return users.size() * 10;
	}

	@Override
	public Object getItem(int position) {
		return null;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getViewTypeCount() {
		return 4;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			if (position == USER_VIEW_POS) {
				convertView = new LeftMenuUserView(parent.getContext());
			} else if (position == FOLLOWING_VIEW_POS) {
				convertView = new LeftMenuFollowingView(parent.getContext());
			} else if (position == FOLLOWERS_VIEW_POS) {
				convertView = new LeftMenuFollowerView(parent.getContext());
			} else if (position >= TIMELINE_VIEW_POS
					&& position <= SETTINGS_VIEW_POS) {
				convertView = new LeftMenuLabelView(parent.getContext());
			}
		}
		if (convertView instanceof UserView) {
			UserView u = (UserView) convertView;
			if (!users.isEmpty()) {
				u.setUser(users.get(0));
			}
		}
		if (position >= TIMELINE_VIEW_POS && position <= SETTINGS_VIEW_POS) {
			LeftMenuLabelView view = (LeftMenuLabelView) convertView;
			if (position == TIMELINE_VIEW_POS) {
				view.setText(R.string.label_timeline);
			} else if (position == MENTIONS_VIEW_POS) {
				view.setText(R.string.label_mentions);
			} else if (position == DM_VIEW_POS) {
				view.setText(R.string.label_dm);
			} else if (position == RETWEETS_VIEW_POS) {
				view.setText(R.string.label_retweets);
			} else if (position == FAVORITES_VIEW_POS) {
				view.setText(R.string.label_favorites);
			} else if (position == SEARCH_VIEW_POS) {
				view.setText(R.string.label_search);
			} else if (position == SETTINGS_VIEW_POS) {
				view.setText(R.string.label_settings);
			}
		}
		return convertView;
	}

	private Runnable update = new Runnable() {
		public void run() {
			notifyDataSetChanged();
		};
	};
}
