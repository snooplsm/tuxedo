package us.wmwm.tuxedo.activities.adapters;

import twitter4j.DirectMessage;
import twitter4j.StallWarning;
import twitter4j.Status;
import twitter4j.StatusDeletionNotice;
import twitter4j.User;
import twitter4j.UserList;
import twitter4j.UserStreamListener;
import us.wmwm.tuxedo.app.SessionAT;
import android.content.Context;
import android.database.Cursor;
import android.os.Handler;
import android.widget.ListView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;


public abstract class AdvancedTwitterAdapter<T> extends PagingAdapter<T>
		implements UserStreamListener, OnRefreshListener<ListView> {

	public AdvancedTwitterAdapter(Context ctx, Handler handlerr, SessionAT session) {
		super(ctx, handlerr,session);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onBlock(User arg0, User arg1) {

	}

	@Override
	public void onDeletionNotice(long arg0, long arg1) {

	}

	@Override
	public void onDeletionNotice(StatusDeletionNotice arg0) {

	}

	@Override
	public void onDirectMessage(DirectMessage arg0) {

	}

	@Override
	public void onException(Exception arg0) {

	}

	@Override
	public void onFavorite(User arg0, User arg1, Status arg2) {

	}

	@Override
	public void onFollow(User arg0, User arg1) {

	}

	@Override
	public void onFriendList(long[] arg0) {

	}

	@Override
	public void onScrubGeo(long arg0, long arg1) {

	}

	@Override
	public void onStallWarning(StallWarning arg0) {

	}

	@Override
	public void onStatus(Status arg0) {

	}

	@Override
	public void onTrackLimitationNotice(int arg0) {

	}

	@Override
	public void onUnblock(User arg0, User arg1) {

	}

	@Override
	public void onUnfavorite(User arg0, User arg1, Status arg2) {

	}

	@Override
	public void onUserListCreation(User arg0, UserList arg1) {

	}

	@Override
	public void onUserListDeletion(User arg0, UserList arg1) {

	}

	@Override
	public void onUserListMemberAddition(User arg0, User arg1, UserList arg2) {

	}

	@Override
	public void onUserListMemberDeletion(User arg0, User arg1, UserList arg2) {

	}

	@Override
	public void onUserListSubscription(User arg0, User arg1, UserList arg2) {

	}

	@Override
	public void onUserListUnsubscription(User arg0, User arg1, UserList arg2) {

	}

	@Override
	public void onUserListUpdate(User arg0, UserList arg1) {

	}

	@Override
	public void onUserProfileUpdate(User arg0) {

	}

	public Mode getMode() {
		return Mode.DISABLED;
	}
	
	@Override
	public void onRefresh(PullToRefreshBase<ListView> refreshView) {
		// TODO Auto-generated method stub
		
	}

	public void onTimeline() {
		// TODO Auto-generated method stub		
	}
}
