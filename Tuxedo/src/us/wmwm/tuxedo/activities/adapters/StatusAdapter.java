package us.wmwm.tuxedo.activities.adapters;

import twitter4j.Status;
import twitter4j.StatusDeletionNotice;
import us.wmwm.tuxedo.app.SessionAT;
import us.wmwm.tuxedo.views.TimelineView;
import android.content.Context;
import android.database.Cursor;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

public abstract class StatusAdapter extends AdvancedTwitterAdapter<Status> {

	private static final int TRADITIONAL_TIMELINE_POS = 0;
	private static final int MEDIA_TIMELINE_POS = 1;
	private static final int YOUTUBE_TIMELINE_POS = 2;

	protected ListView list;

	public StatusAdapter(Context context, ListView list, Handler handler,
			SessionAT session) {
		super(context, handler, session);
		// TODO Auto-generated constructor stub
		changeCursor(newCursor());
		this.list = list;
	}

	@Override
	public int getViewTypeCount() {
		return 3;
	}

	@Override
	public int getCount() {
		int c = super.getCount();
		return c;
	}

	@Override
	public View getViewFromChild(int position, View convertView,
			ViewGroup parent) {
		if (convertView == null) {
			convertView = new TimelineView(parent.getContext());
		}
		if (!mCursor.moveToPosition(position)) {
			throw new IllegalStateException();
		}
		
		TimelineView v = (TimelineView) convertView;
		v.setStatus(session, mCursor,keys);
		return convertView;
	}

	@Override
	public int getItemViewType(int position) {		
			return TRADITIONAL_TIMELINE_POS;
	}

	@Override
	public void onStatus(final Status status) {
		super.onStatus(status);
		handler.post(new Runnable() {
			@Override
			public void run() {
				final int pos = list.getFirstVisiblePosition();
				//items.add(0, status);
				notifyDataSetChanged();
				list.setSelectionFromTop(pos + 1, 0);
			}
		});
	}

	@Override
	public void onDeletionNotice(final StatusDeletionNotice dn) {
		super.onDeletionNotice(dn);

		handler.post(new Runnable() {

			@Override
			public void run() {
				boolean changed = false;
				int top = list.getFirstVisiblePosition();
				int removed = 0;
//				for (Iterator<Status> s = items.iterator(); s.hasNext();) {
//					Status status = s.next();
//					if (status.getId() == dn.getStatusId()) {
//						s.remove();
//						changed = true;
//						removed--;
//					}
//				}

				if (changed) {
					notifyDataSetChanged();
					list.setSelectionFromTop(top - removed, 0);
				}
			}

		});
	}

	protected Cursor newCursor() {
		return session.getStatusDAO().getStatuses();
	}

}
