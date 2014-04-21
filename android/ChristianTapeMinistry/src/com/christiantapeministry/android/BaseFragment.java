package com.christiantapeministry.android;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.app.Dialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.christiantapeministry.android.network.JSONDataReceiverIF;

public class BaseFragment extends Fragment {

	static final	SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
	static java.text.DateFormat outputDateFormat = null;
	private FragmentPushingActivity	containerActivity;
	protected String href;
	protected View rootView;
	protected ProgressDialog mDialog;
	
	protected	Boolean		dataLoaded;

	public BaseFragment() {
		super();
	}
	@Override
	public void onCreate (Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.dataLoaded = false;
	}
	
	public void refreshData(ViewGroup container) {
		
	}

	public View getRootView() {
		return rootView;
	}

	public void setRootView(View rootView) {
		this.rootView = rootView;
	}

	public FragmentPushingActivity getContainerActivity() {
		return containerActivity;
	}

	public void setContainerActivity(FragmentPushingActivity containerActivity) {
		this.containerActivity = containerActivity;
	}

	public String getHref() {
		return href;
	}

	public void setHref(String href) {
		this.href = href;
	}

	protected void showLoadingDialog(ViewGroup container) {
		mDialog = new ProgressDialog(container.getContext());
	    mDialog.setMessage("Loading...");
	    mDialog.setCancelable(false);
	    mDialog.show();
	}

	protected void hideLoadingDialog() {
		mDialog.hide();
	}

	protected void showErrorDialog(String errorMessage, String errorTitle) {
		Dialog alert = new Dialog(rootView.getContext());
		alert.setTitle(errorTitle);
		
		View dialogView = View.inflate(rootView.getContext(), R.layout.error_dialog, null);
		TextView msg = (TextView) dialogView.findViewById(R.id.error_dialog_message);
		
		msg.setText(errorMessage);
		alert.setContentView(R.layout.error_dialog);
		
		alert.setContentView(dialogView);
		alert.show();
	}

	protected void populateDateView(int fieldId, String dateString) {
		Date convertedDate = new Date();
		    try {
		        convertedDate = dateFormat.parse(dateString);
		        if (outputDateFormat == null) {
			    	outputDateFormat = android.text.format.DateFormat.getDateFormat(rootView.getContext());
			    }
			    dateString = outputDateFormat.format(convertedDate);
			    TextView dateView = (TextView) rootView.findViewById(fieldId);
			    dateView.setText(dateString);
		    } catch (ParseException e) {
		        Log.e(this.getClass().getName(), "Error parsing date: "+dateString);
		    }
	}

	protected void populateTextView(int id, String content) {
		TextView tv = (TextView) rootView.findViewById(id);
		if (tv == null) {
			return;
		}
		tv.setText(content);
		return;
		
	}

}