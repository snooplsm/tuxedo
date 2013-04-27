package us.wmwm.tuxedo.activities;

import twitter4j.Status;
import twitter4j.StatusDeletionNotice;
import us.wmwm.tuxedo.R;
import us.wmwm.tuxedo.activities.adapters.AdvancedTwitterAdapter;
import us.wmwm.tuxedo.activities.adapters.FollowersAdapter;
import us.wmwm.tuxedo.activities.adapters.FollowingAdapter;
import us.wmwm.tuxedo.activities.adapters.LeftMenuAdapter;
import us.wmwm.tuxedo.activities.adapters.RetweetsAdapter;
import us.wmwm.tuxedo.activities.adapters.TimelineAdapter;
import us.wmwm.tuxedo.fragments.FragmentLeftMenu;
import us.wmwm.tuxedo.fragments.FragmentTwitterList;
import us.wmwm.tuxedo.services.AdvancedTwitterService;
import us.wmwm.tuxedo.util.Views;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.slidingmenu.lib.SlidingMenu;
import com.slidingmenu.lib.app.SlidingActivityHelper;

public class AdvancedTwitterActivity extends BaseActivity {

	private SlidingActivityHelper mHelper;

	private FragmentTwitterList listFragment;
	private FragmentLeftMenu leftMenuFragment;

	private Handler handler = new Handler();

	private AdvancedTwitterAdapter<?> listFragmentAdapter;

	private LeftMenuAdapter leftMenuAdapter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mHelper = new SlidingActivityHelper(this);
		mHelper.onCreate(savedInstanceState);

		configureBehindView();

		mHelper.getSlidingMenu().setTouchModeAbove(
				SlidingMenu.TOUCHMODE_FULLSCREEN);
		mHelper.getSlidingMenu().setBehindOffsetRes(
				R.dimen.abs__action_button_min_width);
		getSupportActionBar().setHomeButtonEnabled(true);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setIcon(R.drawable.ic_list_left);
		setContentView(R.layout.activity_advanced_twitter);

		listFragment = Views.findFragment(this, R.id.fragment_twitter_list);

		IntentFilter filter = new IntentFilter();
		filter.addAction(AdvancedTwitterService.STATUS_STREAMED);
		filter.addAction(AdvancedTwitterService.STATUS_DELETED);
		filter.addAction(AdvancedTwitterService.STATUSES_UPDATED);
		registerReceiver(tweetReceiver, filter);

	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getSupportMenuInflater().inflate(R.menu.advanced_twitter_activity, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	protected void onServiceConnected(AdvancedTwitterService service) {
		super.onServiceConnected(service);
		if (listFragmentAdapter == null) {
			listFragment.setAdapter(listFragmentAdapter = new TimelineAdapter(
					service, listFragment.getList(), handler, getApp()
							.getSession()));
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		unregisterReceiver(tweetReceiver);
	}

	private void configureBehindView() {
		setBehindContentView(R.layout.view_left_menu);
		leftMenuFragment = Views.findFragment(this, R.id.fragment_left_menu);

		leftMenuFragment.setAdapter(leftMenuAdapter = new LeftMenuAdapter(this,
				getApp().getSession()));

		leftMenuFragment.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int pos,
					long arg3) {
				if (pos == LeftMenuAdapter.FOLLOWERS_VIEW_POS) {
					listFragment
							.setAdapter(listFragmentAdapter = new FollowersAdapter(
									service, leftMenuAdapter.getUser().getId(),
									handler, getApp().getSession()));
					mHelper.showContent();
				} else if (pos == LeftMenuAdapter.FOLLOWING_VIEW_POS) {
					listFragment
							.setAdapter(listFragmentAdapter = new FollowingAdapter(
									service, leftMenuAdapter.getUser().getId(),
									handler, getApp().getSession()));
					mHelper.showContent();
				} else if (pos == LeftMenuAdapter.TIMELINE_VIEW_POS) {
					listFragment
							.setAdapter(listFragmentAdapter = new TimelineAdapter(
									service, listFragment.getList(), handler,
									getApp().getSession()));
					mHelper.showContent();
				} else if (pos == LeftMenuAdapter.RETWEETS_VIEW_POS) {
					listFragment
							.setAdapter(listFragmentAdapter = new RetweetsAdapter(getApplicationContext(),
									leftMenuAdapter.getUser().getId(),
									listFragment.getList().getRefreshableView(), handler, getApp()
											.getSession()));
					mHelper.showContent();
				}

			}

		});
		
		leftMenuFragment.setCloseListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mHelper.showContent();
			}
		});
	}

	@Override
	public void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		mHelper.onPostCreate(savedInstanceState);
	}

	@Override
	public View findViewById(int id) {
		View v = super.findViewById(id);
		if (v != null)
			return v;
		return mHelper.findViewById(id);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onSaveInstanceState(android.os.Bundle)
	 */
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		mHelper.onSaveInstanceState(outState);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#setContentView(int)
	 */
	@Override
	public void setContentView(int id) {
		setContentView(getLayoutInflater().inflate(id, null));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#setContentView(android.view.View)
	 */
	@Override
	public void setContentView(View v) {
		setContentView(v, new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#setContentView(android.view.View,
	 * android.view.ViewGroup.LayoutParams)
	 */
	@Override
	public void setContentView(View v, LayoutParams params) {
		super.setContentView(v, params);
		mHelper.registerAboveContentView(v, params);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.slidingmenu.lib.app.SlidingActivityBase#setBehindContentView(int)
	 */
	public void setBehindContentView(int id) {
		setBehindContentView(getLayoutInflater().inflate(id, null));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.slidingmenu.lib.app.SlidingActivityBase#setBehindContentView(android
	 * .view.View)
	 */
	public void setBehindContentView(View v) {
		setBehindContentView(v, new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT));
	}

	public void setSecondaryMenu(View v) {
		mHelper.getSlidingMenu().setSecondaryMenu(v);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.slidingmenu.lib.app.SlidingActivityBase#setBehindContentView(android
	 * .view.View, android.view.ViewGroup.LayoutParams)
	 */
	public void setBehindContentView(View v, LayoutParams params) {
		mHelper.setBehindContentView(v, params);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.slidingmenu.lib.app.SlidingActivityBase#getSlidingMenu()
	 */
	public SlidingMenu getSlidingMenu() {
		return mHelper.getSlidingMenu();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.slidingmenu.lib.app.SlidingActivityBase#toggle()
	 */
	public void toggle() {
		mHelper.toggle();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.slidingmenu.lib.app.SlidingActivityBase#showAbove()
	 */
	public void showContent() {
		mHelper.showContent();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.slidingmenu.lib.app.SlidingActivityBase#showBehind()
	 */
	public void showMenu() {
		mHelper.showMenu();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.slidingmenu.lib.app.SlidingActivityBase#showSecondaryMenu()
	 */
	public void showSecondaryMenu() {
		mHelper.showSecondaryMenu();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.slidingmenu.lib.app.SlidingActivityBase#setSlidingActionBarEnabled
	 * (boolean)
	 */
	public void setSlidingActionBarEnabled(boolean b) {
		mHelper.setSlidingActionBarEnabled(b);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onKeyUp(int, android.view.KeyEvent)
	 */
	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		boolean b = mHelper.onKeyUp(keyCode, event);
		if (b)
			return b;
		return super.onKeyUp(keyCode, event);
	}

	public static Intent intent(Context ctx) {
		Intent i = new Intent(ctx, AdvancedTwitterActivity.class);
		return i;
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		if (item.getItemId() == android.R.id.home) {
			mHelper.toggle();
		}
		if(item.getItemId()==R.id.menu_send_tweet) {
			Intent i = new Intent(this, TweetActivity.class);
			startActivity(i);
		}
		return super.onMenuItemSelected(featureId, item);
	}

	private BroadcastReceiver tweetReceiver = new BroadcastReceiver() {
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if(listFragmentAdapter==null) {
				return;
			}
			if (AdvancedTwitterService.STATUS_STREAMED.equals(action)) {
				listFragmentAdapter.onStatus((Status) intent
						.getSerializableExtra("status"));
			} else if (AdvancedTwitterService.STATUS_DELETED.equals(action)) {
				listFragmentAdapter
						.onDeletionNotice((StatusDeletionNotice) intent
								.getSerializableExtra("dn"));
			} else if (AdvancedTwitterService.STATUSES_UPDATED.equals(action)) {
				listFragmentAdapter
				.onTimeline();
			}
		};
	};
}
