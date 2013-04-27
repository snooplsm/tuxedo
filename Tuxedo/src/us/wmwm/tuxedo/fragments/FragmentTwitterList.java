package us.wmwm.tuxedo.fragments;

import us.wmwm.tuxedo.R;
import us.wmwm.tuxedo.activities.adapters.AdvancedTwitterAdapter;
import android.widget.AdapterView.OnItemClickListener;

import com.handmark.pulltorefresh.library.PullToRefreshListView;

public class FragmentTwitterList extends FragmentBase {

	PullToRefreshListView list;
	
	@Override
	public int getResourceView() {
		return R.layout.fragment_twitter_list;
	}

	@Override
	protected void initializeViews() {
		list = findView(R.id.list);
	}
	
	public void setAdapter(AdvancedTwitterAdapter<?> adapter) {
		list.setAdapter(adapter);
		list.setOnRefreshListener(adapter);
	}
	
	public void setOnItemClickListener(OnItemClickListener listener) {
		list.setOnItemClickListener(listener);
	}

	public PullToRefreshListView getList() {
		return list;
	}
}
