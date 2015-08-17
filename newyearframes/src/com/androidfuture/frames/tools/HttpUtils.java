package com.androidfuture.frames.tools;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;


import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONObject;

import com.androidfuture.tools.AFLog;




public class HttpUtils {
	private final static String TAG = "HTTPUtils";
	
	public static JSONObject request(String url)
	{
		JSONObject jsonObj = null;
		HttpClient client = new DefaultHttpClient();
	    StringBuilder builder = new StringBuilder();
	    HttpParams  httpParameters = new BasicHttpParams();// Set the timeout in milliseconds until a connection is established.  
	    HttpConnectionParams.setConnectionTimeout(httpParameters, 60000);// Set the default socket timeout (SO_TIMEOUT) // in milliseconds which is the timeout for waiting for data.  
	    HttpConnectionParams.setSoTimeout(httpParameters, 60000);  
	    client = new DefaultHttpClient(httpParameters);  
	    AFLog.d("begin to load: " + url);
	    HttpGet myget = new HttpGet(url);
	    try {
	        HttpResponse response = client.execute(myget);
	       int status = response.getStatusLine().getStatusCode(); 
	       
	        if(status == 200)
	        {
	        	//BufferedReader reader = new BufferedReader(inputStreamToReader(
	        	//		response.getEntity().getContent()));
	        	
	        	//to simply
	        	InputStreamReader inReader  = null;
	        	InputStream in = response.getEntity().getContent();
	        	int byte1 = in.read();
	     	    int byte2 = in.read();
	     	    //AFLog.d("byte:"+byte1 + "-" + byte2);

	     	    if (byte1 == 0xFF && byte2 == 0xFE) {
	     	    	inReader = new InputStreamReader(in, "UTF-16LE");
	     	    } else if (byte1 == 0xFF && byte2 == 0xFF) {
	     	    	inReader = new InputStreamReader(in, "UTF-16BE");
	     	    } else {
	     	      int byte3 = in.read();
	     	      if (byte1 == 0xEF && byte2 == 0xBB && byte3 == 0xBF) {
	     	    	 inReader = new InputStreamReader(in, "UTF-8");
	     	      } else {
	     	    	  builder.append((char)byte1);
	     	    	 builder.append((char)byte2);
	     	    	 builder.append((char)byte3);
	     	    	 inReader = new InputStreamReader(in, "UTF-8");
	     	      }
	     	    }
	        	
	        	 BufferedReader reader = new BufferedReader(inReader);
	        	//BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(),Charset.forName("UTF-8")));
	        	for (String s = reader.readLine(); s != null; s = reader.readLine()) {
	        		builder.append(s);
	        	}
	        	//AFLog.d(builder.toString());
	        	jsonObj = new JSONObject(builder.toString());
	        }
	    }catch(Exception e)
	    {
	    	AFLog.e("Exception:" + e);
	    	return null;
	    }
	    return jsonObj;
	 }
	

	public static Reader inputStreamToReader(InputStream in) throws IOException {
	    in.mark(4);
	    int byte1 = in.read();
	    int byte2 = in.read();
	    AFLog.d("byte:"+byte1 + "-" + byte2);

	    if (byte1 == 0xFF && byte2 == 0xFE) {
	      return new InputStreamReader(in, "UTF-16LE");
	    } else if (byte1 == 0xFF && byte2 == 0xFF) {
	      return new InputStreamReader(in, "UTF-16BE");
	    } else {
	      int byte3 = in.read();
	      if (byte1 == 0xEF && byte2 == 0xBB && byte3 == 0xBF) {
	        return new InputStreamReader(in, "UTF-8");
	      } else {
	    	AFLog.d("reset support:" + in.markSupported());
	        in.reset();
	        return new InputStreamReader(in, "UTF-8");
	      }
	    }
	  }
	    
}
