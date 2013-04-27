package us.wmwm.tuxedo.app;

import android.app.Application;

public class AdvancedTwitterApplication extends Application {

	private SessionAT session;

	public SessionAT getSession() {
		return session;
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		
		System.setProperty("twitter4j.jsonStoreEnabled", "true");
		session = new SessionAT(this);
	}
	
}
