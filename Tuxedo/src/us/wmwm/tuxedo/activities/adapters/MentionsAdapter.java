package us.wmwm.tuxedo.activities.adapters;

import java.util.List;

import twitter4j.Status;
import us.wmwm.tuxedo.app.SessionAT;
import android.os.Handler;

import com.handmark.pulltorefresh.library.PullToRefreshListView;


public class MentionsAdapter extends TimelineAdapter {

	public MentionsAdapter(PullToRefreshListView list, Handler handler, SessionAT session) {
		super(null, list, handler, session);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	protected List<Status> loadInitial() {
		return session.getStatusDAO().getMentions(50);
	}

}
