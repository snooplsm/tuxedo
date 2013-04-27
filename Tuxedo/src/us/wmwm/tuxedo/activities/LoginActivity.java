package us.wmwm.tuxedo.activities;
import java.util.Map;

import twitter4j.auth.AccessToken;
import us.wmwm.tuxedo.app.AdvancedTwitterApplication;
import android.content.Intent;
import android.os.Bundle;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import us.wmwm.tuxedo.R;


public class LoginActivity extends SherlockFragmentActivity {	
	
	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		
		AdvancedTwitterApplication app = (AdvancedTwitterApplication) getApplication();
		
		Map<Long, AccessToken> authedUsers = app.getSession().getUserDAO().getAuthedUserIDs();
		
		if(!authedUsers.isEmpty()) {
			Intent i = AdvancedTwitterActivity.intent(getApplicationContext());
			i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(i);
			finish();	
			return;
		}
		
		setContentView(R.layout.activity_login);
	}
	
	
	
}
