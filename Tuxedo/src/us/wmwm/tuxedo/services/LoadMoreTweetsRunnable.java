package us.wmwm.tuxedo.services;

import twitter4j.Paging;
import twitter4j.ResponseList;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import us.wmwm.tuxedo.app.StatusDAO;
import us.wmwm.tuxedo.app.UserDAO;
import android.content.Intent;

public class LoadMoreTweetsRunnable implements Runnable {

	AdvancedTwitterService service;
	
	public LoadMoreTweetsRunnable(AdvancedTwitterService service, 
			Twitter twitter) {
		super();
		this.service = service;
		this.twitter = twitter;
	}


	Twitter twitter;

	
	@Override
	public void run() {
		try {
			long id = twitter.getId();
			StatusDAO dao = service.getApp().getSession().getStatusDAO();
			UserDAO users = service.getApp().getSession().getUserDAO();
			long statusID = dao.getLastStatusId(id);
			Paging p = new Paging();
			if(statusID>0) {
				p.setSinceId(statusID);
			}
				p.setCount(200);
			ResponseList<Status> newStatuses = twitter.getHomeTimeline(p);
			System.out.println("LoadMoreTweets " + newStatuses.size());
			try {
				dao.saveStatuses(id, newStatuses);
				users.saveUsers(newStatuses);
			} catch (Exception e) {
				e.printStackTrace();
			}
			Intent i = new Intent();
			i.setAction(AdvancedTwitterService.STATUSES_UPDATED);
			i.putExtra("statuses", newStatuses);
			i.putExtra("user", id);
			service.sendBroadcast(i);
			while(newStatuses.size()>0 && newStatuses.size()>=p.getCount()) {
				Status s = newStatuses.get(0);
				p.setSinceId(s.getId());
				newStatuses = twitter.getHomeTimeline(p);
				System.out.println("LoadMoreTweets " + newStatuses.size());
				dao.saveStatuses(id, newStatuses);
				users.saveUsers(newStatuses);
				i.putExtra("statuses", newStatuses);
				i.putExtra("user", id);
				service.sendBroadcast(i);
			}
		} catch (Exception e) {
			System.out.println("LoadMoreTweets " + e.getMessage());
			e.printStackTrace();
			Intent i = new Intent();
			i.setAction(AdvancedTwitterService.STATUSES_UPDATED);
			i.putExtra("exception", e);
		}
	}

}
