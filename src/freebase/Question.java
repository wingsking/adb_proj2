package freebase;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

import operator.Print;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.jayway.jsonpath.JsonPath;

public class Question {
	private static final String API_KEY = "AIzaSyBR42GcexpI2tIyn1WkZ4Ctp-kY61JUcA4";
	private ArrayList<QuestionResult> results = new ArrayList<QuestionResult>();
	private String object;
	
	public Question(String query) throws IOException, ParseException {
		String object = genObject(query);
		//if object cannot be found (not valid input), return
		if (object == null)
			return;
		this.object = object;
		freebaseMQLRead(this.object);
	}
	
	//get the object from query
	private String genObject (String query) {
		query = query.toLowerCase();
		if (!query.startsWith("who created ")){
			System.out.print("Wrong question!!!");
			return null;
		}
		return query.substring(11).trim();
	}
	
	private void freebaseMQLRead(String object) throws IOException, ParseException {
		this.results.addAll(getMQLResults(object, "BusinessPerson"));
		this.results.addAll(getMQLResults(object, "Author"));
		
		Collections.sort(this.results, QuestionResult.sortByName);
	}
	
	//normal print in command line
	public void print() {
		if (this.results.isEmpty()) {
			if (object == null)
				return;
			System.out.println("It seems no one created [" + this.object + "]!!!");
			return;
		}
			
		for (int i=0;i<this.results.size();i++) {
			System.out.println(i+1 + ". " + this.results.get(i).printString());
		}
	}
	
	//print in infobox format
	public void printInfobox() {
		if (this.object == null)
			return;
		String title = "Who created " + this.object + "?";
		Print.printMeta(title);
		
		for (QuestionResult qr:this.results) {
			qr.print();
		}
		
		Print.printHyphen();
		
	}
	
	//get results from MQL and store them as ArrayList
	private ArrayList<QuestionResult> getMQLResults(String object, String entity) throws IOException, ParseException {
		  String path ="";
		  if (entity.equals("BusinessPerson"))
			  path = "$./organization/organization_founder/organizations_founded";
		  else if (entity.equalsIgnoreCase("Author"))
			  path = "$./book/author/works_written";
		  else
			  return new ArrayList<QuestionResult>();
		  
		  String query = constructQuery(object, entity);
		  
		  HttpTransport httpTransport = new NetHttpTransport();
		  HttpRequestFactory requestFactory = httpTransport.createRequestFactory();
		  JSONParser parser = new JSONParser();
		  GenericUrl url = new GenericUrl("https://www.googleapis.com/freebase/v1/mqlread");
		  url.put("query", query);
		  url.put("key", API_KEY);
		  HttpRequest request = requestFactory.buildGetRequest(url);
		  HttpResponse httpResponse = request.execute();
		  JSONObject response = (JSONObject)parser.parse(httpResponse.parseAsString());
		  JSONArray results = (JSONArray)response.get("result");
		  
		  ArrayList<QuestionResult> QR = new ArrayList<QuestionResult>();
		  for (Object result : results) {
			  QuestionResult qr = new QuestionResult();
			  qr.setName(JsonPath.read(result,"$.name").toString());
			  qr.setEntityType(entity);
			  JSONArray rets = (JSONArray) JsonPath.read(result, path);
			  ArrayList<String> created = new ArrayList<String>();
			  for (Object o : rets) {
				  created.add(JsonPath.read(o,"$.name").toString());
			  }
			  qr.getCreated().addAll(created);
			  QR.add(qr);
		  }
		  return QR;
	}
	
	//construct the query than can be used to do MQL search
	private String constructQuery(String object, String entity) {
		String type = "";
		String subtype = "";
		if (entity.equals("BusinessPerson")) {
			type = "/organization/organization_founder";
			subtype = "organizations_founded";
		}
		else if (entity.equals("Author")) {
			type = "/book/author";
			subtype = "works_written";
		}
		else 
			return null;
		
		String query = "[{"
						+ "\"type\":\"" + type + "\","
						+ "\"name\":null,"
						+ "\"" + type + "/" + subtype + "\":"
	  					+ "[{"
	  						+ "\"name\":null,"
	  						+ "\"name~=\": \"" + object + "\""
	  					+ "}]"
	  				+ "}]";
		
		return query;
	}
}
