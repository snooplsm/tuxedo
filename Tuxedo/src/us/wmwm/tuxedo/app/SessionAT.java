package us.wmwm.tuxedo.app;

import java.text.DateFormat;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.TwitterStream;
import twitter4j.TwitterStreamFactory;
import twitter4j.User;
import android.content.Context;


public class SessionAT {

	private Set<User> users = new HashSet<User>();
	
	private DatabaseHelper databaseHelper;
	
	private UserDAO userDAO;
	
	private StatusDAO statusDAO;
	
	private DirectMessageDAO directMessageDAO;
	
	private MentionDAO mentionDAO;
	
	private PagingDAO pagingDAO;
	
	private ScheduledExecutorService executors;
	
	private DateFormat dateFormat;
		
	public DateFormat getDateFormat() {
		return dateFormat;
	}

	public SessionAT(Context ctx) {
		databaseHelper = new DatabaseHelper(ctx, "twitter.db");
		userDAO = new UserDAO(databaseHelper);
		statusDAO = new StatusDAO(databaseHelper);
		directMessageDAO = new DirectMessageDAO(databaseHelper);
		mentionDAO = new MentionDAO(databaseHelper);
		pagingDAO = new PagingDAO(databaseHelper);
		executors = Executors.newScheduledThreadPool(5);
		dateFormat = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.MEDIUM);
	}
	
	public PagingDAO getPagingDAO() {
		return pagingDAO;
	}
	
	public MentionDAO getMentionDAO() {
		return mentionDAO;
	}
	
	public UserDAO getUserDAO() {
		return userDAO;
	}
	
	public StatusDAO getStatusDAO() {
		return statusDAO;
	}
	
	
	public DirectMessageDAO getDirectMessageDAO() {
		return directMessageDAO;
	}
	
	public Twitter newTwitterClient() {
		Twitter twitter = new TwitterFactory().getInstance();
		twitter.setOAuthConsumer("EO7qq8tD1Rr3vo1oRiMSA", "1Xape9KkEDrXoziTthtqLf8ERWp7bHJCnWOSAmzqZlE");
		return twitter;
	}
	
	public TwitterStream newTwitterStream() {
		TwitterStream twitter = new TwitterStreamFactory().getInstance();
		twitter.setOAuthConsumer("EO7qq8tD1Rr3vo1oRiMSA", "1Xape9KkEDrXoziTthtqLf8ERWp7bHJCnWOSAmzqZlE");
		return twitter;
	}
	
	public Collection<User> getAuthenticatedUsers() {
		return users;
	}
	
	public ScheduledExecutorService getExecutorService() {
		return executors;
	}

	public void updateUser(User user) {
		users.add(user);
	}
}
