package com.flockload.flockload;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.util.zip.Deflater;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import android.net.Uri;
import android.os.Environment;

public class FlockFileUtils {
	//static Integer BUFFER_SIZE = null;
	
	public static String mergeFlockedFiles(String FilePath) {
		
		String result =  null;
		try {
			result = java.net.URLDecoder.decode(FilePath, "UTF-8");
		} catch (UnsupportedEncodingException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		String fileName = result.substring(result.lastIndexOf('/') + 1);
		if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)) {

			File flockedFilesFolder = new File(Environment.getExternalStorageDirectory() + File.separator + "FlockLoad");
			System.out.println("FlockedFileDir: "+flockedFilesFolder);
		    long leninfile=0, leng=0;
		    int count=1, data=0;
		    int bytesRead = 0;
		    try {
		        File filename = new File(flockedFilesFolder.toString()+"/"+fileName);
		        if(filename.exists()) filename.delete();
		        //BUFFER_SIZE = (int) filename.length();
		        System.out.println("filename: " +filename);
		        FileOutputStream fos = new FileOutputStream(filename,true);
		        
		        while(true) {
		        	File filepart = new File(filename +".part"+count);
		            System.out.println("part filename: " +filepart);
		            if (filepart.exists()) {
		                FileInputStream fis = new FileInputStream(filepart);
		                byte fileBytes[] = new byte[(int)filepart.length()];
		                bytesRead = fis.read(fileBytes, 0,(int)  filepart.length());
				        assert(bytesRead == fileBytes.length);
				        assert(bytesRead == (int) filepart.length());
				        fos.write(fileBytes);
				        fos.flush();
				        fileBytes = null;
				        fis.close();
				        fis = null;
		                count++;
		                filepart.delete();
		            } else break;
		        }
		        fos.close();
		        fos = null;
		    } catch(Exception e) {
		        e.printStackTrace();
		    }
		}
		return fileName;
	}
	
	public static String zip(String filename) throws IOException {
	    BufferedInputStream origin = null;
	    Integer BUFFER_SIZE = 20480;
	    
	    if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)) {

			File flockedFilesFolder = new File(Environment.getExternalStorageDirectory() + File.separator + "FlockLoad");
			System.out.println("FlockedFileDir: "+flockedFilesFolder);
			String uncompressedFile = flockedFilesFolder.toString()+"/"+filename;
			String compressedFile = flockedFilesFolder.toString()+"/"+"flockZip.zip";
		    ZipOutputStream out = new ZipOutputStream(new BufferedOutputStream(new FileOutputStream(compressedFile)));
		    out.setLevel(9);
		    try { 
		    	 	byte data[] = new byte[BUFFER_SIZE];
		            FileInputStream fi = new FileInputStream(uncompressedFile);
		            System.out.println("Filename: "+uncompressedFile);
		            System.out.println("Zipfile: " +compressedFile);
		            origin = new BufferedInputStream(fi, BUFFER_SIZE);
		            try {
		                ZipEntry entry = new ZipEntry(uncompressedFile.substring(uncompressedFile.lastIndexOf("/") + 1));
		                out.putNextEntry(entry);
		                int count;
		                while ((count = origin.read(data, 0, BUFFER_SIZE)) != -1) {
		                    out.write(data, 0, count);
		                }
		            }
		            finally {
		                origin.close();
		            }
		    }
		    finally {
		        out.close();
		    }
		   return "flockZip.zip";
	    }
	    return null;
	}
	
	public static String compressGzipFile(String filename) {
		if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)) {

			File flockedFilesFolder = new File(Environment.getExternalStorageDirectory() + File.separator + "FlockLoad");
			System.out.println("FlockedFileDir: "+flockedFilesFolder);
			String uncompressedFile = flockedFilesFolder.toString()+"/"+filename;
			String compressedFile = flockedFilesFolder.toString()+"/"+"gzip.zip";
		
			try {
	            FileInputStream fis = new FileInputStream(uncompressedFile);
	            FileOutputStream fos = new FileOutputStream(compressedFile);
	            GZIPOutputStream gzipOS = new GZIPOutputStream(fos){{def.setLevel(Deflater.BEST_COMPRESSION);}};
	           
	            byte[] buffer = new byte[1024];
	            int len;
	            while((len=fis.read(buffer)) != -1){
	                gzipOS.write(buffer, 0, len);
	            }
	            //close resources
	            gzipOS.close();
	            fos.close();
	            fis.close();
	            return compressedFile;
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
		}
		return null;
        
    }
	
	private static void decompressGzipFile(String gzipFile) {
        try {
        	File newFile = new File(Environment.getExternalStorageDirectory() + File.separator + "FlockLoad");
            FileInputStream fis = new FileInputStream(gzipFile);
            GZIPInputStream gis = new GZIPInputStream(fis);
            FileOutputStream fos = new FileOutputStream(newFile);
            byte[] buffer = new byte[1024];
            int len;
            while((len = gis.read(buffer)) != -1){
                fos.write(buffer, 0, len);
            }
            //close resources
            fos.close();
            gis.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
         
    }

	public static String unzip(String filename) throws IOException {
		BufferedOutputStream origin = null;
		String path = null;  
		Integer BUFFER_SIZE = 20480;
		if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)) {

				File flockedFilesFolder = new File(Environment.getExternalStorageDirectory() + File.separator + "FlockLoad");
				System.out.println("FlockedFileDir: "+flockedFilesFolder);
				String compressedFile = flockedFilesFolder.toString()+"/"+filename;
				System.out.println("compressed File name:" +compressedFile);
				//String uncompressedFile = flockedFilesFolder.toString()+"/"+"flockUnZip";
				File purgeFile = new File(compressedFile);
			    
		
			try {
		        ZipInputStream zin = new ZipInputStream(new FileInputStream(compressedFile));
		        BufferedInputStream bis = new BufferedInputStream(zin);
		        try {
		            ZipEntry ze = null;
		            while ((ze = zin.getNextEntry()) != null) {
		            	byte data[] = new byte[BUFFER_SIZE];
		                path = flockedFilesFolder.toString()+"/"+ ze.getName();
		                System.out.println("path is:" +path);
		                if (ze.isDirectory()) {
		                    File unzipFile = new File(path);
		                    if(!unzipFile.isDirectory()) {
		                        unzipFile.mkdirs();
		                    }
		                }
		                else {
		                    FileOutputStream fout = new FileOutputStream(path, false);
		                    origin = new BufferedOutputStream(fout, BUFFER_SIZE);
		                    try {
		                       /* for (int c = bis.read(data, 0, BUFFER_SIZE); c != -1; c = bis.read(data)) {
		                        	origin.write(c);
		                        }
		                       */
		                    	 int count;
		                    	while ((count = bis.read(data, 0, BUFFER_SIZE)) != -1) {
		                    		origin.write(data, 0, count);
				                }
		                        zin.closeEntry();
		                    }
		                    finally {
		                        origin.close();
		                    	fout.close();
		                        
		                    }
		                }
		            }
		        }
		        finally {
		        	bis.close();
		            zin.close();
		            purgeFile.delete();
		        }
		    }
		    catch (Exception e) {
		        e.printStackTrace();
		    }
			return path;
	}
	return null;
}
}
