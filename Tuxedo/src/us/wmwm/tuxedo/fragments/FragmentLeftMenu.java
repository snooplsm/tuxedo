package us.wmwm.tuxedo.fragments;

import us.wmwm.tuxedo.services.AdvancedTwitterService;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;

import us.wmwm.tuxedo.R;

public class FragmentLeftMenu extends FragmentBase {

	ListView list;
	
	BaseAdapter adapter;
	
	View close;
	
	BroadcastReceiver receiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			System.out.println("user updated");
			adapter.notifyDataSetChanged();
		}
	};
	
	@Override
	public int getResourceView() {
		return R.layout.fragment_left_menu;
	}

	@Override
	protected void initializeViews() {
		list = findView(R.id.list);
		close = findView(R.id.close);
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}
	
	public void setAdapter(BaseAdapter adapter) {
		this.adapter = adapter;		
		list.setAdapter(adapter);
	}
	
	public void setOnItemClickListener(OnItemClickListener listener) {
		list.setOnItemClickListener(listener);
	}
	
	public void setCloseListener(OnClickListener l) {
		close.setOnClickListener(l);
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getActivity().registerReceiver(receiver, new IntentFilter(AdvancedTwitterService.USER_UPDATED));
		
//		Drawable d = getResources().getDrawable(R.attr.homeAsUpIndicator);
//		System.out.println(d);
	}

}
