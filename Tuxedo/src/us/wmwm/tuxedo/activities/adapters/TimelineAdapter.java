package us.wmwm.tuxedo.activities.adapters;

import java.util.List;

import twitter4j.Status;
import us.wmwm.tuxedo.app.SessionAT;
import us.wmwm.tuxedo.services.AdvancedTwitterService;
import android.content.Context;
import android.database.Cursor;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshListView;


public class TimelineAdapter extends StatusAdapter {

	AdvancedTwitterService service;
	
	SessionAT session;
	
	PullToRefreshListView pull;
	
	public TimelineAdapter(AdvancedTwitterService service, PullToRefreshListView list, Handler handler, SessionAT session) {
		super(service.getApplicationContext(), list.getRefreshableView(), handler, session);
		this.pull = list;
		this.service = service;
		this.session = session;
	}
	

	
	@Override
	protected List<Status> loadInitial() {
		List<Status> s = session.getStatusDAO().getNewestStatuses(40);
		if(!s.isEmpty()) {
			Status status = s.get(0);
			service.updateStatusNotification(status.getUser().getId(), status);
		}
		return s;
	}
	
	@Override
	public Mode getMode() {
		return Mode.PULL_FROM_START;
	}

	@Override
	public void onRefresh(PullToRefreshBase<ListView> refreshView) {
		super.onRefresh(refreshView);
		service.loadMoreTweets(session.getAuthenticatedUsers().iterator().next().getId());
	}



	@Override
	public View newView(Context arg0, Cursor arg1, ViewGroup arg2) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public void onTimeline() {
		super.onTimeline();
		pull.onRefreshComplete();
		notifyDataSetChanged();
	}


}
