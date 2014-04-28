package com.christiantapeministry.android.metadata;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.christiantapeministry.android.BaseFragment;
import com.christiantapeministry.android.MessageListFragment;
import com.christiantapeministry.android.R;
import com.christiantapeministry.android.network.JSONDataReceiverIF;
import com.christiantapeministry.android.network.LoadHREF;

public class MetadataListFragment extends BaseFragment implements JSONDataReceiverIF {
	ArrayList<JSONObject> rows;

	public MetadataListFragment() {
		super();
		rows = new ArrayList<JSONObject>();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		rootView = inflater.inflate(R.layout.metadata_list_layout, container, false);

		if (!dataLoaded) {
			refreshData(container);

		} else {
			populateListView((ListView) rootView.findViewById(R.id.metadataListView
					));
		}

		return rootView;
	}

	public void refreshData(ViewGroup container) {
		showLoadingDialog(container);
		LoadHREF loader = new LoadHREF();
		loader.setReceiver(this);
		loader.execute(href);
	}

	@Override
	public void receiveObject(JSONObject data) {
		this.dataLoaded = true;
		hideLoadingDialog();
		// Log.d(this.getClass().getName(), data.toString());

		if (data == null) {
			String errorMessage = "Error reading ministry list from the server";
			String errorTitle = "Error loading content";

			showErrorDialog(errorMessage, errorTitle);
		}
		try {

			final JSONArray jsonRows = (JSONArray) data.get("rows");
			ListView listView = (ListView) rootView.findViewById(R.id.metadataListView);

			for (int i = 0; i < jsonRows.length(); i++) {
				JSONObject row = (JSONObject) jsonRows.get(i);
				rows.add(row);
			}

			populateListView(listView);
		} catch (Exception e) {
			Log.e(this.getClass().getName(), "Error loading messages", e);
		}
	}

	protected void populateListView(ListView listView) {
		MetadataArrayAdapter<JSONObject> listItems = new MetadataArrayAdapter<JSONObject>(rootView.getContext(), 
				R.layout.metadata_list_cell, 
				rows);

		listView.setAdapter(listItems);

		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {

				Log.d("s click", "id: " + arg3);
				try {
					JSONObject selectedMinistry = (JSONObject) rows.get((int) arg3);
					String href = selectedMinistry.getString("href");
					String responseType = selectedMinistry.getString("response-type");

					if ("detailList".equals(responseType)) {

						BaseFragment detailFragment = new MessageListFragment();
						detailFragment.setHref(href);
						getContainerActivity().pushFragment(R.layout.fragment_main, detailFragment);

					}
				} catch (JSONException e) {
					Log.e(this.getClass().getName(), "JSON Exception: " + e);
				}
			}
		});
	}
}
