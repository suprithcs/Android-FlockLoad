package com.flockload.flockload;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.concurrent.ExecutionException;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.widget.Toast;

public class ClientRecieveAsyncTask extends AsyncTask<DownloadParams, Void, DownloadParams> implements AsyncResponse{
	  
	  String dstAddress;
	  int dstPort;
	  String response = "";
	  Context context = null;
	  AsyncResponse listener = null;
	  private ProgressDialog dialog;
	  
	  public ClientRecieveAsyncTask(Context context){
	   this.context = context;
	   this.listener = (AsyncResponse)context;
	   dialog = new ProgressDialog(context);
	  }

	  @Override
	    protected void onPreExecute() {
	    	Toast.makeText(context, "Starting client", Toast.LENGTH_SHORT).show();
	    	dialog.setMessage("Recieving file from group owner, please wait..");
	        dialog.show();
	    }
	  
	  
	  @Override
	  protected DownloadParams doInBackground(DownloadParams...dps) {
	   
		  System.out.println("Inside client");
		  Socket clientSocket = null;
		   InputStream is = null;
		   PrintStream os = null;
		   Integer part = dps[0].getPart();
			String serverFile = dps[0].getActualFilename()+".part"+1;
			String flockURL = dps[0].getFlockURL();
			String extension = dps[0].getFileExtension();
			File recievedFile = new File(dps[0].getFlockedFilesFolder()+"/"+serverFile);
			dstAddress = dps[0].getGroupOwnerAddress();
			dstPort = Integer.parseInt(dps[0].getGroupOwnerPort());
			System.out.println("recievedFile" +dps[0].getFlockedFilesFolder()+"/"+serverFile);
			System.out.println("dstAdres:" +dstAddress +"dstPort:" +dstPort);
			byte[] aByte = new byte[1];
		        int bytesRead;
		   
		    
		        try {
			   
			   clientSocket = new Socket(dstAddress, dstPort);
			  System.out.println("Requesting connection");
			  if(clientSocket != null){
				  is = clientSocket.getInputStream();
				  System.out.println("Connected to the server" +clientSocket.getRemoteSocketAddress());
			  }
		    	} 
		    catch (IOException ex) {
	           // Do exception handling
		    }

	       ByteArrayOutputStream baos = new ByteArrayOutputStream();

	       if (is != null) {
	    	   System.out.println("writing file");
	           FileOutputStream fos = null;
	           BufferedOutputStream bos = null;
	           try {
	               fos = new FileOutputStream( recievedFile );
	               bos = new BufferedOutputStream(fos);
	               bytesRead = is.read(aByte, 0, aByte.length);

	               do {
	                       baos.write(aByte);
	                       bytesRead = is.read(aByte);
	               } while (bytesRead != -1);

	               bos.write(baos.toByteArray());
	               bos.flush();
	               bos.close();
	               
	           } catch (IOException ex) {
	               // Do exception handling
	        	   ex.printStackTrace();
	           }
	       }
	       try {
	    	   if(clientSocket != null)
	    		   clientSocket.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	       return dps[0];
	  }

	  @Override
	  protected void onPostExecute(DownloadParams dp) {
		  if(dialog.isShowing()) dialog.dismiss();
		  if(dp !=null){
			  String extension = dp.getFlockURL().substring(dp.getFlockURL().lastIndexOf(".") + 1);
		  if (listener != null) 
	  		  
	  		  try {
	  			listener.processFinish(dp);
	  		} catch (InterruptedException | ExecutionException | IOException e) {
	  			// TODO Auto-generated catch block
	  			e.printStackTrace();
	  		}
	   	  }
	 }

	@Override
	public void processFinish(Integer result) throws InterruptedException,
			ExecutionException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void processFinish(DownloadParams dp) throws InterruptedException,
			ExecutionException {
		// TODO Auto-generated method stub
		
	}
}
