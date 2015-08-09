package com.flockload.flockload;

	import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.concurrent.ExecutionException;

import com.suprith.flockload.R;

	import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class SendFileToServer extends Activity {
	
		private DownloadParams dp;
		@Override
		protected void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			setContentView(R.layout.activity_filetoserver);
			Bundle extras = getIntent().getExtras();
			
			if (extras != null) {
			    dp = (DownloadParams) extras.getSerializable("com.suprith.flockload.DownloadParams");
			}
			
			Button clientButton = (Button)findViewById(R.id.fileToServer);
			
			clientButton.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					
						new ClientSendAsyncTask(SendFileToServer.this).execute(dp);
					
					}
			});
		}
		
		@Override
		public void onDestroy() {
		    super.onDestroy();  // Always call the superclass
		    
		    // Stop method tracing that the activity started during onCreate()
		    
		}
		@Override
		public void onPause(){
			super.onPause();
		}
		
		@Override
		public void onResume(){
			super.onResume();
		}
		
		@Override
		public void onStop(){
			super.onStop();
		}
}