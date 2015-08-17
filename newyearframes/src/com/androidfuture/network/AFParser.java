package com.androidfuture.network;
import java.util.ArrayList;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;
public interface AFParser{
		   
	
		public ArrayList<AFData> getObjects();
		
		public int getTotalNum();
}
