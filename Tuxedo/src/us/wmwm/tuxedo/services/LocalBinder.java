package us.wmwm.tuxedo.services;

import java.lang.ref.WeakReference;

import android.app.Service;
import android.os.Binder;

public class LocalBinder<T extends Service> extends Binder {

	private WeakReference<T> service;
	
	public LocalBinder(T service) {
		this.service = new WeakReference<T>(service);
	}
	
	public T getService() {
		return service.get();
	}
	
	
	
}
