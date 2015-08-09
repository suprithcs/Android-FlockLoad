package com.flockload.flockload;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.concurrent.ExecutionException;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

public class GetContentLengthBackground extends AsyncTask<URL, Integer, Integer> {
	public AsyncResponse delegate=null;
	public Context context;
	private ProgressDialog dialog;
	private AsyncResponse listener;

	
	 @Override
	protected void onPreExecute() {
	        dialog.setMessage("Determining the file size!");
	        dialog.show();
	}
	
	public GetContentLengthBackground(Context context)
	{

	    listener = (AsyncResponse) context;
	    dialog = new ProgressDialog(context);
	}  
	
	@Override
	   protected void onPostExecute(Integer result) {
		if(dialog.isShowing()) dialog.dismiss();
		if (listener != null) 
		  {
		  try {
			listener.processFinish(result);
		} catch (InterruptedException | ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		  }
	   }

	@Override
	protected Integer doInBackground(URL...urls) {
		// TODO Auto-generated method stub
		//URLConnection uconn = null;
		//String contentLengthStr = null;
		Integer lengthOfFile = null;
		try {
			//uconn = urls[0].openConnection();
			//uconn.setReadTimeout(2000);
			//uconn.setConnectTimeout(4000);
			HttpURLConnection connection =(HttpURLConnection) urls[0].openConnection();
			connection.setRequestMethod("HEAD");
			if(connection.getResponseCode() == HttpURLConnection.HTTP_OK){
			    lengthOfFile = connection.getContentLength();
			    connection.disconnect();
			    }
        /*try
        {
        	//uconn.connect();
        	//contentLengthStr=uconn.getHeaderField("content-length");
        	
        }*/
		} 
		catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
		
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        return lengthOfFile;        

		}
}