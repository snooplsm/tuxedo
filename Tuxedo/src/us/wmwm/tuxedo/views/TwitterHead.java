package us.wmwm.tuxedo.views;

import android.widget.TextView;
import twitter4j.Status;
import us.wmwm.tuxedo.R;
import android.content.Context;
import android.view.Gravity;
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
        invalidate();
    }
    

    public void stopDrag() {
        statusTextView.setVisibility(VISIBLE);
        invalidate();
    }
    
    public void left() {
    	RelativeLayout.LayoutParams iconLP = (RelativeLayout.LayoutParams) icon.getLayoutParams();
    	RelativeLayout.LayoutParams textLP = (RelativeLayout.LayoutParams) statusTextView.getLayoutParams();
    	
    	iconLP.removeRule(RelativeLayout.RIGHT_OF);
    	iconLP.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
    	textLP.removeRule(RelativeLayout.LEFT_OF);
    	textLP.addRule(RelativeLayout.RIGHT_OF,R.id.icon);
    	textLP.removeRule(RelativeLayout.ALIGN_PARENT_LEFT);
    	statusTextView.setGravity(Gravity.LEFT);
    	invalidate();
    }
    
    public void right() {
    	RelativeLayout.LayoutParams iconLP = (RelativeLayout.LayoutParams) icon.getLayoutParams();
    	RelativeLayout.LayoutParams textLP = (RelativeLayout.LayoutParams) statusTextView.getLayoutParams();
    	
    	iconLP.removeRule(RelativeLayout.ALIGN_PARENT_LEFT);
    	iconLP.addRule(RelativeLayout.RIGHT_OF,R.id.status_textview);
    	textLP.removeRule(RelativeLayout.RIGHT_OF);
    	statusTextView.setGravity(Gravity.RIGHT);
    	textLP.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
    	invalidate();
    }
}
