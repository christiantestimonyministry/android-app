package com.christiantapeministry.android;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.Fragment;
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
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.christiantapeministry.android.util.JSONLoader;

public class MainActivity extends Activity implements FragmentPushingActivity {
	JSONObject config;
	private String[] mPlanetTitles = { "Recent", "Speaker", "Venue" };
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
		mDrawerLayout = (DrawerLayout) findViewById(R.id.hamburger_layout);
		mDrawerList = (ListView) findViewById(R.id.hamburger_drawer);
		mDrawerList.setAdapter(new ArrayAdapter<String>(this,
				R.layout.drawer_list_item, mPlanetTitles));
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

		String href = "http://www.christiantapeministry.com/svc-get-recent.php";
		try {
			config = this.getConfig();

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
		setTitle(mPlanetTitles[position]);
		mDrawerLayout.closeDrawer(mDrawerList);

		Log.d(this.getClass().getName(), "Drawer menu item clicked, position "
				+ position);
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

	private class DrawerItemClickListener implements
			ListView.OnItemClickListener {
		@Override
		public void onItemClick(AdapterView parent, View view, int position,
				long id) {
			selectItem(position);
		}
	}

}
