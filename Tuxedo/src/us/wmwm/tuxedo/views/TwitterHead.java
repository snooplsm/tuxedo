package us.wmwm.tuxedo.views;

import twitter4j.Status;
import us.wmwm.tuxedo.R;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;

import com.loopj.android.image.SmartImageView;

public class TwitterHead extends RelativeLayout {

	SmartImageView icon;

	public TwitterHead(Context ctx) {
		super(ctx);
		LayoutInflater.from(ctx).inflate(R.layout.twitter_head, this);
		icon = (SmartImageView) findViewById(R.id.icon);
	}

	public void update(Status status) {
		icon.setImageUrl(status.getUser().getBiggerProfileImageURL());
	}

}
