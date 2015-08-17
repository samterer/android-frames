package com.androidfuture.network;
import java.util.ArrayList;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;
abstract public class AFXMLParser extends DefaultHandler implements AFParser{
		   
	protected ArrayList<AFData> objects = null;
		   protected AFData currentObject;
		   protected int totalNum;
		   protected String tagName;
		   public ArrayList<AFData> getObjects() {
			   return objects;
		   }
		  
		   @Override 
		   public void startDocument() throws SAXException {
			   objects = new ArrayList<AFData>();
		   }

		   @Override
		   abstract public void characters(char[] ch, int start, int length);
		  
		   @Override 
		   abstract public void startElement(String namespaceURI, String localName, String qName, Attributes atts);
		   abstract public void endElement(String uri, String localName, String name);

		public int getTotalNum() {
			// TODO Auto-generated method stub
			return totalNum;
		}
}
