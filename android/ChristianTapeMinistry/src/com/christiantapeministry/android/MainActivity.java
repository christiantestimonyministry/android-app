package com.christiantapeministry.android;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.christiantapeministry.android.drawerMenu.MenuItemArrayAdapter;
import com.christiantapeministry.android.metadata.MetadataListFragment;
import com.christiantapeministry.android.util.JSONLoader;

public class MainActivity extends Activity implements FragmentPushingActivity {
	JSONObject config;
	private DrawerLayout mDrawerLayout;
	private ListView mDrawerList;

	private ActionBarDrawerToggle mDrawerToggle;
	private CharSequence mDrawerTitle;
	private CharSequence mTitle;

	JSONObject getConfig() {
		if (this.config == null) {
			try {
				this.config = JSONLoader.loadJSONResource(this,
						R.raw.initial_menu);
			} catch (Exception e) {
				Log.e(getLocalClassName(), "Error loading configuration: " + e);
				e.printStackTrace();
			}
		}
		return this.config;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		String href = null;
		href = "http://www.christiantapeministry.com/svc-get-recent.php";
		
		try {
			config = this.getConfig();
			initalizeDrawerMenu(config);
			
			JSONArray menuOptions = config.getJSONArray("menu");

			for (int i = 0; i < menuOptions.length(); i++) {
				JSONObject menuOption = menuOptions.getJSONObject(i);
				if (menuOption.getBoolean("primary")) {
					href = menuOption.getString("href");
				}
			}
			if (href == null) {
				throw new Exception(
						"No primary menu option specified in initial_menu resource, using default");
			}

		} catch (Exception e) {
			Log.e(getLocalClassName(),
					"Error loading initial menu configuration: " + e);

		}

		
		if (savedInstanceState == null) {
			BaseFragment fragment = new MessageListFragment();
			fragment.setHref(href);
			fragment.setContainerActivity(this);
			getFragmentManager().beginTransaction()
					.add(R.id.container, fragment).commit();
		}

	}

	protected void initalizeDrawerMenu(JSONObject config) throws JSONException {
		mDrawerLayout = (DrawerLayout) findViewById(R.id.hamburger_layout);
		mDrawerList = (ListView) findViewById(R.id.hamburger_drawer);
		
		JSONArray menuItems = config.getJSONArray("menu");
		List<JSONObject> menuItemList = new ArrayList<JSONObject>(menuItems.length());
		for(int i=0;i<menuItems.length();i++) {
			menuItemList.add(menuItems.getJSONObject(i));
		}
		mDrawerList.setAdapter(new MenuItemArrayAdapter<JSONObject>(this,
				R.layout.menu_item_cell, menuItemList));
		// Set the list's click listener
		mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

		mTitle = mDrawerTitle = getTitle();
		mDrawerLayout = (DrawerLayout) findViewById(R.id.hamburger_layout);
		mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
				R.drawable.ic_drawer, R.string.drawer_open,
				R.string.drawer_close) {

			/** Called when a drawer has settled in a completely closed state. */
			public void onDrawerClosed(View view) {
				super.onDrawerClosed(view);
				getActionBar().setTitle(mTitle);
				invalidateOptionsMenu(); // creates call to
											// onPrepareOptionsMenu()
			}

			/** Called when a drawer has settled in a completely open state. */
			public void onDrawerOpened(View drawerView) {
				super.onDrawerOpened(drawerView);
				getActionBar().setTitle(mDrawerTitle);
				invalidateOptionsMenu(); // creates call to
											// onPrepareOptionsMenu()
			}
		};

		// Set the drawer toggle as the DrawerListener
		mDrawerLayout.setDrawerListener(mDrawerToggle);

		mDrawerLayout.setDrawerListener(mDrawerToggle);

		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setHomeButtonEnabled(true);
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		// Sync the toggle state after onRestoreInstanceState has occurred.
		mDrawerToggle.syncState();
	}

	@Override
	public void setTitle(CharSequence title) {
		mTitle = title;
		getActionBar().setTitle(mTitle);
	}

	private void selectItem(int position) {
		// Highlight the selected item, update the title, and close the drawer
		mDrawerList.setItemChecked(position, true);
		
		try {
			JSONArray menuItems = config.getJSONArray("menu");
			JSONObject selectObject = menuItems.getJSONObject(position);
			
			setTitle(selectObject.getString("title"));
			mDrawerLayout.closeDrawer(mDrawerList);

			Log.d(this.getClass().getName(), "Drawer menu item clicked, position "
					+ position);
			
			String responseType = selectObject.getString("response-type");
			String href = selectObject.getString("href");
			
			BaseFragment newFragment = null;
			int		layoutId = 0;
			if ("metadataList".equals(responseType)) {
				newFragment = new MetadataListFragment();
				newFragment.setHref(href);
				layoutId = R.layout.metadata_list_layout;
				
				
			} else if ("detailList".equals(responseType)) {
				newFragment = new MessageListFragment();
				newFragment.setHref(href);
				layoutId = R.layout.fragment_main;
			} else if ("detail".equals(responseType)) {
				newFragment = new MinistryDetailFragment();
				newFragment.setHref(href);
				layoutId = R.layout.fragment_ministry_detail;
			}
			if (newFragment != null) {
				pushFragment(layoutId, newFragment);
			}
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);

		try {

			JSONArray menuOptions = this.getConfig().getJSONArray("menu");
			for (int i = 0; i < menuOptions.length(); i++) {
				JSONObject menuOption = menuOptions.getJSONObject(i);
				menu.add(menuOption.getString("title"));
			}

		} catch (JSONException e) {

			e.printStackTrace();
		} catch (Exception e) {

			e.printStackTrace();
		}

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		if (mDrawerToggle.onOptionsItemSelected(item)) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	public void pushFragment(int id, BaseFragment fragment) {
		
		fragment.setContainerActivity(this);
		
		
		// Insert the fragment by replacing any existing fragment
		FragmentManager fragmentManager = getFragmentManager();

		FragmentTransaction transaction = fragmentManager.beginTransaction();
		transaction.replace(R.id.container, fragment);
		transaction.addToBackStack(null);

		// Commit the transaction
		transaction.commit();

	}
	
	public void handleDrawerMenuSelection(JSONObject selectedOption) {
		
	}

	private class DrawerItemClickListener implements
			ListView.OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			selectItem(position);
			
			
		}
	}

}
