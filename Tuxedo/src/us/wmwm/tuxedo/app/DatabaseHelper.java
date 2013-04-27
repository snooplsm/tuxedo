package us.wmwm.tuxedo.app;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

	private static final int VERSION = 1;
	
	public DatabaseHelper(Context context, String name) {
		super(context, name, null, VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		StringBuilder[] sqls = new StringBuilder[] {
				createUsers(db),
				createStatuses(),
				createDirectMessages(),
				createFollowers(),
				createFriends(),
				createMentions(),
				createPendingTweets(),
				createPaging(PagingDAO.Type.FOLLOWERS),
				createPaging(PagingDAO.Type.FOLLOWING),
		};
		
		for(StringBuilder b : sqls) {
			String sql = b.toString();
			db.execSQL(sql);
		}
	}

	private StringBuilder createMentions() {
		Column[] cols = new Column[] {
				new Column("status_id", Type.INTEGER),
				new Column("user_id", Type.INTEGER)
		};
		Constraint[] cons = new Constraint[] {
				new Constraint(new String[]{"user_id","status_id"},true)
		};
		return toSQL("mentions",cols,cons);
	}
	
	private StringBuilder createPendingTweets() {
		Column[] cols = new Column[] {
			new Column("text", Type.TEXT),
			new Column("created", Type.INTEGER),
			new Column("scheduled_for", Type.INTEGER),
			new Column("id", Type.INTEGER,null,false,true,true),
			new Column("status_id", Type.INTEGER,null,true),
			new Column("in_reply_to", Type.INTEGER),
			new Column("for_user_id", Type.INTEGER),
			new Column("latitude", Type.REAL),
			new Column("longitude", Type.REAL),
			new Column("image", Type.TEXT)
		};
		Constraint[] cons = new Constraint[] {
				
		};
		return toSQL("pending_tweets",cols,cons);
	};

	private StringBuilder createUsers(SQLiteDatabase db) {
		Column[] cols = new Column[] {
			new Column("created_at",Type.INTEGER),
			new Column("description", Type.TEXT),
			new Column("id", Type.INTEGER, null, true),
			new Column("name", Type.TEXT),
			new Column("screen_name", Type.TEXT),
			new Column("json", Type.TEXT),
			new Column("oauth_token", Type.TEXT),
			new Column("oauth_token_secret", Type.TEXT),
			new Column("big_profile_image", Type.TEXT)
		};
		return toSQL("users", cols);
	}
	
	private StringBuilder createFollowers() {
		Column[] cols = new Column[] {
				new Column("id", Type.INTEGER,null,false,true,true),
				new Column("follower_id", Type.INTEGER),
				new Column("user_id", Type.INTEGER)
		};
		Constraint[] cons = new Constraint[] {
			new Constraint(new String[] {"follower_id", "user_id"},true)
		};
		return toSQL("followers", cols, cons);
	}
	
	private StringBuilder createPaging(PagingDAO.Type type) {
		Column[] cols = new Column[] {
				new Column("id", Type.INTEGER, null, false, true,true),
				new Column("from_cursor", Type.INTEGER),
				new Column("next_cursor", Type.INTEGER),
				new Column("previous_cursor", Type.INTEGER),
				new Column("user_id", Type.INTEGER),
				new Column("for_user_id", Type.INTEGER),
				new Column("created", Type.INTEGER)
		};
		Constraint[] cons = new Constraint[] {
			new Constraint(new String[] {"user_id", "for_user_id"},true)	
		};
		return toSQL(type.table, cols,cons);
	}
	
	private StringBuilder createFriends() {
		Column[] cols = new Column[] {
			new Column("friend_id", Type.INTEGER),
			new Column("user_id", Type.INTEGER)
		};
		
		Constraint[] cons = new Constraint[] {
				new Constraint(new String[] {"friend_id", "user_id"}, true)
		};
		return toSQL("friends", cols, cons);
	}
	
	private StringBuilder createStatuses() {
		Column[] cols = new Column[] {
			new Column("created_at", Type.INTEGER),
			new Column("current_user_retweet_id", Type.INTEGER),
			new Column("id", Type.INTEGER, null,true),
			new Column("in_reply_to_screen_name", Type.TEXT),
			new Column("in_reply_to_status_id", Type.INTEGER),
			new Column("in_reply_to_user_id", Type.INTEGER),
			new Column("retweet_count", Type.INTEGER),
			new Column("lat", Type.REAL),
			new Column("lng", Type.REAL),
			new Column("text", Type.TEXT),
			new Column("user_id", Type.INTEGER),
			new Column("for_user_id", Type.INTEGER),
			new Column("source", Type.TEXT),
			new Column("json", Type.TEXT),
			new Column("is_retweet", Type.INTEGER),
			new Column("is_retweeted_by_me", Type.INTEGER),
			new Column("is_favorited", Type.INTEGER),
			new Column("retweeted_user_id", Type.INTEGER)
			
		};
		
		Constraint[] cons = new Constraint[] {
			new Constraint(new String[] {
				"id", "for_user_id"	
			}, true)
		};
		return toSQL("statuses",cols, cons);
	}
	
	private StringBuilder createDirectMessages() {
		Column[] cols = new Column[] {
			new Column("created_at", Type.INTEGER),
			new Column("id", Type.INTEGER, null,true),			
			new Column("text", Type.TEXT),
			new Column("user_id", Type.INTEGER),
			new Column("for_user_id", Type.INTEGER),
			new Column("json", Type.TEXT)
			
		};
		
		Constraint[] cons = new Constraint[] {
			new Constraint(new String[] {
				"id", "for_user_id"	
			}, true)
		};
		return toSQL("direct_messages",cols, cons);
	}
	
	private StringBuilder toSQL(String table, Column[] cols, Constraint... cons) {
		StringBuilder b = new StringBuilder("create table ").append(table).append("(");
		List<Column> list = Arrays.asList(cols);
		Iterator<Column> c = list.iterator();
		while(c.hasNext()) {
			Column col = c.next();
			b.append(col.name).append(' ');
			b.append(col.type.name());
			if(col.length!=null) {
				b.append('(');
				b.append(col.length);
				b.append(')');				
			}
			if(col.primary) {
				b.append(" primary key");
			}
			if(col.autoincrement) {
				b.append(" autoincrement");
			}
			if(col.unique) {
				b.append(" unique");
			}
			if(c.hasNext()) {
				b.append(", ");
			}
		}
		if(cons!=null && cons.length>0) {
			b.append(", ");
			List<Constraint> con = Arrays.asList(cons);
			Iterator<Constraint> conIterator = con.iterator();

			while(conIterator.hasNext()) {
				Constraint constraint = conIterator.next();
				b.append("constraint ");
				List<String> columns = Arrays.asList(constraint.columns);
				Iterator<String> colIter = columns.iterator();
				while(colIter.hasNext()) {
					String coln = colIter.next();
					b.append(coln);
					if(colIter.hasNext()) {
						b.append("_");
					}
				}
				b.append("_constraint ");
				if(constraint.unique) {
					b.append("unique (");
				}
				colIter = columns.iterator();
				while(colIter.hasNext()) {
					String coln = colIter.next();
					b.append(coln);
					if(colIter.hasNext()) {
						b.append(",");
					}
				}
				b.append(")");
				if(conIterator.hasNext()) {
					b.append(", ");
				}
			}
		}
		b.append(")");
		return b;
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int from, int to) {
		// TODO Auto-generated method stub

	}
	
	private static class Column {
		
		private boolean unique;
		private boolean primary;
		private boolean autoincrement;
		private String name;
		private Integer length;
		private Type type;
		
		public Column(String name, Type type) {
			this.name = name;
			this.type = type;	
		}
		
		public Column(String name, Type type, Integer length, boolean unique) {
			this(name,type);
			this.length = length;
			this.unique = unique;
		}
		
		public Column(String name, Type type, Integer length, boolean unique, boolean primary, boolean autoincrement) {
			this(name,type,length,unique);
			this.primary = primary;
			this.autoincrement = autoincrement;
		}
		
	}
	
	private static class Constraint {
		
		String[] columns;
		
		boolean unique;
		
		public Constraint(String[] columns, boolean unique) {
			this.columns = columns;
			this.unique = unique;
		}
		
	}
	
	private static enum Type {
		INTEGER, TEXT, VARCHAR, REAL;
	}

}
