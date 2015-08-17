package com.androidfuture.frames.tools;
import java.io.File;  
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;  
import java.io.IOException;  
import java.io.InputStream;  
import java.io.OutputStream;  

  
import android.app.ActivityManager;
import android.content.ContentResolver;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;  
import android.util.Log;
  
public class FileUtils {  
	    private static String SDPATH = Environment.getExternalStorageDirectory() + "/";
	      
		private static final  int maxSize = 512 * 1<<10;//128K
		
	    private int FILESIZE = 4 * 1024;   
	      
	    public static String getSDPATH(){  
	        return SDPATH;  
	    }  
	   
	    /**  
	     * 
	     * @param fileName  
	     * @return  
	     * @throws IOException  
	     */  
	    public File createSDFile(String fileName) throws IOException{  
	        File file = new File(SDPATH + fileName);  
	        file.createNewFile();  
	        return file;  
	    }  
	      
	    /**  
	     * 
	     * @param dirName  
	     * @return  
	     */  
	    public File createSDDir(String dirName){  
	        File dir = new File(dirName);  
	        dir.mkdir();  
	        return dir;  
	    }  
	      
	    /**  
	     *
	     * @param fileName  
	     * @return  
	     */  
	    static public boolean isFileExist(String filePathName){  
	        File file = new File(filePathName);
	               return file.exists();  
	    }  
	    
	      
	    
	    public boolean removeFile(String filePathName)
	    {
	    	File file = new File(filePathName);
	    	return file.delete();
	    }
	    /**  
	     * 
	     * @param path  
	     * @param fileName  
	     * @param input  
	     * @return  
	     */  
	    public File write2SDFromInput(String path,String fileName,InputStream input){  
	        File file = null;  
	        OutputStream output = null;  
	        try {  
	            createSDDir(path);  
	            file = createSDFile(path + fileName);  
	            output = new FileOutputStream(file);  
	            byte[] buffer = new byte[FILESIZE];  
	            while((input.read(buffer)) != -1){  
	                output.write(buffer);  
	            }  
	            output.flush();  
	        }   
	        catch (Exception e) {  
	            e.printStackTrace();  
	        }  
	        finally{  
	            try {  
	                output.close();  
	            } catch (IOException e) {  
	                e.printStackTrace();  
	            }  
	        }  
	        return file;  
	    }  
	 
	    
	    public InputStream readFromSD(String path,String fileName)
	    {
	    	InputStream in = null;
			try {
				
				in = new FileInputStream(path + File.separator +  fileName);
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return in;
	    }
	    
	    public static boolean delAllFile(String path) {
	        boolean bea = false;
	        File file = new File(path);
	        if (!file.exists()) {
	            return bea;
	        }
	        if (!file.isDirectory()) {
	            return bea;
	        }
	        String[] tempList = file.list();
	        File temp = null;
	        for (int i = 0; i < tempList.length; i++) {
	            if (path.endsWith(File.separator)) {
	                temp = new File(path + tempList[i]);
	            } else {
	                temp = new File(path + File.separator + tempList[i]);
	            }
	            if (temp.isFile()) {
	                temp.delete();
	            }else if (temp.isDirectory()) {
	                delAllFile(path + File.separator + tempList[i]);
	                delDir(path + File.separator + tempList[i],true);
	                bea = true;
	            }
	        }
	        return bea;
	    }
	    
	    public static void delDir(String folderPath,boolean dirDel) {
	        try {
	            delAllFile(folderPath);
	            if(dirDel){
	                String filePath = folderPath;
	                filePath = filePath.toString();
	                java.io.File myFilePath = new java.io.File(filePath);
	                myFilePath.delete();
	            }
	        } catch (Exception e) {
	            
	        }
	    }
	    
	    public static File saveBitmap2SD(Bitmap bitmap,String filePath)
	    {
	    	
	    	try {
	    	       FileOutputStream out = new FileOutputStream(filePath);
	    	       bitmap.compress(Bitmap.CompressFormat.PNG, 90, out);
	    	} catch (Exception e) {
	    	       e.printStackTrace();
	    	}
	    	File file = new File(filePath);
	    	return file;
	    }
	    
	    public static Bitmap readBitmapFromSD( ContentResolver resolver ,Uri uri)
	    {
	    	Bitmap bitmap = null;
	        BitmapFactory.Options options  = new BitmapFactory.Options();
            try{
	           InputStream is =  resolver.openInputStream(uri);
	           int times = 1;
	           int size = is.available();

	           while((size = size/2) > maxSize)
	        	   times *=2;
	           /*
	 		  ActivityManager activityManager = (ActivityManager) this .getSystemService(Activity.ACTIVITY_SERVICE);   
			  if(activityManager.getMemoryClass ()<=16)
			  {
			   times *= 2;
			  }
			  */
	           options.inSampleSize = times;
	           options.inJustDecodeBounds = false;
	           options.inPreferredConfig = Bitmap.Config.RGB_565;

	           bitmap = BitmapFactory.decodeStream(is,null,options);  
	           is.close();
	        	} catch (Exception e) { 
	            Log.e("error","" + e.getMessage()); 
	        } 
        	return bitmap;
	    }
	
}
