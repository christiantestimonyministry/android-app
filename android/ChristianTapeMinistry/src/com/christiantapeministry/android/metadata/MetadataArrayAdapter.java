package com.christiantapeministry.android.metadata;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import org.json.JSONObject;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.christiantapeministry.android.R;

public class MetadataArrayAdapter<T> extends ArrayAdapter<T> {

	
	public MetadataArrayAdapter(Context context, int textViewResourceId,
			List<T> objects) {
		super(context, textViewResourceId, objects);
		// TODO Auto-generated constructor stub
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			LayoutInflater inflater = (LayoutInflater)getContext().getSystemService
				      (Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(R.layout.metadata_list_cell, parent, false);
		}
		JSONObject obj = (JSONObject) getItem(position);
		
		try {
			TextView name = (TextView) convertView.findViewById(R.id.metadataListName);
			name.setText(obj.getString("name"));
			
			TextView count = (TextView) convertView.findViewById(R.id.metadataListCount);
			count.setText(obj.getString("count"));
		
		} catch (Exception e) {
			
		}
		return convertView;
	}

	


}
