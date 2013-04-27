package us.wmwm.tuxedo.views;

import java.util.Map;

import twitter4j.User;
import us.wmwm.tuxedo.R;
import us.wmwm.tuxedo.util.Views;
import android.content.Context;
import android.database.Cursor;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.loopj.android.image.SmartImageView;

public class LeftMenuUserView extends RelativeLayout implements UserView {

	private TextView screenName;
	private TextView name;
	private SmartImageView image;
	private User user;

	public LeftMenuUserView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		LayoutInflater.from(context)
				.inflate(R.layout.view_left_menu_user, this);
		screenName = Views.findView(this, R.id.screen_name);
		name = Views.findView(this, R.id.name);
		image = Views.findView(this, R.id.image);
	}

	public LeftMenuUserView(Context context, AttributeSet attrs) {
		this(context, attrs, -1);
	}

	public LeftMenuUserView(Context context) {
		this(context, null, -1);
	}

	@Override
	public void setUser(User user) {
		if (!user.equals(this.user)) {
			screenName.setText("@"+user.getScreenName());
			name.setText(user.getName());
			image.setImageUrl(user.getBiggerProfileImageURL());
			this.user = user;
		}
	}
	
	public void setUser(Cursor user, Map<String,Integer> position) {
		if (!user.equals(this.user)) {
			screenName.setText("@"+user.getString(position.get("screen_name")));
			name.setText(user.getString(position.get("name")));
			//image.setImageUrl(user.getString("));
			//this.user = user;
		}
	}

}
