package us.wmwm.tuxedo.services;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;

import twitter4j.DirectMessage;
import twitter4j.PagableResponseList;
import twitter4j.Paging;
import twitter4j.ResponseList;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterAPIConfiguration;
import twitter4j.TwitterException;
import twitter4j.TwitterStream;
import twitter4j.User;
import twitter4j.UserMentionEntity;
import twitter4j.auth.AccessToken;
import twitter4j.json.DataObjectFactory;
import us.wmwm.tuxedo.R;
import us.wmwm.tuxedo.app.AdvancedTwitterApplication;
import us.wmwm.tuxedo.app.PagingDAO;
import us.wmwm.tuxedo.app.PendingTweet;
import us.wmwm.tuxedo.app.StatusDAO;
import us.wmwm.tuxedo.util.Images;
import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationCompat.BigPictureStyle;
import android.support.v4.app.NotificationCompat.InboxStyle;
import android.support.v4.app.NotificationCompat.Style;

public class AdvancedTwitterService extends Service {

	private AlarmManager alarmManager;

	private ConnectivityManager connectionManager;

	private NotificationManager notificationManager;

	private Map<Long, PendingIntent> userPendingIntents = new HashMap<Long, PendingIntent>();

	private Map<Long, PendingIntent> statusPendingIntents = new HashMap<Long, PendingIntent>();
	
	private Map<Long, PendingIntent> pendingTweets = new HashMap<Long, PendingIntent>();
	
	private Map<Long, TwitterException> pendingTweetExceptions = new HashMap<Long, TwitterException>();

	private Map<Long, AccessToken> users;

	private Map<Long, Twitter> twitters = new HashMap<Long, Twitter>();

	private Map<Long, TwitterStream> streams = new HashMap<Long, TwitterStream>();
	private Map<String, Future<?>> futures = new HashMap<String, Future<?>>();

	private Future<?> updateConfiguration;

	SharedPreferences preferences;

	public static final String USER_UPDATED = "us.wmwm.tuxedo.USER_UPDATED";
	public static final String STATUSES_UPDATED = "us.wmwm.tuxedo.STATUSES_UPDATED";
	public static final String MENTIONS_UPDATED = "us.wmwm.tuxedo.MENTIONS_UPDATED";
	public static final String DM_UPDATED = "us.wmwm.tuxedo.DMS_UPDATED";
	public static final String STATUS_DELETED = "us.wmwm.tuxedo.STATUS_DELETED";
	public static final String STATUS_STREAMED = "us.wmwm.tuxedo.STATUS_STREAMED";
	public static final String DM_STREAMED = "us.wmwm.tuxedo.DM_STREAMED";
	public static final String MENTION_STREAMED = "us.wmwm.tuxedo.MENTION_STREAMED";
	public static final String FOLLOWERS_LOADED = "us.wmwm.tuxedo.FOLLOWERS_LOADED";
	public static final String FOLLOWING_LOADED = "us.wmwm.tuxedo.FOLLOWING_LOADED";
	public static final String MORE_FOLLOWERS_LOADED = "us.wmwm.tuxedo.MORE_FOLLOWERS_LOADED";
	public static final String MORE_FOLLOWING_LOADED = "us.wmwm.tuxedo.MORE_FOLLOWING_LOADED";
	public static final String RETWEETS_LOADED = "us.wmwm.tuxedo.RETWEETS_LOADED";

	@Override
	public IBinder onBind(Intent intent) {
		return new LocalBinder<AdvancedTwitterService>(this);
	}

	@Override
	public void onCreate() {
		super.onCreate();
		alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
		connectionManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		preferences = PreferenceManager.getDefaultSharedPreferences(this);
		scheduleUserUpdates();
		getApp().getSession().getExecutorService().submit(new Runnable() {
			@Override
			public void run() {
				loadRetweets(getApp().getSession().getAuthenticatedUsers()
						.iterator().next().getId());
			}
		});
		// getApp().getSession().getPagingDAO().deleteAll();
		// deleteAllTweets();
	}

	private void deleteAllTweets() {
		getApp().getSession().getExecutorService().submit(new Runnable() {
			@Override
			public void run() {
				try {
					Thread.sleep(10000);
				} catch (InterruptedException e3) {
					// TODO Auto-generated catch block
					e3.printStackTrace();
				}
				for (Map.Entry<Long, Twitter> e : twitters.entrySet()) {
					while (true) {
						try {
							ResponseList<Status> t = e.getValue()
									.getUserTimeline();
							for (Status s : t) {
								e.getValue().destroyStatus(s.getId());
								try {
									Thread.sleep(800);
								} catch (InterruptedException e1) {
									// TODO Auto-generated catch block
									e1.printStackTrace();
								}
							}
						} catch (TwitterException e1) {
							try {
								Thread.sleep(10000);
							} catch (InterruptedException e2) {
								// TODO Auto-generated catch block
								e2.printStackTrace();
							}
						}
					}
				}
			}
		});
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		if (intent != null) {
			Uri uri = intent.getData();
			final Type type = Type.valueOf(uri.getQueryParameter("type"));
			final Long userId = Long.parseLong(uri.getQueryParameter("userID"));
			Twitter twitte = twitters.get(userId);
			if (twitte == null) {
				try {
					twitters.put(userId, twitte = getApp().getSession()
							.newTwitterClient());
					twitte.setOAuthAccessToken(users.get(userId));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			final Twitter twitter = twitte;
			Future<?> future = futures.get(userId + "_" + type.name());
			if (future != null) {
				future.cancel(true);
			}
			if (type == Type.UPDATE_USER) {
				future = getApp().getSession().getExecutorService()
						.submit(new Runnable() {
							@Override
							public void run() {
								try {
									User user = twitter.verifyCredentials();
									getApp().getSession().getUserDAO()
											.saveUser(user, null);
									getApp().getSession().updateUser(user);
									Intent i = new Intent();
									i.setAction(USER_UPDATED);
									i.putExtra("user", user);
									sendBroadcast(i);
								} catch (TwitterException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}

							}
						});
			} else if (type == Type.UPDATE_STATUSES) {
				future = getApp().getSession().getExecutorService()
						.submit(new Runnable() {
							@Override
							public void run() {
								try {
									ResponseList<Status> statuses = twitter
											.getHomeTimeline();
									getApp().getSession().getStatusDAO()
											.saveStatuses(userId, statuses);
									getApp().getSession().getUserDAO().saveUsers(statuses);
									getApp().getSession().getMentionDAO()
											.saveMentions(userId, statuses);
									Intent i = new Intent();
									i.setAction(STATUSES_UPDATED);
									i.putExtra("statuses", statuses);
									i.putExtra("user", userId);
									sendBroadcast(i);
								} catch (TwitterException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
							}
						});
			} else if (type == Type.UPDATE_MENTIONS) {
				future = getApp().getSession().getExecutorService()
						.submit(new Runnable() {
							@Override
							public void run() {
								Status latest = getApp().getSession()
										.getStatusDAO().getLastStatus(userId);
								final Paging paging = new Paging();

								try {
									ResponseList<Status> statuses = twitter
											.getMentionsTimeline(paging);
									getApp().getSession().getStatusDAO()
											.saveStatuses(userId, statuses);
									getApp().getSession().getMentionDAO()
											.saveMentions(userId, statuses);
									Intent i = new Intent();
									i.setAction(MENTIONS_UPDATED);
									i.putExtra("mentions", statuses);
									i.putExtra("user", userId);
									sendBroadcast(i);
								} catch (TwitterException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
							}
						});
			} else if (type == Type.UPDATE_DM) {
				future = getApp().getSession().getExecutorService()
						.submit(new Runnable() {
							@Override
							public void run() {
								final Paging paging = new Paging();

								try {
									ResponseList<DirectMessage> statuses = twitter
											.getDirectMessages(paging);
									getApp().getSession().getDirectMessageDAO()
											.saveDirectMessage(statuses);
									Intent i = new Intent();
									i.setAction(DM_UPDATED);
									i.putExtra("mentions", statuses);
									i.putExtra("user", userId);
									sendBroadcast(i);
								} catch (TwitterException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
							}
						});
			} else if (type==Type.PENDING_TWEET) {
				String pid = uri.getQueryParameter("pendingID");
				PendingTweet pending = getApp().getSession().getStatusDAO().getPending(Long.parseLong(pid));
				future = getApp().getSession().getExecutorService().submit(new StatusUpdateRunnable(this,twitte,pending));
			}
		}
		return super.onStartCommand(intent, flags, startId);
	}

	public AdvancedTwitterApplication getApp() {
		return (AdvancedTwitterApplication) super.getApplication();
	}

	private void scheduleUserUpdates() {
		users = getApp().getSession().getUserDAO().getAuthedUserIDs();
		getApp().getSession().getExecutorService().submit(new Runnable() {

			@Override
			public void run() {
				for(Long userId : users.keySet()) {
					try {
					User user = getApp().getSession().getUserDAO().getUser(userId);
					getApp().getSession().updateUser(user);
					} catch (Exception e) {
						e.printStackTrace();
					}
					
				}
			}
			
		});
		for (Map.Entry<Long, AccessToken> e : users.entrySet()) {
			PendingIntent pi = PendingIntent.getService(this, 0,
					intent(e.getKey(), Type.UPDATE_USER), 0);
			userPendingIntents.put(e.getKey(), pi);
			alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP,
					System.currentTimeMillis(), 60 * 1000 * 1000, pi);

			pi = PendingIntent.getService(this, 0,
					intent(e.getKey(), Type.UPDATE_STATUSES), 0);
			statusPendingIntents.put(e.getKey(), pi);
			alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP,
					System.currentTimeMillis(), 60 * 1000 * 1000, pi);

			final Twitter twitter = getApp().getSession().newTwitterClient();
			twitter.setOAuthAccessToken(e.getValue());
			twitters.put(e.getKey(), twitter);
			
			// status
			TwitterStream stream = getApp().getSession().newTwitterStream();
			stream.setOAuthAccessToken(users.get(e.getKey()));
			stream.addListener(new TwitterStreamListener(stream, this));
			stream.user();
			streams.put(e.getKey(), getApp().getSession().newTwitterStream());
			
			System.out.println("pending: " + getApp().getSession().getStatusDAO().getPendingTweetsCount(e.getKey(), Long.MAX_VALUE));

			loadPending(e.getKey());
			
			if (updateConfiguration == null) {
				Long lastConfigurationUpdate = preferences.getLong(
						Type.UPDATE_CONFIGURATION.name(), 0);

				Calendar cal = Calendar.getInstance();
				cal.setTimeInMillis(lastConfigurationUpdate);

				Calendar yesterday = Calendar.getInstance();
				yesterday.add(Calendar.DAY_OF_YEAR, -1);
				if (cal.before(yesterday)) {
					updateConfiguration = getApp().getSession().getExecutorService().submit(new Runnable() {
						@Override
						public void run() {
							TwitterAPIConfiguration config = null;
							try {
								config = twitter.getAPIConfiguration();
							} catch (TwitterException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							if(config!=null) {
								preferences.edit().putString(Type.CONFIGURATION.name(), DataObjectFactory.getRawJSON(config))
								.putLong(Type.UPDATE_CONFIGURATION.name(), System.currentTimeMillis())
								.commit();
							}
						}
					});
				} else {
					
				}
			}
		}

	}

	private static enum Type {
		UPDATE_USER, UPDATE_STATUSES, UPDATE_DM, UPDATE_MENTIONS, UPDATE_CONFIGURATION, CONFIGURATION, PENDING_TWEET;
	}

	// public void

	public void loadFollowersAsync(final Long forUserID, final Long userID) {
		getApp().getSession().getExecutorService().submit(new Runnable() {
			@Override
			public void run() {
				loadFollowers(forUserID, userID);
			}
		});
	}

	public PagableResponseList<User> loadFollowers(Long forUserID, Long userID) {
		Twitter twitte = twitters.get(forUserID);
		try {

			PagableResponseList<User> pagableResponse = twitte
					.getFollowersList(userID, PagableResponseList.START);
			getApp().getSession().getUserDAO().saveUsers(pagableResponse);
			getApp().getSession()
					.getPagingDAO()
					.save(PagingDAO.Type.FOLLOWERS, userID,
							PagableResponseList.START, pagableResponse, true);
			Intent i = new Intent();
			i.setAction(FOLLOWERS_LOADED);
			i.putExtra("followers", pagableResponse);
			sendBroadcast(i);
			return pagableResponse;
		} catch (TwitterException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return null;
	}

	public void loadMoreFollowersAsync(final Long forUserID, final Long userID,
			final Long afterUserID) {
		getApp().getSession().getExecutorService().submit(new Runnable() {
			@Override
			public void run() {
				loadMoreFollowers(forUserID, userID, afterUserID);
			}
		});
	}

	public PagableResponseList<User> loadMoreFollowers(Long forUserID,
			Long userID, Long afterUserID) {
		Twitter twitte = twitters.get(forUserID);
		try {

			PagableResponseList<User> pagableResponse = twitte
					.getFollowersList(
							userID,
							getApp().getSession()
									.getPagingDAO()
									.getNext(PagingDAO.Type.FOLLOWERS,
											afterUserID));
			getApp().getSession().getUserDAO().saveUsers(pagableResponse);
			getApp().getSession()
					.getPagingDAO()
					.save(PagingDAO.Type.FOLLOWERS, userID,
							PagableResponseList.START, pagableResponse, false);
			Intent i = new Intent();
			i.setAction(MORE_FOLLOWERS_LOADED);
			i.putExtra("followers", pagableResponse);
			return pagableResponse;
		} catch (TwitterException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return null;
	}

	public List<User> loadMoreFollowing(Long forUserID, Long userID,
			Long afterUserID) {
		Twitter twitte = twitters.get(forUserID);
		try {

			PagableResponseList<User> pagableResponse = twitte
					.getFollowersList(
							userID,
							getApp().getSession()
									.getPagingDAO()
									.getNext(PagingDAO.Type.FOLLOWING,
											afterUserID));
			getApp().getSession().getUserDAO().saveUsers(pagableResponse);
			getApp().getSession()
					.getPagingDAO()
					.save(PagingDAO.Type.FOLLOWING, userID,
							PagableResponseList.START, pagableResponse, false);
			Intent i = new Intent();
			i.setAction(MORE_FOLLOWING_LOADED);
			i.putExtra("following", pagableResponse);
			return pagableResponse;
		} catch (TwitterException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return null;
	}

	Intent intent(Long userID, Type type) {
		Intent i = new Intent(this, AdvancedTwitterService.class);
		Uri uri = new Uri.Builder().scheme("at").authority("ok")
				.appendQueryParameter("userID", userID.toString())
				.appendQueryParameter("type", type.name()).build();
		i.setData(uri);
		return i;
	}
	
	Intent intent(PendingTweet p) {
		Intent i = new Intent(this, AdvancedTwitterService.class);
		Uri uri = new Uri.Builder().scheme("at").authority("ok")
				.appendQueryParameter("pendingID", p.id.toString())
				.appendQueryParameter("type", Type.PENDING_TWEET.name())
				.appendQueryParameter("userID", p.forUserId.toString()).build();
		i.setData(uri);
		return i;
	}

	public List<User> loadFollowing(Long forUserID, Long userID) {
		Twitter twitte = twitters.get(forUserID);
		try {

			PagableResponseList<User> pagableResponse = twitte.getFriendsList(
					userID, PagableResponseList.START);
			getApp().getSession().getUserDAO().saveUsers(pagableResponse);
			getApp().getSession()
					.getPagingDAO()
					.save(PagingDAO.Type.FOLLOWING, userID,
							PagableResponseList.START, pagableResponse, true);
			Intent i = new Intent();
			i.setAction(FOLLOWERS_LOADED);
			i.putExtra("following", pagableResponse);
			sendBroadcast(i);
			return pagableResponse;
		} catch (TwitterException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return null;
	}

	public ResponseList<Status> loadRetweets(Long forUserID) {
		Twitter twitter = twitters.get(forUserID);
		try {
			ResponseList<Status> retweets = twitter.getRetweetsOfMe();

			getApp().getSession().getStatusDAO()
					.saveStatuses(forUserID, retweets);
			Intent i = new Intent();
			i.setAction(RETWEETS_LOADED);
			i.putExtra("retweets", retweets);
			sendBroadcast(i);
			return retweets;
		} catch (TwitterException e) {

		}
		return null;
	}

	private static final int NOTIFICATION_ID_SINGLE = 1;
	private static final int NOTIFICCATION_ID_MULTIPLE = 2;
	private static final int NOTIFICATION_SENDING = 3;

	public void updateStatusNotification(Long forUserID, Status status) {

		boolean mention = false;
		for (long userId : status.getContributors()) {
			if (userId == forUserID.longValue()) {
				mention = true;
			}
		}
		for (UserMentionEntity e : status.getUserMentionEntities()) {
			if (e.getId() == forUserID.longValue()) {
				mention = true;
			}
		}

		if (mention) {
			NotificationCompat.Builder b = new NotificationCompat.Builder(this);
			InboxStyle t = new InboxStyle();
			String title = status.getUser().getScreenName() + " mentioned you";
			t.setBigContentTitle(title);
			String text = status.getText()
					+ " | "
					+ getApp().getSession().getDateFormat()
							.format(status.getCreatedAt());
			b.setContentText(text);
			// b.setB
			t.setSummaryText(status.getText());
			b.setStyle(t);
			b.setWhen(status.getCreatedAt().getTime());
			b.setSmallIcon(R.drawable.ic_stat_tweet);
			b.setContentTitle(title);
			notificationManager.notify(NOTIFICATION_ID_SINGLE, b.build());
		}
		// b.setStyle(new NotificationCompat.InboxStyle().)

	}
	
	private void updateSendingNotification() {
		
		if(pendingTweets.size()>1) {
			InboxStyle inbox = new InboxStyle();
			
			List<Long> longs = new ArrayList<Long>(pendingTweets.keySet());
			Collections.sort(longs);
			NotificationCompat.Builder b = new NotificationCompat.Builder(this);
			b.setStyle(inbox);
			b.setSmallIcon(R.drawable.ic_send_tweet);
			notificationManager.notify(NOTIFICATION_SENDING, b.build());
			for(Long pendingId : longs) {
				PendingTweet pt = getApp().getSession().getStatusDAO().getPending(pendingId);
				inbox.addLine("\"" + pt.text + "\" going out at " + DateFormat.getDateTimeInstance().format(pt.scheduledFor));
			}
		} else {
			if(pendingTweets.isEmpty()) {
				notificationManager.cancel(NOTIFICATION_SENDING);
			} else {
				Long id = pendingTweets.keySet().iterator().next();
				PendingTweet pt = getApp().getSession().getStatusDAO().getPending(id);
				NotificationCompat.Builder b = new NotificationCompat.Builder(this);
				final Style style;
				if(pt.image!=null) {
					int maxWidth = (int) getResources().getDimension(R.dimen.big_picture_width);
					int maxHeight = maxWidth/2;
					BigPictureStyle image = new BigPictureStyle();
					image.bigPicture(Images.loadImage(getApplicationContext(), maxWidth, maxHeight, Uri.parse(pt.image)));
					image.setSummaryText(pt.text);
					style = image;
				} else {
					InboxStyle inbox = new InboxStyle();
					inbox.addLine("\"" + pt.text + "\" going out at " + DateFormat.getDateTimeInstance().format(pt.scheduledFor));
					style = inbox;
				}
				b.setStyle(style);
				b.setSmallIcon(R.drawable.ic_stat_tweet);
				notificationManager.notify(NOTIFICATION_SENDING, b.build());
			}
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();

		for (Map.Entry<Long, TwitterStream> entry : streams.entrySet()) {
			entry.getValue().cleanUp();
		}
	}
	
	private void loadPending(Long userID) {
		StatusDAO dao = getApp().getSession().getStatusDAO();
		int count = dao.getPendingTweetsCount(userID, System.currentTimeMillis() + 1000*60*60);
		if(count==0) {
			return;
		}
		List<PendingTweet> t = dao.getPendingTweets(userID, System.currentTimeMillis() + 1000*60*60);
		for(PendingTweet pt : t) {
			sendTweet(pt);
		}
	}

	public void sendTweet(PendingTweet pending) {
		// TODO Auto-generated method stub
		getApp().getSession().getStatusDAO().savePending(pending);
		Future<?> pendingFuture = futures.get("pending_tweet_"+pending.id);
		if(pendingFuture!=null) {
			pendingFuture.cancel(true);
		}
		PendingIntent pp = pendingTweets.remove(pending.id);
		if(pp!=null) {
			alarmManager.cancel(pp);
		}
		if(pending.scheduledFor!=null) {
			Calendar now = Calendar.getInstance();
			if(pending.scheduledFor.after(now)) {
				PendingIntent pi = PendingIntent.getService(this, 0,
						intent(pending), 0);
				pendingTweets.put(pending.id, pi);
				alarmManager.set(AlarmManager.RTC_WAKEUP,
						pending.scheduledFor.getTimeInMillis(), pi);
				return;
			}
		}
		Runnable r = new StatusUpdateRunnable(this,twitters.get(pending.forUserId),pending);
		futures.put("pending_tweet_"+pending.id, getApp().getSession().getExecutorService().submit(r));
	}

	public void clearPendingTweet(PendingTweet pending, Status status) {
		// TODO Auto-generated method stub
		Future<?> pendingFuture = futures.get("pending_tweet_"+pending.id);
		if(pendingFuture!=null) {
			pendingFuture.cancel(true);
		}
		PendingIntent pp = pendingTweets.remove(pending.id);
		pendingTweetExceptions.remove(pending.id);
		pending.statusId = status.getId();
		getApp().getSession().getStatusDAO().savePending(pending);
		getApp().getSession().getStatusDAO().saveStatus(pending.forUserId, status);
		if(pp!=null) {
			alarmManager.cancel(pp);
		}
	}

	public void informTweetException(PendingTweet t, TwitterException te) {
		pendingTweetExceptions.put(t.id, te);
	}

	public void loadMoreTweets(long id) {
		Future<?> loadMore = futures.get("load_more_"+id);
		
		if(loadMore!=null) {
			loadMore.cancel(true);
		} 
		
		loadMore = getApp().getSession().getExecutorService().submit(new LoadMoreTweetsRunnable(this, twitters.get(id)));				
		
	}

}
