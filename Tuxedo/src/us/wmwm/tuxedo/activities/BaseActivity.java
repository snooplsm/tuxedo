package us.wmwm.tuxedo.activities;

import us.wmwm.tuxedo.app.AdvancedTwitterApplication;
import us.wmwm.tuxedo.services.AdvancedTwitterService;
import us.wmwm.tuxedo.services.LocalBinder;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;

import com.actionbarsherlock.app.SherlockFragmentActivity;

public class BaseActivity extends SherlockFragmentActivity {

	protected AdvancedTwitterService service;
	
	private ServiceConnection serviceConnection = new ServiceConnection() {

		@SuppressWarnings("unchecked")
		@Override
		public void onServiceConnected(ComponentName name, IBinder s) {
			service = ((LocalBinder<AdvancedTwitterService>)s).getService();
			BaseActivity.this.onServiceConnected(service);			
		}

		@Override
		public void onServiceDisconnected(ComponentName name) {
			service = null;
		}
		
	};
	
	protected void onCreate(android.os.Bundle bundle) {
		super.onCreate(bundle);
		
		Intent service = new Intent(this, AdvancedTwitterService.class);
		bindService(service, serviceConnection, Context.BIND_AUTO_CREATE);
	};
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		
		unbindService(serviceConnection);
	}
	
	protected AdvancedTwitterApplication getApp() {
		return (AdvancedTwitterApplication)getApplication();
	}
	
	protected void onServiceConnected(AdvancedTwitterService service) {
		
	}
	
}
