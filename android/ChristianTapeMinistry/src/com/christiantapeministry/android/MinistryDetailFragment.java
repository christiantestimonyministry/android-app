package com.christiantapeministry.android;

import java.io.IOException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import com.christiantapeministry.android.network.JSONDataReceiverIF;
import com.christiantapeministry.android.network.LoadHREF;

/**
	 * A placeholder fragment containing a simple view.
	 */
	public class MinistryDetailFragment extends BaseFragment implements
			JSONDataReceiverIF {
		
		private			String		videoURL;
		private			String		audioURL;
		
		public MinistryDetailFragment() {
			super();
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			rootView = inflater.inflate(R.layout.fragment_ministry_detail,
					container, false);

			showLoadingDialog(container);
			
			LoadHREF loader = new LoadHREF();
			loader.setReceiver(this);
			loader.execute(href);
			return rootView;
		}

		private void populateViewFromJSON(JSONObject json) {
			hideLoadingDialog();
			if (json == null) {
				String errorMessage = "Error reading ministry list from the server";
				String errorTitle = "Error loading content";
				
				
				showErrorDialog(errorMessage, errorTitle);
			}
			try {
				populateTextView(R.id.ministryDetailTitle, json.getString("title"));
				populateTextView(R.id.ministryDetailSpeaker, json.getString("speaker"));
				populateTextView(R.id.ministryDetailVenue, json.getString("venue"));
				
				
				populateDateView( R.id.ministryDetailDate, json.getString("deliverDate"));
				    
				
				
				// populate summary
				populateSummary(json);
				
				// load subjects
				populateSubject(json);
				
				
				populateReferences(json);
				
				Boolean		videoStreamPresent = false;
				Boolean 	audioStreamPresent = false;
				JSONArray mediaData = json.getJSONArray("mediaData");
				for(int i=0;i<mediaData.length();i++) {
					JSONObject media = mediaData.getJSONObject(i);
					
					String mediaType = media.getString("mediaType");
					if ("3".equals(mediaType)) {
						Button listenButton = (Button) rootView.findViewById(R.id.ministryDetailListenButton);
						audioStreamPresent= true;
						audioURL = media.getString("streamURL");
						listenButton.setOnClickListener(new View.OnClickListener() {
				             public void onClick(View v) {
				                 Log.d(this.getClass().getName(), "Watch button pressed on audio "+videoURL);
				                 
				                 Intent videoIntent = new Intent(v.getContext(), MediaActivity.class );
				                 videoIntent.putExtra("URL", audioURL);
				                 videoIntent.putExtra("mediaType", "3");
				                 startActivity(videoIntent);
				                 
				             }
				         });
					}
					if ("8".equals(mediaType)) {
						// video
						videoStreamPresent = true;
						Button watchButton = (Button) rootView.findViewById(R.id.ministryDetailWatchButton);
						
						videoURL = media.getString("streamURL");
						watchButton.setOnClickListener(new View.OnClickListener() {
				             public void onClick(View v) {
				                 Log.d(this.getClass().getName(), "Watch button pressed on video "+videoURL);
				                 
				                 try {
//									MediaPlayer mp = new MediaPlayer();
//									 Uri uri = Uri.parse(videoURL);
//									 mp.setDataSource(videoURL);
//									 
//									 mp.prepare();
//									 mp.start();
									 
									 Intent videoIntent = new Intent(v.getContext(), MediaActivity.class );
									 videoIntent.putExtra("URL", videoURL);
									 videoIntent.putExtra("mediaType", "8");
									 startActivity(videoIntent);
								} catch (Exception e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								} 
				                 
				             }
				         });
						
					}
				}
				if (!videoStreamPresent) {
					// hide the watch button
					Button watchButton = (Button) rootView.findViewById(R.id.ministryDetailWatchButton);
					watchButton.setVisibility(View.GONE);
				}
				if (!audioStreamPresent) {
					Button listenButton = (Button) rootView.findViewById(R.id.ministryDetailListenButton);
					listenButton.setVisibility(View.GONE);
				}
				if (!audioStreamPresent && !videoStreamPresent) {
					LinearLayout buttonArea = (LinearLayout) rootView.findViewById(R.id.ministrydetailButtonArea);
					buttonArea.setVisibility(View.GONE);
				}

			} catch (JSONException je) {
				Log.e(this.getClass().getName(), "Error creating ministry detail fragment: "+je.getLocalizedMessage());
			}

		}

		protected void populateReferences(JSONObject json) throws JSONException {
			JSONArray refs = json.getJSONArray("scriptures");
			LinearLayout refLayout =(LinearLayout) rootView.findViewById(R.id.ministryDetailReferenceListLayout);
			
			for(int i=0;i<refs.length();i++) {
				String ref = refs.getString(i);
				
				LayoutParams lparams = new LayoutParams(
						   LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
						TextView tv=new TextView(refLayout.getContext());
						tv.setLayoutParams(lparams);
						tv.setText(ref);
						refLayout.addView(tv);
				
			}
		}

		protected void populateSubject(JSONObject json) throws JSONException {
			JSONArray subjects = json.getJSONArray("subjects");
			LinearLayout subjectLayout =(LinearLayout) rootView.findViewById(R.id.ministryDetailSubjectListLayout);
			
			for(int i=0;i<subjects.length();i++) {
				JSONObject subj = subjects.getJSONObject(i);
				
				LayoutParams lparams = new LayoutParams(
						   LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
						TextView tv=new TextView(subjectLayout.getContext());
						tv.setLayoutParams(lparams);
						tv.setText(subj.getString("name"));
						subjectLayout.addView(tv);
				
			}
		}

		protected void populateSummary(JSONObject json) throws JSONException {
			WebView webView = (WebView) rootView.findViewById(R.id.ministryDetailSummary);
			
			String content = json.getString("summary");
			webView.loadData(content, "text/html", null);
		}

		@Override
		public void receiveObject(JSONObject data) {
			// TODO Auto-generated method stub
			Log.d(this.getClass().getName(), data.toString());

			populateViewFromJSON(data);

		}

	}