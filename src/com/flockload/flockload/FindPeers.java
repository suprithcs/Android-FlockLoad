package com.flockload.flockload;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import com.suprith.flockload.R;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.net.wifi.WpsInfo;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.ActionListener;
import android.net.wifi.p2p.WifiP2pManager.Channel;
import android.net.wifi.p2p.WifiP2pManager.ConnectionInfoListener;
import android.net.wifi.p2p.WifiP2pManager.PeerListListener;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

public class FindPeers extends Activity implements OnClickListener, android.content.DialogInterface.OnClickListener, AsyncResponse {
	Channel mChannel;
	WifiP2pManager mManager;
	WiFiDirectBroadcastReceiver mReceiver;
	private IntentFilter intentFilter;
	private ListView PeerList;
	String flockURL;
	Integer contentSize;
	String actualFilename =  null;	
	String flockedFilesFolder = null;
	String fileExtension = null;
	
	
	public boolean isConnectingToInternet(){
        ConnectivityManager connectivity = (ConnectivityManager) FindPeers.this.getSystemService(Context.CONNECTIVITY_SERVICE);
          if (connectivity != null) 
          {
              NetworkInfo[] info = connectivity.getAllNetworkInfo();
              if (info != null) 
                  for (int i = 0; i < info.length; i++) 
                      if (info[i].getState() == NetworkInfo.State.CONNECTED)
                      {
                          return true;
                      }
 
          }
          return false;
    }
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	    setContentView(R.layout.activity_findpeers);
	    Bundle extras = getIntent().getExtras();
		if (extras != null) {
		    flockURL = extras.getString("FLOCKURL");
		    flockedFilesFolder = extras.getString("FLOCKLOADFOLDER");
		    fileExtension = extras.getString("FILEEXTENSION");
		    if(extras.getString("TOTALSIZE") != null)
		    	contentSize = Integer.parseInt(extras.getString("TOTALSIZE"));
		    
			try {
				String result = java.net.URLDecoder.decode(flockURL.toString(), "UTF-8");
				actualFilename = result.substring(result.lastIndexOf('/') + 1);
			} catch (UnsupportedEncodingException e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
			}
		}
		
		intentFilter = new IntentFilter();
		 //  Indicates a change in the Wi-Fi P2P status.
	    intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);

	    // Indicates a change in the list of available peers.
	    intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);

	    // Indicates the state of Wi-Fi P2P connectivity has changed.
	    intentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);

	    // Indicates this device's details have changed.
	    intentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);
	    mManager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
		mChannel = mManager.initialize(this, getMainLooper(), null);
		mReceiver = new WiFiDirectBroadcastReceiver(mManager, mChannel, this);
		Button Discover = (Button)findViewById(R.id.FindAllNearByDevices);
		PeerList = (ListView) findViewById(R.id.listView1);
		registerReceiver(mReceiver, intentFilter);
		
		Discover.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				try {
					mManager.discoverPeers(mChannel, new ActionListener() {
			            @Override
			            public void onSuccess() {
			                Toast.makeText(FindPeers.this, "Discovered peers successfully.", Toast.LENGTH_SHORT).show();
			            }
			            @Override
			            public void onFailure(int reason) {
			                Toast.makeText(FindPeers.this, "Discover peers failed.", Toast.LENGTH_SHORT).show();
			            }
			        });
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				}
		});
	}
	
	@Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        if(mReceiver != null){
        	unregisterReceiver(mReceiver);
        	mReceiver = null;
        	}
    }

	@Override
	public void onClick(DialogInterface dialog, int which) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onClick(View v) {
	    switch (v.getId()) {
	    case R.id.FindAllNearByDevices:
	        break;
	    }
	}
	
	private class WiFiDirectBroadcastReceiver extends BroadcastReceiver{

	
		private WifiP2pManager mManager;
	    private Channel mChannel;
	    private Context mActivity;
	    private ArrayList<WifiP2pDevice> mDeviceList = new ArrayList<WifiP2pDevice>();
	    public ArrayAdapter mAdapter;
	    private PeerListListener peerListener;
	    private ConnectionInfoListener connectionListener;
	    private WifiP2pInfo info;
	    int flag=0;
	    public WiFiDirectBroadcastReceiver(WifiP2pManager manager, Channel channel,
	            Context activity) {
	        super();
	        this.mManager = manager;
	        this.mChannel = channel;
	        this.mActivity = activity;
	       
	    }

	    @Override
	    public void onReceive(Context context, Intent intent) {
	        String action = intent.getAction();
	        String message = intent.getStringExtra("action");
	        Toast.makeText(FindPeers.this, message, Toast.LENGTH_SHORT);
	        if (WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION.equals(action)) {

	            int state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1);
	            if (state == WifiP2pManager.WIFI_P2P_STATE_ENABLED) {
	                   String title = "ANDROID_ID[" + getAndroid_ID() + "]";
	                   title += "   MAC[" + getMACAddress() + "]";
	                Toast.makeText(mActivity, "Wi-Fi Direct is enabled."+title, Toast.LENGTH_SHORT).show();
	            } else {
	                Toast.makeText(mActivity, "Wi-Fi Direct is disabled.", Toast.LENGTH_SHORT).show();
	            }

	        } else if (WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION.equals(action)) {
	        	System.out.println("Intention recieved");
	        	
	           
	            	if (mManager != null) {
	                    mManager.requestPeers(mChannel, new PeerListListener() {

	                        @Override
	                        public void onPeersAvailable(WifiP2pDeviceList peers) {
	                   
	                            if (peers != null) {
	                            	mDeviceList.clear();
	                                mDeviceList.addAll(peers.getDeviceList());
	                                ArrayList<String> deviceNames = new ArrayList<String>();
	                                for (WifiP2pDevice device : mDeviceList) {
	                                    deviceNames.add(device.deviceName);
	                                }
	                                if (deviceNames.size() > 0) {
	                                	Toast.makeText(FindPeers.this, "Peers found", Toast.LENGTH_SHORT).show();
	                                	
	                                    mAdapter = new ArrayAdapter<String>(mActivity, android.R.layout.simple_list_item_1, deviceNames);
	                                    if(flag==0)
	                                    {
	                                        flag=1;
	                                        showDeviceListDialog();
	                                    }
	                                } else {
	                                    Toast.makeText(mActivity, "Device list is empty.", Toast.LENGTH_SHORT).show();
	                                }
	                            }
	                        }
	                    });
	                }

	        } else if (WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION.equals(action)) {
	        	
	        	if (mManager == null) {
	                return;
	            }

	            NetworkInfo networkInfo = (NetworkInfo) intent
	                    .getParcelableExtra(WifiP2pManager.EXTRA_NETWORK_INFO);
	            
	            if (networkInfo.isConnected()) {
	                mManager.requestConnectionInfo(mChannel, new ConnectionInfoListener() {
						
						@Override
						public void onConnectionInfoAvailable(WifiP2pInfo info) {
							
							// TODO Auto-generated method stub
							System.out.println("Came here!!!!!!!!!!!!!!!!");
		    	        	String groupOwnerAddress = info.groupOwnerAddress.getHostAddress();
		    	        	
		    		        Toast.makeText(FindPeers.this, groupOwnerAddress, Toast.LENGTH_SHORT).show();
		    		     // The owner IP is now known.
		    		        
		    		        // After the group negotiation, we can determine the group owner.
		    		        Integer temp = (int) Math.ceil((contentSize/2));
		    		        
		    		        if (info.groupFormed && info.isGroupOwner) {
		    		        	Toast.makeText(FindPeers.this, "Group formed", Toast.LENGTH_SHORT).show();
		    		        	
		    		        	DownloadParams partDownload = new DownloadParams(0, temp, flockURL.toString(),1, actualFilename, flockedFilesFolder.toString(),null,"8988",fileExtension,contentSize.toString());
		    		    		
		    		        	while(true){
		    		        		if(isConnectingToInternet()) break;
		    		        	}
		    		        	new BackgroundDownloader(FindPeers.this).execute(partDownload);
								
		    		        } else if (info.groupFormed) {
		    		        	Toast.makeText(FindPeers.this, "I'm the client", Toast.LENGTH_SHORT).show();
		    		        	DownloadParams partDownload2 = new DownloadParams(temp+1, contentSize, flockURL.toString(),2, actualFilename,flockedFilesFolder.toString(),groupOwnerAddress, "8988",fileExtension,null);
		    		        	while(true){
		    		        		if(isConnectingToInternet()) break;
		    		        	}
		    		        	new BackgroundDownloader(FindPeers.this).execute(partDownload2);
		    		        }
						}
					});
	                
	            }
	        } else if (WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION.equals(action)) {

	        }
	    }
    private void showDeviceListDialog() {
        DeviceListDialog deviceListDialog = new DeviceListDialog();
        deviceListDialog.show(getFragmentManager(), "devices");
        
    }
	private class DeviceListDialog extends DialogFragment  {
		
	    public Dialog onCreateDialog(Bundle savedInstanceState) {
	        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
	        builder.setTitle("Choose a device")
	               .setSingleChoiceItems(mAdapter, 0, (android.content.DialogInterface.OnClickListener) mActivity)
	               .setPositiveButton("Connect", new DialogInterface.OnClickListener() {
	            	   public void onClick(DialogInterface dialog, int id) {
	            		   WifiP2pDevice device = mDeviceList.get(0);
	                    	System.out.println("Device address is :" +device.deviceAddress);
	                        WifiP2pConfig config = new WifiP2pConfig();
	                        config.groupOwnerIntent = 15;
	                        config.deviceAddress = device.deviceAddress;
	                        config.wps.setup = WpsInfo.PBC;
                        	mManager.connect(mChannel, config, new ActionListener() {

                            @Override
                            public void onSuccess() {
                                // WiFiDirectBroadcastReceiver will notify us. Ignore for now.
                            	Toast.makeText(mActivity, "Connection established!", Toast.LENGTH_SHORT).show();
                            }
                            @Override
                            public void onFailure(int reason) {
                                //Toast.makeText(.this, "Connect failed. Retry."+reason,Toast.LENGTH_SHORT).show();
                            	Toast.makeText(mActivity, "Connect failed. Retry.",Toast.LENGTH_SHORT).show();
                            }
                        });
	            	   	}
	               		})
	               .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
	
	                    @Override
	                    public void onClick(DialogInterface dialog, int which) {
	                    	dialog.dismiss();
	                    	System.out.println("Came here!!!!!!!!!!!!!!!!");
	                    }
	               });
	        return builder.create();
	    }
	}

	    /**
	     * ANDROID_ID
	     */
	    private String getAndroid_ID() {
	        return Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
	    }
	
	    /**
	     * Wi-Fi MAC
	     */
	    private String getMACAddress() {
	        WifiManager manager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
	        WifiInfo wifiInfo = manager.getConnectionInfo();
	        String mac = wifiInfo.getMacAddress();
	        return mac;
	    }
	}
	
        
    public static boolean copyFile(InputStream inputStream, OutputStream out) {
        byte buf[] = new byte[1024];
        int len;
        long startTime=System.currentTimeMillis();
        
        try {
            while ((len = inputStream.read(buf)) != -1) {
                out.write(buf, 0, len);
            }
            out.close();
            inputStream.close();
            long endTime=System.currentTimeMillis()-startTime;
            Log.v("","Time taken to transfer all bytes is : "+endTime);
            
        } catch (IOException e) {
            Log.d("CopyFILE", e.toString());
            return false;
        }
        return true;
    }

	@Override
	public void processFinish(Integer result) throws InterruptedException,
			ExecutionException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void processFinish(DownloadParams dp) throws InterruptedException, ExecutionException, IOException {
		// TODO Auto-generated method stub
		if(dp != null){
			System.out.println("reached where it matters");
		if(dp.getPart() == 1){
			System.out.println("Starting server task");
			Intent intent = new Intent(FindPeers.this,SendFileToClient.class);
			intent.putExtra("com.suprith.flockload.DownloadParams", dp);
			startActivity(intent);
			//finish();
		}
			
		else{
			System.out.println("Starting client task");
			Intent intent = new Intent(FindPeers.this,GetFileFromServer.class);
			intent.putExtra("com.suprith.flockload.DownloadParams", dp);
			String compressedFile;
			startActivity(intent);
			
		}
	}
	}
	
	@Override
	public void onDestroy(){
		super.onDestroy();
	}
	
	@Override
	public void onStop(){
		super.onStop();
	}
	

}
