package com.flockload.flockload;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutionException;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

/**
 * A simple server socket that accepts connection and writes some data on
 * the stream.
 */
public class ServerSendAsyncTask extends AsyncTask<DownloadParams, Void, DownloadParams> implements AsyncResponse{

    private Context context;
   // private TextView statusText;
    private static final String TAG = "FileServerAsyncTask";
    private BufferedOutputStream  outToClient = null;
    private ProgressDialog dialog;
    private AsyncResponse listener = null;
    /**
     * @param context
     * @param statusText
     */
    public ServerSendAsyncTask(Context context) {
        this.context = context;
        listener = (AsyncResponse) context;
        //this.statusText = (TextView) statusText;
        dialog = new ProgressDialog(context);
    }

    @Override
    protected DownloadParams doInBackground(DownloadParams...dps) {
    	
    	System.out.println("Inside server");    	
    	Integer part = dps[0].getPart();
    	 		String fileName = dps[0].getActualFilename()+".part"+part;
    	 		File fileToSend = new File(dps[0].getFlockedFilesFolder()+"/"+fileName);
    	 		System.out.println("Sending file to client:" +fileName);
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
    	                 outToClient = new BufferedOutputStream(connectionSocket.getOutputStream());
    	             } catch (IOException ex) {
    	                 // Do exception handling
    	             }

    	             if (outToClient != null) {
    	                 System.out.println("Sending file to client");
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
    	                     outToClient.write(mybytearray, 0, mybytearray.length);
    	                     outToClient.flush();
    	                     outToClient.close();
    	                     connectionSocket.close();
    	                     
    	                 } catch (IOException ex) {
    	                     // Do exception handling
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
    	             }
    	         
    	             return dps[0];
    	       
         }
    /*
     * (non-Javadoc)
     * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
     */
    @Override
    protected void onPostExecute(DownloadParams result) {
    	if(dialog.isShowing()) dialog.dismiss();
    	if (result != null) {
        	Toast.makeText(context, "File sent."+result, Toast.LENGTH_SHORT).show();
        	if (listener != null) 
  		  
  		  try {
  			listener.processFinish(result);
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

    /*
     * (non-Javadoc)
     * @see android.os.AsyncTask#onPreExecute()
     */
    @Override
    protected void onPreExecute() {
    	
    	dialog.setMessage("Sending file to peers, please wait..");
        dialog.show();
    }

}

    	

