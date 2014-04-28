package com.christiantapeministry.android.network;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.zip.GZIPInputStream;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.util.Log;

public class LoadHREF extends AsyncTask<String, Integer, String> {
	InputStream inputStream = null;
	String result = "";
	JSONDataReceiverIF		receiver;

	public JSONDataReceiverIF getReceiver() {
		return receiver;
	}

	public void setReceiver(JSONDataReceiverIF receiver) {
		this.receiver = receiver;
	}

	@Override
	protected String doInBackground(String... params) {
		String url_select = params[0];

		ArrayList<NameValuePair> param = new ArrayList<NameValuePair>();

		try {
			// Set up HTTP post

			// HttpClient is more then less deprecated. Need to change to
			// URLConnection
			HttpClient httpClient = new DefaultHttpClient();

			HttpPost httpPost = new HttpPost(url_select);
			httpPost.setEntity(new UrlEncodedFormEntity(param));
			httpPost.setHeader("Accept-Encoding", "gzip");
			
			HttpResponse httpResponse = httpClient.execute(httpPost);
			HttpEntity httpEntity = httpResponse.getEntity();

			// Read content & Log
			inputStream = httpEntity.getContent();
			
			Header contentEncoding = httpResponse.getFirstHeader("Content-Encoding");
			if (contentEncoding != null && contentEncoding.getValue().equalsIgnoreCase("gzip")) {
				inputStream = new GZIPInputStream(inputStream);
			}
			
		} catch (UnsupportedEncodingException e1) {
			Log.e("UnsupportedEncodingException", e1.toString());
			e1.printStackTrace();
		} catch (ClientProtocolException e2) {
			Log.e("ClientProtocolException", e2.toString());
			e2.printStackTrace();
		} catch (IllegalStateException e3) {
			Log.e("IllegalStateException", e3.toString());
			e3.printStackTrace();
		} catch (IOException e4) {
			Log.e("IOException", e4.toString());
			e4.printStackTrace();
		}
		// Convert response to string using String Builder
		try {
			BufferedReader bReader = new BufferedReader(
					new InputStreamReader(inputStream, "iso-8859-1"), 8);
			StringBuilder sBuilder = new StringBuilder();

			String line = null;
			while ((line = bReader.readLine()) != null) {
				sBuilder.append(line + "\n");
			}

			inputStream.close();
			result = sBuilder.toString();

		} catch (Exception e) {
			Log.e("StringBuilding & BufferedReader",
					"Error converting result " + e.toString());
		}
		return result;
	} // protected Void doInBackground(String... params)

	protected void onPostExecute(String resultParam) {
		JSONObject resultDictionary=null;
		try {
			resultDictionary = new JSONObject(result);
			
			
		} catch (Exception e) {

			Log.e(this.getClass().getName(),
					"Error parsing results from recent service", e);
		} finally {
			if (receiver != null) {
				receiver.receiveObject(resultDictionary);
			}
		}

	}
}
