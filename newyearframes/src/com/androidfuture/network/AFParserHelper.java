package com.androidfuture.network;


import java.io.StringReader;
import java.util.ArrayList;


import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.json.JSONObject;
import org.xml.sax.InputSource;





public class AFParserHelper {
	public static ArrayList<AFData> readData(String str, AFParser parser)
	{
		if(parser instanceof AFXMLParser)
		{
			return readData(str, (AFXMLParser)parser);
		}else if(parser instanceof AFJSONParser)
		{
			return readData(str, (AFJSONParser)parser);
		}
		
		return null;
	}
	private static ArrayList<AFData> readData(String str,AFXMLParser parser) {
		   try {
			   InputSource is = new InputSource( new StringReader( str ) );
			   SAXParserFactory spf = SAXParserFactory.newInstance();
			   SAXParser saxParser = spf.newSAXParser();
			   //saxParser.setProperty("http://xml.org/sax/features/namespaces",true);
			   saxParser.parse(is, parser);
			   
			   return parser.getObjects();
		   } catch (Exception e) {
			   e.printStackTrace();
		   }
		  return null;
		}
		
	private static ArrayList<AFData> readData(String str,AFJSONParser parser) {
		   try {
			  return parser.parser( new JSONObject(str));
		   } catch (Exception e) {
			   e.printStackTrace();
		   }
		  return null;
		}
}
