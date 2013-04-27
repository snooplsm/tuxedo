package us.wmwm.tuxedo.activities.adapters;

import java.util.List;

import twitter4j.Status;
import us.wmwm.tuxedo.app.SessionAT;
import android.content.Context;
import android.os.Handler;
import android.widget.ListView;


public class RetweetsAdapter extends StatusAdapter {

	Long userID;
	
	public RetweetsAdapter(Context ctx, Long userID, ListView list, Handler handler, SessionAT session) {
		super(ctx,list, handler, session);
		// TODO Auto-generated constructor stub
		this.userID = userID;
	}

	@Override
	protected List<Status> loadInitial() {
		return session.getStatusDAO().getRetweets(userID, 40);
	}

}
