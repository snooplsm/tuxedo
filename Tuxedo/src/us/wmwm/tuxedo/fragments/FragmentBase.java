package us.wmwm.tuxedo.fragments;

import us.wmwm.tuxedo.activities.BaseActivity;
import us.wmwm.tuxedo.app.AdvancedTwitterApplication;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.actionbarsherlock.app.SherlockFragment;

public abstract class FragmentBase extends SherlockFragment {

	public abstract int getResourceView();
	
	protected View root;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		root = inflater.inflate(getResourceView(), null);
		initializeViews();
		return root;
	}

	protected abstract void initializeViews();
	
	@SuppressWarnings("unchecked")
	protected <T> T findView(int id) {
		return (T) root.findViewById(id);
	}
	
	public AdvancedTwitterApplication getApplication() {
		return (AdvancedTwitterApplication) getActivity().getApplication();
	}
	
	public BaseActivity getBaseActivity() {
		return (BaseActivity) getActivity();
	}
	
}
