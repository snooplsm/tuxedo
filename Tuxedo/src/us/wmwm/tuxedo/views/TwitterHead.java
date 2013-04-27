package us.wmwm.tuxedo.views;

import android.widget.TextView;
import twitter4j.Status;
import us.wmwm.tuxedo.R;
import android.content.Context;
import android.view.LayoutInflater;
import android.widget.RelativeLayout;

import com.loopj.android.image.SmartImageView;

public class TwitterHead extends RelativeLayout {

	SmartImageView icon;
    TextView statusTextView;

	public TwitterHead(Context ctx) {
		super(ctx);
		LayoutInflater.from(ctx).inflate(R.layout.twitter_head, this);
		icon = (SmartImageView) findViewById(R.id.icon);
        statusTextView = (TextView) findViewById(R.id.status_textview);
	}

	public void update(Status status) {
		icon.setImageUrl(status.getUser().getBiggerProfileImageURL());
        statusTextView.setText(status.getText());
	}

    public void startDrag() {
        statusTextView.setVisibility(GONE);
    }

    public void stopDrag() {
        statusTextView.setVisibility(VISIBLE);
    }
}
