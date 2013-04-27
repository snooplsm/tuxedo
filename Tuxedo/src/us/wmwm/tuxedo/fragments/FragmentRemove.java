package us.wmwm.tuxedo.fragments;

import us.wmwm.tuxedo.R;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

public class FragmentRemove extends DialogFragment {

	OnClickListener no;
	
	OnClickListener yes;
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		AlertDialog.Builder b = new AlertDialog.Builder(getActivity(),R.style.Theme_Sherlock_Light_Dialog);
		b.setTitle("Remove");
		b.setMessage("Remove from tweet?");
		b.setNegativeButton("No", no);
		b.setPositiveButton("Yes", yes);
		return b.create();
	}
	
	public void setNo(OnClickListener no) {
		this.no = no;
	}
	
	public void setYes(OnClickListener yes) {
		this.yes = yes;
	}
	
}
