package us.wmwm.tuxedo.util;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.View;

public class Views {

	@SuppressWarnings("unchecked")
	public static <T extends View> T findView(View v, int id) {
		return (T) v.findViewById(id);
	}
	
	@SuppressWarnings("unchecked")
	public static <T extends Fragment> T findFragment(FragmentActivity c, int id) {
		return (T) c.getSupportFragmentManager().findFragmentById(id);
	}
	
}
