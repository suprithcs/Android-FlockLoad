package com.flockload.flockload;

import java.io.File;
import java.net.*;
import java.util.concurrent.ExecutionException;
import com.suprith.flockload.R;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;

public class MainActivity extends Activity implements AsyncResponse{
	private Integer contentLength = null;
	private URL flockURL = null;
	String extension = null;
	private File flockedFilesFolder = null;
	CheckBox prefCheckBox = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)) {

			flockedFilesFolder = new File(Environment.getExternalStorageDirectory() + File.separator + "FlockLoad");
			if(flockedFilesFolder.exists() == false){
				flockedFilesFolder.mkdirs();  
	        }
		}
		
		Button FlockHit = (Button)findViewById(R.id.FlockURL);
		final EditText testURL   = (EditText)findViewById(R.id.FlockLoadURLInput);
		FlockHit.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
				extension = testURL.getText().toString().substring(testURL.getText().toString().lastIndexOf(".") + 1);
				try {
					flockURL = new URL(testURL.getText().toString());
					GetContentLengthBackground lengthTask = new GetContentLengthBackground(MainActivity.this);
					lengthTask.execute(flockURL);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		switch (item.getItemId()) {
		
	    case R.id.action_settings:
	    	Intent intent = new Intent();
	        intent.setClass(MainActivity.this, SetPreferenceActivity.class);
	        startActivityForResult(intent, 0); 
	        return true;
	    
	    default:
	    return super.onOptionsItemSelected(item);
	}
		
	}

	@Override
	public void processFinish(Integer output) throws InterruptedException, ExecutionException {
		// TODO Auto-generated method stub
		this.contentLength = output;
		Toast.makeText(MainActivity.this, "got it"+contentLength, Toast.LENGTH_SHORT).show();
		Intent intent = new Intent(MainActivity.this,FindPeers.class);
		intent.putExtra("FLOCKURL", flockURL.toString());
		intent.putExtra("TOTALSIZE", String.valueOf(contentLength));
		intent.putExtra("FLOCKLOADFOLDER", flockedFilesFolder.toString());
		intent.putExtra("FILEEXTENSION",extension);
		
		ConnectivityManager connManager = (ConnectivityManager) getSystemService(MainActivity.this.CONNECTIVITY_SERVICE);
		NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

		while(!mWifi.isConnected()){
			WifiManager wifi = (WifiManager) getSystemService(MainActivity.this.WIFI_SERVICE);
			wifi.setWifiEnabled(true); // true or false to activate/deactivate wifi
			if(mWifi.isConnected()) break;
		}
		
		startActivity(intent);
	}

	@Override
	public void processFinish(DownloadParams dp) throws InterruptedException,
			ExecutionException {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void onStop(){
		super.onStop();
	}
	
	@Override
	public void onDestroy() {
	    super.onDestroy();  // Always call the superclass
	    
	    // Stop method tracing that the activity started during onCreate()
	    
	}
	
	
	@Override
	 protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	  // TODO Auto-generated method stub
	  //super.onActivityResult(requestCode, resultCode, data);
	  loadPref();
	 }
	    
	 private void loadPref(){
	  SharedPreferences mySharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
	  boolean my_checkbox_preference = mySharedPreferences.getBoolean("checkbox_preference", false);
	  prefCheckBox.setChecked(my_checkbox_preference);

	 }
}
