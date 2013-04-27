package us.wmwm.tuxedo.fragments;

import java.util.Calendar;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

import com.googlecode.android.widgets.DateSlider.DateSlider.OnDateSetListener;
import com.googlecode.android.widgets.DateSlider.DateTimeSlider;

public class FragmentDatePicker extends DialogFragment {

	private OnDateSetListener onDateSetListener;
	
	public void setOnDateSetListener(OnDateSetListener onDateSetListener) {
		this.onDateSetListener = onDateSetListener;
	}
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		DateTimeSlider s = new DateTimeSlider(getActivity(),onDateSetListener,Calendar.getInstance());
		return s;
	}
}
