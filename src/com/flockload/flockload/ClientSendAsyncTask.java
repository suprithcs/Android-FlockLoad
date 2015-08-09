package com.flockload.flockload;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.widget.Toast;

public class ClientSendAsyncTask extends AsyncTask<DownloadParams, Void, DownloadParams> {
	String dstAddress;
	  int dstPort;
	  Context context = null;
	  ProgressDialog dialog;
	  
	  public ClientSendAsyncTask(Context context){
		  this.context = context;
		  dialog = new ProgressDialog(context);
		  
	  }
	  @Override
	    protected void onPreExecute() {
	    	
	    	dialog.setMessage("Sending file to group owner, please wait..");
	        dialog.show();
	    } 

	@Override
	 protected DownloadParams doInBackground(DownloadParams...dps) {
		// TODO Auto-generated method stub
		System.out.println("Inside client send");
		  Socket clientSocket = null;
		   OutputStream os = null;
		   Integer part = dps[0].getPart()==1?2:2;
		   String fileName = dps[0].getActualFilename()+".part"+part;
	 		File fileToSend = new File(dps[0].getFlockedFilesFolder()+"/"+fileName);
	 		dstAddress = dps[0].getGroupOwnerAddress();
			dstPort = Integer.parseInt(dps[0].getGroupOwnerPort());
			System.out.println("sendingFile" +dps[0].getFlockedFilesFolder()+"/"+fileName);
			System.out.println("dstAdres:" +dstAddress +"dstPort:" +dstPort);
			try {
				   
				   clientSocket = new Socket(dstAddress, dstPort);
				  System.out.println("Requesting connection");
				  if(clientSocket != null){
					  os = new BufferedOutputStream(clientSocket.getOutputStream());
					  System.out.println("Connected to the server" +clientSocket.getRemoteSocketAddress());
				    
				  }
			} 
		    catch (IOException ex) {
	           // Do exception handling
		    }
			if (os != null) {
                System.out.println("Sending file to server");
                byte[] mybytearray = new byte[(int) fileToSend.length()];
                System.out.println("filetoSend" +dps[0].getFlockedFilesFolder()+"/"+fileName);
                FileInputStream fis = null;

                try {
               	 
                    fis = new FileInputStream(fileToSend);
                } catch (FileNotFoundException ex) {
                    // Do exception handling
                }
                BufferedInputStream bis = new BufferedInputStream(fis);

                try {
               	 
                    bis.read(mybytearray, 0, mybytearray.length);
                    os.write(mybytearray, 0, mybytearray.length);
                    os.flush();
                    os.close();
                    os.close();
                    //
                    // File sent, exit the main method
                    
                } catch (IOException ex) {
                    // Do exception handling
                }
                finally{    
   	        	 if(clientSocket != null)
   	        	 try {
   	        		clientSocket.close();
					System.out.println("Client socket closed");
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}  
   	         }
            }

	 		
		return dps[0];
	}
	
	@Override
	  protected void onPostExecute(DownloadParams dp) {
		  if(dialog.isShowing()) dialog.dismiss();
		super.onPostExecute(dp);
		  if(dp !=null){
			  String extension = dp.getFlockURL().substring(dp.getFlockURL().lastIndexOf(".") + 1);
			  Toast.makeText(context, "Recieved the group owner's half", Toast.LENGTH_SHORT).show();
			  	
			  String fileName = FlockFileUtils.mergeFlockedFiles(dp.getFlockURL());
			if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)) {

				File flockedFilesFolder = new File(Environment.getExternalStorageDirectory() + File.separator + "FlockLoad");
				Toast.makeText(context, "flockURL"+dp.getFlockURL(), Toast.LENGTH_SHORT).show();
				System.out.println("FlockedFileDir: "+flockedFilesFolder);
				/*try {
					System.out.println("Here");
					String compressedFile = FlockFileUtils.zip(fileName);
					Toast.makeText(context, "got it"+compressedFile, Toast.LENGTH_SHORT).show();
					
					String unCompressedFile = FlockFileUtils.unzip(compressedFile);
					System.out.println("fielpath:" +unCompressedFile);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}*/
				
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

	  
}
