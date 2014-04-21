package com.christiantapeministry.android;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.json.JSONObject;

import android.content.Context;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class MinistryArrayAdapter<T> extends ArrayAdapter<T> {
	static final	SimpleDateFormat dateFormat = new SimpleDateFormat("yy-MM-dd HH:mm:ss", Locale.US);
	static java.text.DateFormat outputDateFormat = null;
	
	public MinistryArrayAdapter(Context context, int textViewResourceId,
			List<T> objects) {
		super(context, textViewResourceId, objects);
		// TODO Auto-generated constructor stub
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			LayoutInflater inflater = (LayoutInflater)getContext().getSystemService
				      (Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(R.layout.ministry_list_cell, parent, false);
		}
		JSONObject obj = (JSONObject) getItem(position);
		Log.d(this.getClass().getName(), "In getView, postion = "+position);
		try {
			TextView title = (TextView) convertView.findViewById(R.id.ministryListTitle);
			title.setText(obj.getString("title"));
			
			TextView speaker = (TextView) convertView.findViewById(R.id.ministryListSpeaker);
			speaker.setText(obj.getString("speaker"));
		
			TextView venue = (TextView) convertView.findViewById(R.id.ministryListVenue);
			venue.setText(obj.getString("venue"));
			
			
			//"deliverDate": "2014-04-06 00:00:00",
			
			String dateString = obj.getString("deliverDate");
		   
		    Date convertedDate = new Date();
		    try {
		        convertedDate = dateFormat.parse(dateString);
		    } catch (ParseException e) {
		        // TODO Auto-generated catch block
		        e.printStackTrace();
		    }
		    if (outputDateFormat == null) {
		    	outputDateFormat = android.text.format.DateFormat.getDateFormat(parent.getContext());
		    }
		    dateString = outputDateFormat.format(convertedDate);
		    TextView dateView = (TextView) convertView.findViewById(R.id.ministryListDate);
		    dateView.setText(dateString);
		} catch (Exception e) {
			
		}
		return convertView;
	}

	


}
