package com.christiantapeministry.android;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.MediaController;
import android.widget.VideoView;



public class MediaActivity extends Activity  {

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.video_layout);
		
		VideoView video = (VideoView)this.findViewById(R.id.mediaView);
		video.setMediaController(new MediaController(this));
		
		Bundle extras = getIntent().getExtras();
		String URL = null;
		if (extras != null) {
			URL = extras.getString("URL");
		}
		
		Log.d(this.getClass().getName(), "URL = "+URL);
		video.requestFocus();
		video.setVideoURI(Uri.parse(URL));
		video.start();	
	}
	
}
