package us.wmwm.tuxedo.activities.adapters;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;

import us.wmwm.tuxedo.app.SessionAT;
import android.content.Context;
import android.database.Cursor;
import android.os.Handler;
import android.support.v4.widget.CursorAdapter;
import android.view.View;
import android.view.ViewGroup;


public abstract class PagingAdapter<T> extends CursorAdapter {

	protected SessionAT session;
	protected Handler handler;

	private Future<?> future,futureLoadMore;
	
	protected Map<String, Integer> keys = new HashMap<String,Integer>();
	
	@Override
	public void changeCursor(Cursor cursor) {
		super.changeCursor(cursor);
		keys.clear();
		if(cursor!=null) {
			for(int i = 0; i < cursor.getColumnNames().length; i++) {
				keys.put(cursor.getColumnNames()[i], i);
			}
		}
	}

	public PagingAdapter(Context ctx, Handler handlerr, SessionAT session) {
		super(ctx, null, false);
		this.handler = handlerr;
		this.session = session;
//		future = session.getExecutorService().submit(new Runnable() {
//			@Override
//			public void run() {
//				try {
//				final List<T> data = loadInitial();
//				if (!data.isEmpty()) {
//					handler.post(new Runnable() {
//						@Override
//						public void run() {
//							items.addAll(data);
//							notifyDataSetChanged();
//						}
//					});
//				} else {
//					if(canLoadBefore(null)) {
//						loadBefore(null);
//					}
//				} }
//				catch (Exception e) {
//					Log.e(getClass().getSimpleName(), "initial load", e);
//					throw new RuntimeException(e);
//				}
//			}
//		});
	}

	//protected List<T> items = new ArrayList<T>();

	protected boolean canLoadAfter(T data) {
		return false;
	}

	protected boolean canLoadBefore(T data) {
		return false;
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public final View getView(int position, View convertView, ViewGroup parent) {
//		if(isLastItem(position)) {
//			
//			if(futureLoadMore!=null && !futureLoadMore.isDone()) {
//				
//			} else {
//				final T obj = items.get(position);
//				futureLoadMore = session.getExecutorService().submit(new Runnable() {
//					@Override
//					public void run() {
//						if(canLoadAfter(obj)) {
//							final List<T> after =  loadAfter(obj);
//							if(after.isEmpty()) {
//								return;
//							}
//							handler.post(new Runnable() {
//								public void run() {
//									items.addAll(after);
//									notifyDataSetChanged();
//								};
//							});
//						}
//					}
//				});
//			}
//		}
		return getViewFromChild(position,convertView,parent);
	}
	
	@Override
	public void bindView(View arg0, Context arg1, Cursor arg2) {
		// TODO Auto-generated method stub		
	}
	
	public View newView(Context arg0, Cursor arg1, ViewGroup arg2) {
		return null;
	};
	
	protected abstract View getViewFromChild(int position, View convertView,
			ViewGroup parent);
	
	protected boolean isLastItem(int pos) {
		return pos==getCount()-1;
	}

	protected abstract List<T> loadInitial();
	
	protected List<T> loadBefore(T data) {
		return Collections.emptyList();
	}
	
	protected List<T> loadAfter(T data) {
		return Collections.emptyList();
	}
}
