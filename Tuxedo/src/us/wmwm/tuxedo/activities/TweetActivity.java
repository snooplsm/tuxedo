package us.wmwm.tuxedo.activities;

import us.wmwm.tuxedo.app.PendingTweet;
import us.wmwm.tuxedo.fragments.FragmentSendTweet;
import us.wmwm.tuxedo.fragments.FragmentSendTweet.OnSendTweetListener;
import android.os.Bundle;
import android.view.Window;

import us.wmwm.tuxedo.R;

public class TweetActivity extends BaseActivity {

	private FragmentSendTweet sendTweetFragment;
	
	@Override
	protected void onCreate(Bundle bundle) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(bundle);
		setContentView(R.layout.activity_tweet);
		
		sendTweetFragment = (FragmentSendTweet) getSupportFragmentManager().findFragmentById(R.id.fragment_tweet);
		
		sendTweetFragment.setOnSendTweetListener(new OnSendTweetListener() {

			@Override
			public void onSendTweet(PendingTweet pending) {
				service.sendTweet(pending);
				finish();
			}
			
		});
	}
	
}
