package us.wmwm.tuxedo.fragments;

import java.util.concurrent.Future;

import twitter4j.Twitter;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;
import us.wmwm.tuxedo.activities.AdvancedTwitterActivity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import us.wmwm.tuxedo.R;

public class FragmentLogin extends FragmentBase {

	private WebView webView;
	
	private Future<?> requestAuthUrl;
	
	private Twitter twitter;
	
	private String _token, _tokenSecret, _verifier;
	
	private String _requestURL;
	private String _authorizeURL;
	
	@Override
	public int getResourceView() {
		return R.layout.fragment_login;
	}
	
	@Override
	protected void initializeViews() {
		webView = findView(R.id.webview);
	}
	
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		WebSettings s = webView.getSettings();
		s.setSaveFormData(false);
		s.setSavePassword(false);
		webView.setWebViewClient(new WebViewClient() {
			
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				if(url.startsWith("http://wmwm.us/callback")) {
					Uri uri = Uri.parse(url);
					_token = uri.getQueryParameter("oauth_token");
					_verifier = uri.getQueryParameter("oauth_verifier");
					if(verifyFuture!=null) {
						verifyFuture.cancel(true);
					}
					verifyFuture = getApplication().getSession().getExecutorService().submit(verifyRunnable);
					return true;
				}
				return super.shouldOverrideUrlLoading(view, url);
			}
		});
		twitter = getApplication().getSession().newTwitterClient();
		requestAuthUrl = getApplication().getSession().getExecutorService().submit(requestAuthRunnable);
	};
	
	private Future<?> verifyFuture;
	
	private Runnable verifyRunnable = new Runnable() {
		public void run() {
			try {
				AccessToken token = twitter.getOAuthAccessToken(new RequestToken(_token, _tokenSecret), _verifier);
				getApplication().getSession().getUserDAO().saveAuthedUser(token);
				Intent i = AdvancedTwitterActivity.intent(getActivity());
				i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(i);
				getActivity().finish();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		};
	};
	private Runnable requestAuthRunnable = new Runnable() {
		@Override
		public void run() {
			try {
				RequestToken token = twitter.getOAuthRequestToken("http://wmwm.us/callback");
				_token = token.getToken();
				_tokenSecret = token.getTokenSecret();
				_requestURL = token.getAuthorizationURL();
				getActivity().runOnUiThread(loadRequestRunnable);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	};
	
	private Runnable loadRequestRunnable = new Runnable() {
		public void run() {
			webView.loadUrl(_requestURL);
		};
	};
	
	
	public void onDestroy() {
		if(requestAuthUrl!=null) {
			requestAuthUrl.cancel(true);
		}
		if(verifyFuture!=null) {
			verifyFuture.cancel(true);
		}
		super.onDestroy();
	};
	
}
