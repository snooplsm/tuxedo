package us.wmwm.tuxedo.fragments;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import us.wmwm.tuxedo.R;
import us.wmwm.tuxedo.app.PendingTweet;
import us.wmwm.tuxedo.util.Images;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.googlecode.android.widgets.DateSlider.DateSlider;
import com.googlecode.android.widgets.DateSlider.DateSlider.OnDateSetListener;

public class FragmentSendTweet extends FragmentBase {

	public static interface OnSendTweetListener {
		void onSendTweet(PendingTweet pending);
	}

	private static final int CODE_CAMERA = 10;

	private static final int CODE_PICK = 20;

	public static final int MEDIA_TYPE_IMAGE = 1;

	public static final int MEDIA_TYPE_VIDEO = 2;

	private boolean camera;
	ViewGroup cameraContainer;

	private View cancel;

	private ViewGroup clockContainer;

	private ImageView clockImage;

	private ViewGroup galleryContainer;

	private Handler handler = new Handler();
	private Uri image;
	private Long inReplyTo;
	private OnSendTweetListener onSendTweetListener;

	private Calendar scheduledFor;

	private Future<?> scheduleFuture;
	private ViewGroup sendContainer;
	private ImageView sendImage;
	private EditText text;

	private ImageView picture;
	private ViewGroup timeContainer;

	private TextView timeLeft;
	private TextView dateTime;

	private Uri tmpImage;

	private Runnable updateTime = new Runnable() {
		public void run() {
			handler.post(updateTimeUI);
		};
	};

	private Runnable updateTimeUI = new Runnable() {
		public void run() {
			if (scheduledFor == null) {
				scheduleFuture.cancel(true);
				clockContainer.setVisibility(View.GONE);
				return;
			}
			long future = scheduledFor.getTimeInMillis();
			long now = Calendar.getInstance().getTimeInMillis();
			long days = (future - now) / 86400000;
			long left = (future - now) % 86400000;
			long hours = left / 3600000;
			left = left % 3600000;
			long mins = left / 60000;
			left = left % 60000;
			long seconds = left / 1000;

			StringBuilder b = new StringBuilder();

			if (days > 0) {
				b.append(days).append("d");
			}
			if (hours > 0) {
				b.append(hours).append("h");
			}
			if (mins > 0) {
				b.append(mins).append("m");
			}
			if (seconds > 0) {
				if (seconds > 9) {
					b.append(seconds).append("s");
				} else {
					b.append("0").append(seconds).append("s");
				}
			} else {
				b.append("00s");
			}
			timeLeft.setText(b);
		};
	};

	private void checkScheduledFor() {
		int hide = View.VISIBLE;
		if (scheduledFor == null) {
			hide = View.GONE;
		} else {
			if (scheduledFor.before(Calendar.getInstance())) {
				hide = View.GONE;
				scheduledFor = null;
			}
		}

		timeContainer.setVisibility(hide);

		if (scheduleFuture != null) {
			scheduleFuture.cancel(true);
		}

		if (hide == View.VISIBLE) {
			scheduleFuture = getApplication().getSession().getExecutorService()
					.scheduleAtFixedRate(updateTime, 0, 1, TimeUnit.SECONDS);
			dateTime.setText(DateFormat.getDateTimeInstance(DateFormat.SHORT,
					DateFormat.SHORT).format(scheduledFor.getTime()));
		}
	}

	private void deleteImageIfNeeded() {
		if (image != null && camera) {
			File file = new File(image.toString());
			if (file.exists()) {
				file.delete();
			}
		}
		picture.setVisibility(View.GONE);

	};

	private File getOutputMediaFile(int type) {
		// To be safe, you should check that the SDCard is mounted
		// using Environment.getExternalStorageState() before doing this.

		File mediaStorageDir = Environment
				.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
		// This location works best if you want the created images to be shared
		// between applications and persist after your app has been uninstalled.

		// Create the storage directory if it does not exist
		if (!mediaStorageDir.exists()) {
			if (!mediaStorageDir.mkdirs()) {
				Log.d("Photos", "failed to create directory");
				return null;
			}
		}

		// Create a media file name
		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss")
				.format(new Date());
		File mediaFile = null;
		if (type == MEDIA_TYPE_IMAGE) {
			mediaFile = new File(mediaStorageDir.getPath() + File.separator
					+ "IMG_" + timeStamp + ".jpg");
		} else if (type == MEDIA_TYPE_VIDEO) {
			mediaFile = new File(mediaStorageDir.getPath() + File.separator
					+ "VID_" + timeStamp + ".mp4");
		}

		return mediaFile;
	};

	private Uri getOutputMediaFileUri(int type) {
		return Uri.fromFile(getOutputMediaFile(type));
	}

	@Override
	public int getResourceView() {
		return R.layout.fragment_send_tweet;
	}

	@Override
	protected void initializeViews() {
		// TODO Auto-generated method stub
		clockContainer = findView(R.id.clock);
		clockImage = findView(R.id.clock_image);
		galleryContainer = findView(R.id.gallery_picture);
		cameraContainer = findView(R.id.camera_picture);
		timeContainer = findView(R.id.time_container);
		sendContainer = findView(R.id.send_tweet);
		sendImage = findView(R.id.send_tweet_image);
		text = findView(R.id.edit);
		timeLeft = findView(R.id.time_until_tweet);
		dateTime = findView(R.id.datetime_until_tweet);
		cancel = findView(R.id.cancel);
		picture = findView(R.id.picture);
	}

	private void loadImage() {
		if(image==null) {
			return;
		}
		getApplication().getSession().getExecutorService()
				.submit(new Runnable() {
					@Override
					public void run() {
						int maxWidth = (int) (getResources()
								.getDimension(
										R.dimen.abs__action_button_min_width) * 1.5);
						final Bitmap b = Images
								.loadImage(
										getActivity(),
										maxWidth,maxWidth,
										image);
						handler.post(new Runnable() {
							@Override
							public void run() {
								picture.setImageBitmap(b);
								picture.setVisibility(View.VISIBLE);
							}
						});
					}
				});
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		clockContainer.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				FragmentDatePicker dp = new FragmentDatePicker();
				dp.setOnDateSetListener(new OnDateSetListener() {
					@Override
					public void onDateSet(DateSlider view, Calendar selectedDate) {
						scheduledFor = selectedDate;
						checkScheduledFor();
					}
				});
				dp.show(getSherlockActivity().getSupportFragmentManager(), "dp");
			}
		});

		sendContainer.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				sendTweet();
			}
		});

		if (savedInstanceState != null) {
			restore(savedInstanceState);
		}

		cameraContainer.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
				tmpImage = getOutputMediaFileUri(MEDIA_TYPE_IMAGE); // create a
																	// file to
																	// save the
																	// image
				intent.putExtra(MediaStore.EXTRA_OUTPUT, tmpImage);
				startActivityForResult(intent, CODE_CAMERA);
			}
		});

		galleryContainer.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setType("image/*");
				intent.setAction(Intent.ACTION_GET_CONTENT);
				startActivityForResult(intent, CODE_PICK);
			}
		});

		cancel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				deleteImageIfNeeded();
				getActivity().finish();
			}
		});

		picture.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				FragmentRemove dp = new FragmentRemove();
				// dp.setOnDateSetListener(new OnDateSetListener() {
				// @Override
				// public void onDateSet(DateSlider view, Calendar selectedDate)
				// {
				// scheduledFor = selectedDate;
				// checkScheduledFor();
				// }
				// });
				dp.setNo(new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						deleteImageIfNeeded();
					}
				});
				dp.setYes(new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						deleteImageIfNeeded();

					}
				});
				dp.show(getSherlockActivity().getSupportFragmentManager(), "dp");
			}
		});

		loadImage();
		checkScheduledFor();

	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (resultCode != Activity.RESULT_OK) {
			return;
		}

		deleteImageIfNeeded();

		if (requestCode == CODE_CAMERA) {
			image = tmpImage;
			System.out.println(image);
			camera = true;
			loadImage();
		}
		if (requestCode == CODE_PICK) {
			camera = false;
			image = data.getData();
			loadImage();
		}
	}

	public void onPause() {
		super.onPause();
		if (scheduleFuture != null) {
			scheduleFuture.cancel(true);
		}
	}

	public void onResume() {
		super.onResume();
		checkScheduledFor();
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);

		if (scheduledFor != null) {
			outState.putLong("scheduledFor", scheduledFor.getTimeInMillis());
		}
		outState.putBoolean("camera", camera);
		if (image != null) {
			outState.putString("image", image.toString());
		}
	}

	private void restore(Bundle b) {
		if (b.containsKey("scheduledFor")) {
			Calendar c = Calendar.getInstance();
			c.setTimeInMillis(b.getLong("scheduledFor"));
			scheduledFor = c;
		}
		if (b.containsKey("image")) {
			image = Uri.parse(b.getString("image"));
		}
		camera = b.getBoolean("camera");
	}

	protected void sendTweet() {
		PendingTweet pt = new PendingTweet();
		pt.forUserId = getApplication().getSession().getAuthenticatedUsers()
				.iterator().next().getId();
		if (image != null) {
			pt.image = image.toString();
		}
		pt.text = text.getText().toString();
		pt.inReplyTo = inReplyTo;
		pt.scheduledFor = scheduledFor;
		if (onSendTweetListener != null) {
			onSendTweetListener.onSendTweet(pt);
		}
	}

	public void setOnSendTweetListener(OnSendTweetListener onSendTweetListener) {
		this.onSendTweetListener = onSendTweetListener;
	}

}
