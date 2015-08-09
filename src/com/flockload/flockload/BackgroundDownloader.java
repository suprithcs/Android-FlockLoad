package com.flockload.flockload;
import java.io.BufferedInputStream;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.util.concurrent.ExecutionException;


import org.apache.http.util.ByteArrayBuffer;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.flockload.flockload.*;
public class BackgroundDownloader extends AsyncTask<DownloadParams, Integer, DownloadParams>{
	private Context context;
	public static int flockFileCount;
	private ProgressDialog dialog;
	private static final String TAG = "FlockLoad";
	private File flockedFilesFolder = null;
	private AsyncResponse listener;
	public BackgroundDownloader(Activity activity) {
		 	context = activity;
	        dialog = new ProgressDialog(activity);
	        listener = (AsyncResponse) activity;
	}
	
	@Override
	protected DownloadParams doInBackground(DownloadParams...dps) {
		// TODO Auto-generated method stub
		
		Integer startByte = dps[0].getStartByteRange();
		Integer endByte = dps[0].getEndByteRange();
		Integer part = dps[0].getPart();
		String fileName = dps[0].getActualFilename()+".part"+part;
		URL targetURL = null;
		try {
			targetURL = new URL(dps[0].getFlockURL());
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)) {

			flockedFilesFolder = new File(Environment.getExternalStorageDirectory() + File.separator + "FlockLoad");
			File downloadedFile = new File(flockedFilesFolder,fileName);
			if(flockedFilesFolder.exists() == false){
				flockedFilesFolder.mkdirs();  
	        }
			try {
		        HttpURLConnection connection =(HttpURLConnection) targetURL.openConnection();
		        connection.setReadTimeout(2000);
		        connection.setConnectTimeout(4000);
		        connection.setRequestMethod("GET");
		        connection.setRequestProperty("Range", "bytes=" + startByte + "-" + endByte);
		        if(connection.getResponseCode() == HttpURLConnection.HTTP_PARTIAL){
		        	InputStream is = connection.getInputStream();
			        System.out.println("downloading...............................");
			        BufferedInputStream bufferinstream = new BufferedInputStream(is);
			        ByteArrayBuffer baf = new ByteArrayBuffer(5000);
			        int current = 0;
			        while((current = bufferinstream.read()) != -1){
			            baf.append((byte) current);
			        }
			        FileOutputStream fos = new FileOutputStream(downloadedFile);
			        fos.write(baf.toByteArray());
			        fos.flush();
			        fos.close();
		        }
			}
	        catch(final IOException e1){
	        	e1.printStackTrace();
	        }
			return dps[0];
		}
		
		else{
			return null;
		}
	}
	
	@Override
	 protected void onPostExecute(DownloadParams dp) {
		 if(dialog.isShowing()) dialog.dismiss();
			 Toast.makeText(context, "File downloaded", Toast.LENGTH_SHORT).show();
			 if (listener != null) 
			  {
			  try {
				listener.processFinish(dp);
			} catch (InterruptedException | ExecutionException | IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
     }
	 
	 @Override
	 protected void onPreExecute() {
	        dialog.setMessage("Downloading the content, please wait..");
	        dialog.show();
	 }
}


