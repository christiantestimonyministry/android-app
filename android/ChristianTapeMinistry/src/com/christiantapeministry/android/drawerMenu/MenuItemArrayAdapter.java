package com.christiantapeministry.android.drawerMenu;

import java.util.List;

import org.json.JSONObject;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.christiantapeministry.android.R;

public class MenuItemArrayAdapter<T> extends ArrayAdapter<T> {
	
	public MenuItemArrayAdapter(Context context, int textViewResourceId,
			List<T> objects) {
		super(context, textViewResourceId, objects);
		// TODO Auto-generated constructor stub
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			LayoutInflater inflater = (LayoutInflater)getContext().getSystemService
				      (Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(R.layout.menu_item_cell, parent, false);
		}
		JSONObject obj = (JSONObject) getItem(position);
		
			try {
				TextView tv= (TextView) convertView;
				
				tv.setText(((JSONObject)obj).getString("title"));
				
			} catch (Exception e) {
				
			}
		
		return convertView;
	}

}
