package com.flockload.flockload;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.widget.Toast;

public class ServerRecieveFileFromClientAsyncTask extends AsyncTask<DownloadParams, Void, DownloadParams> {
	private Context context;
	private ProgressDialog dialog;
	public ServerRecieveFileFromClientAsyncTask(Context context){
		this.context = context;
		dialog = new ProgressDialog(context);
	}
	
	@Override
    protected DownloadParams doInBackground(DownloadParams...dps) {
		Integer part = dps[0].getPart()==1?2:1;
		String fileName = dps[0].getActualFilename()+".part"+part;
		File clientFile = new File(dps[0].getFlockedFilesFolder()+"/"+fileName);
		String extension = dps[0].getFileExtension();
		System.out.println("clientFile" +dps[0].getFlockedFilesFolder()+"/"+fileName);
		//System.out.println("dstAdres:" +dstAddress +"dstPort:" +dstPort);
		byte[] aByte = new byte[1];
	        int bytesRead;
	        InputStream is = null;
	        ServerSocket serverSocket = null;
	 		Socket connectionSocket = null;
	 		int port = Integer.valueOf(dps[0].getGroupOwnerPort());
	 		System.out.println("port: "+port);
	 		String message = null;
	 		BufferedReader in = null;
	 		try {
				serverSocket = new ServerSocket(port);
				System.out.println("Created server socket");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
	 		
	             try {
	            	 
	                 
	                 System.out.println("Server: Socket opened");
	                 connectionSocket = serverSocket.accept();
	                 System.out.println("Server: got a client" +"Address:" +connectionSocket.getRemoteSocketAddress());
	             }
	             catch (IOException ex) {
	                 // Do exception handling
	             }
	             
	             if(connectionSocket != null){
	            	 try {
						is = connectionSocket.getInputStream();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
	            	 System.out.println("Connected to the client" +connectionSocket.getRemoteSocketAddress());
	             }
	             ByteArrayOutputStream baos = new ByteArrayOutputStream();

	  	       if (is != null) {
	  	    	   System.out.println("geting file");
	  	           FileOutputStream fos = null;
	  	           BufferedOutputStream bos = null;
	  	           try {
	  	               fos = new FileOutputStream( clientFile );
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
	  	    	   if(connectionSocket != null)
	  	    		 connectionSocket.close();
	  		} catch (IOException e) {
	  			// TODO Auto-generated catch block
	  			e.printStackTrace();
	  		}
	  	     finally{    
	        	 if(serverSocket != null)
	        	 try {
				serverSocket.close();
				System.out.println("Server socket closed");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
	  	     }

	   return dps[0];
		
	} 
	  	       
	  	     @Override
	  	    protected void onPostExecute(DownloadParams dp) {
	  	    	super.onPostExecute(dp);
	  	    	if(dialog.isShowing()) dialog.dismiss();
	  		  if(dp !=null){
	  			  String extension = dp.getFlockURL().substring(dp.getFlockURL().lastIndexOf(".") + 1);
	  			  Toast.makeText(context, "Recieved the group owner's half", Toast.LENGTH_SHORT).show();
	  			  	
	  			  String fileName = FlockFileUtils.mergeFlockedFiles(dp.getFlockURL());
	  			if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)) {

	  				File flockedFilesFolder = new File(Environment.getExternalStorageDirectory() + File.separator + "FlockLoad");
	  				Toast.makeText(context, "flockURL"+dp.getFlockURL(), Toast.LENGTH_SHORT).show();
	  				System.out.println("FlockedFileDir: "+flockedFilesFolder);
	  				
	  				Uri itemUri = Uri.parse(flockedFilesFolder.toString()+"/"+fileName);
	  				
	  				System.out.println("itemUri: " +itemUri);
	  				Intent intent = new Intent();
	  				intent.setAction(Intent.ACTION_VIEW);
	  				if(extension.equals("png")|| extension.equals("jpg") || extension.equals("gif") || extension.equals("gif") || extension.equals("bmp") || extension.equals("WebP") )
	  					intent.setDataAndType(Uri.parse("file://" + itemUri.toString()), "image/*");
	  				else if(extension.equals("3gp")|| extension.equals("mp4") || extension.equals("webm") || extension.equals("mkv"))
	  					intent.setDataAndType(Uri.parse("file://" + itemUri.toString()), "video/*");
	  				else if(extension.equals("mp3") || extension.equals("flac") || extension.equals("wav"))
	  					intent.setDataAndType(Uri.parse("file://" + itemUri.toString()), "audio/*");
	  				context.startActivity(intent);
	  			}
	  		  }

	  	    	

	  	    }
	  	     
	  	   @Override
	  	 protected void onPreExecute() {
	  	        dialog.setMessage("Recieving the file part from client, please wait..");
	  	        dialog.show();
	  	 }

}
