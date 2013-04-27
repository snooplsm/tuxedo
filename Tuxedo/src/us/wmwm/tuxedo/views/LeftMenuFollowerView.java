package us.wmwm.tuxedo.views;

import twitter4j.User;
import us.wmwm.tuxedo.util.Views;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.RelativeLayout;
import android.widget.TextView;

import us.wmwm.tuxedo.R;

public class LeftMenuFollowerView extends RelativeLayout implements UserView {

	private TextView text;
	
	private User user;
	
	public LeftMenuFollowerView(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
		LayoutInflater.from(context).inflate(R.layout.view_left_menu_follower,this);
		text = Views.findView(this, R.id.text);
	}

	public LeftMenuFollowerView(Context context, AttributeSet attrs) {
		this(context, attrs,-1);
	}

	public LeftMenuFollowerView(Context context) {
		this(context,null,-1);
	}

	@Override
	public void setUser(User user) {
		if(!user.equals(this.user)) {
			this.user = user;
			text.setText(user.getFollowersCount() + " " + getResources().getString(R.string.label_followers));
		}
	}

}
