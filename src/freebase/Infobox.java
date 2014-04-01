package freebase;

import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.jayway.jsonpath.JsonPath;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import operator.Print;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import type.*;

public class Infobox {
	  private static final String API_KEY = proj2.API_KEY;//"AIzaSyBR42GcexpI2tIyn1WkZ4Ctp-kY61JUcA4";
	  private HashMap<String, Boolean> entityTypes = new HashMap<String, Boolean>();
	  private JSONObject topic;
	  private String meta;
	  private String query;
	  
	  public Infobox(String query) throws IOException, ParseException {
		  this.query = query;
		  JSONArray results = freebaseSearch(query);
		  freebaseTopic(results);
	  }
	  
	  // return an Array of matching objects from freesearch database
	  private JSONArray freebaseSearch(String query) throws IOException, ParseException {
		  HttpTransport httpTransport = new NetHttpTransport();
		  HttpRequestFactory requestFactory = httpTransport.createRequestFactory();
		  JSONParser parser = new JSONParser();
		  GenericUrl url = new GenericUrl("https://www.googleapis.com/freebase/v1/search");
		  url.put("query", query);
		  url.put("key", API_KEY);
		  HttpRequest request = requestFactory.buildGetRequest(url);
		  HttpResponse httpResponse = request.execute();
		  JSONObject response = (JSONObject)parser.parse(httpResponse.parseAsString());
		  JSONArray results = (JSONArray)response.get("result");
		  
		  return results;
	  }
	  
	  //get the top-rank entity that matches at least one of six high-level type
	  private void freebaseTopic(JSONArray results) throws IOException, ParseException {
		  HttpTransport httpTransport = new NetHttpTransport();
		  HttpRequestFactory requestFactory = httpTransport.createRequestFactory();
		  JSONParser parser = new JSONParser();
		  
		  int count = 0; //counting how many topics are searched
		  for (Object result:results) { 
			  String topicId = JsonPath.read(result, "$.mid").toString();
			  GenericUrl url = new GenericUrl("https://www.googleapis.com/freebase/v1/topic" + topicId);
			  url.put("key", API_KEY);
			  HttpRequest request = requestFactory.buildGetRequest(url);
			  HttpResponse httpResponse = request.execute();
			  
			  JSONObject topic = (JSONObject)parser.parse(httpResponse.parseAsString());
			  
			  HashMap<String, Boolean> entityTypes = mapType(topic);
			  if (entityTypes.size() > 0) {
				  this.topic = topic;
				  this.meta = JsonPath.read(result, "$.name").toString();
				  this.entityTypes = entityTypes;
				  break;
			  }	  
			  
			  if (this.topic == null) {
				  count++;
				  if (count % 5 == 0)
					  System.out.println(count + " Search API result entries are searched. None of them of a supported type.");
			  }
		  }
	  }
	  
	  //this maps freebase types to six high level type
	  private HashMap<String, Boolean> mapType(JSONObject topic) {
		  HashMap<String, Boolean> hm = new HashMap<String, Boolean>();
		  
		  //System.out.println(topic.toString());
		  JSONObject type = (JSONObject) JsonPath.read(topic,"$.property['/type/object/type']");
		  //topic.get("/type/object/type");
		  //System.out.println(type.toString());
		  JSONArray typeValues = (JSONArray) type.get("values");
		  for (Object t: typeValues) {
			  String typeId = JsonPath.read(t,"$.id").toString();
			  if (typeId.equals("/sports/sports_team") || typeId.equals("/sports/professional_sports_team"))
					 hm.put("SportsTeam", true);
			  if (typeId.equals("/sports/sports_league"))
					 hm.put("League", true);
			  if (typeId.equals("/organization/organization_founder") || typeId.equals("/business/board_member"))
					 hm.put("BusinessPerson", true);
			  if (typeId.equals("/film/actor") || typeId.equals("/tv/tv_actor"))if (!hm.containsKey("Actor"))
					 hm.put("Actor", true);
			  if (typeId.equals("/book/author"))
					 hm.put("Author", true);
			  if (typeId.equals("/people/person"))
					 hm.put("Person", true);
		  }
		  
		  return hm;
	  }
	  
	  //print all matching types
	  public void printAll() {
		  if (this.topic == null) {
			  System.out.println("No related information about query [" + query + "] was found!");
			  return;
		  }
		  
		  StringBuilder sb = new StringBuilder();
		  sb.append(" (");
		  for (Map.Entry<String, Boolean> entry:this.entityTypes.entrySet()) {
			  if (entry.getKey().equals("Person"))
				  continue;
			  sb.append(entry.getKey() + ",");
		  }
		  if (sb.length() > 2)
			  meta += sb.substring(0, sb.length()-1) + ")";
		  
		  Print.printMeta(this.meta);
		  
		  if (this.entityTypes.containsKey("League"))
			  print("League");
		  if (this.entityTypes.containsKey("SportsTeam"))
			  print("SportsTeam");
		  if (this.entityTypes.containsKey("Person"))
			  print("Person");
		  if (this.entityTypes.containsKey("Author"))
			  print("Author");
		  if (this.entityTypes.containsKey("Actor"))
			  print("Actor");
		  if (this.entityTypes.containsKey("BusinessPerson"))
			  print("BusinessPerson");
		  
		  Print.printHyphen();
		
	  }
	  
	  //print individual type
	  private void print(String type){
		  if (type.equals("Person")){
			  Person p = new Person(this.topic);
			  p.print();
		  } else if (type.equals("BusinessPerson")){
			  BusinessPerson b = new BusinessPerson(this.topic);
			  b.print();
		  } else if (type.equals("Author")){
			  Author au = new Author(this.topic);
			  au.print();
		  } else if (type.equals("Actor")){
			  Actor ac = new Actor(this.topic);
			  ac.print();
		  } else if (type.equals("League")){
			  League l = new League(this.topic);
			  l.print();
		  } else if (type.equals("SportsTeam")){
			  SportsTeam s = new SportsTeam(this.topic);
			  s.print();
		  }
	  }
}
