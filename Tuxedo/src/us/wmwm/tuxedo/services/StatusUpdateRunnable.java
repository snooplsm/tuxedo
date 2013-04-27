package us.wmwm.tuxedo.services;

import java.io.File;

import twitter4j.GeoLocation;
import twitter4j.Status;
import twitter4j.StatusUpdate;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import us.wmwm.tuxedo.app.PendingTweet;
import us.wmwm.tuxedo.util.Images;
import android.net.Uri;


public class StatusUpdateRunnable implements Runnable {

	AdvancedTwitterService service;
	
	PendingTweet pendingTweet;
	
	Twitter twitter;
	
	private static final long MAX_FILE_SIZE = 3145728;
	private static final int MAX_WIDTH = 1024;
	private static final int MAX_HEIGHT = 2048;
	
	public StatusUpdateRunnable(AdvancedTwitterService service, Twitter twitter,
			PendingTweet pendingTweet) {
		super();
		this.service = service;
		this.twitter = twitter;
		this.pendingTweet = pendingTweet;
	}

	@Override
	public void run() {
		try {
			StatusUpdate d = new StatusUpdate(pendingTweet.text);
			if(pendingTweet.inReplyTo!=null) {
				d.setInReplyToStatusId(pendingTweet.inReplyTo);				
			}
			if(pendingTweet.image!=null) {
				int quality = 100;
				File file = Images.resizeImage(service, MAX_WIDTH, MAX_HEIGHT, quality, pendingTweet.id, Uri.parse(pendingTweet.image));
				while(file.length()>MAX_FILE_SIZE) {
					file.delete();
					quality-=5;
					file = Images.resizeImage(service, MAX_WIDTH, MAX_HEIGHT, quality, pendingTweet.id, Uri.parse(pendingTweet.image));
				}
				d.setMedia(file);
			}
			if(pendingTweet.latitude!=null) {
				d.setLocation(new GeoLocation(pendingTweet.latitude,pendingTweet.longitude));
			}
			Status status = twitter.updateStatus(d);
			service.clearPendingTweet(pendingTweet,status);
		} catch (Exception e) {
			if(e instanceof TwitterException) {
				TwitterException te = (TwitterException)e;
				if(!te.isCausedByNetworkIssue()) {
					service.informTweetException(pendingTweet, te);
				}
			}
		}
	}

}
