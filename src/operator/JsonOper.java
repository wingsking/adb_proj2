package operator;

import java.util.ArrayList;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.PathNotFoundException;

public class JsonOper {
	public ArrayList<String> getAttributes(JSONObject topic, String attribute, String dataType) {
		ArrayList<String> tmp = new ArrayList<String>();
		
		try {	
			JSONObject p = (JSONObject) topic.get("property");
			JSONObject attr = (JSONObject) p.get(attribute);
			JSONArray values = (JSONArray) attr.get("values");
			for (Object v:values) {
				tmp.add(JsonPath.read(v, "$."+dataType).toString());
			}
		} catch (PathNotFoundException ex){
			return null;
		} catch (NullPointerException ex) {
			return null;
		}
		return tmp;
	}
	
	public String getAttribute(JSONObject topic, String attribute, String dataType) {
		String tmp = "";
		
		try {	
			JSONObject p = (JSONObject) topic.get("property");
			JSONObject attr = (JSONObject) p.get(attribute);
			JSONArray values = (JSONArray) attr.get("values");
			for (Object v:values) {
				tmp = JsonPath.read(v, "$." + dataType).toString();
			}
		} catch (PathNotFoundException ex){
			return "";
		} catch (NullPointerException ex) {
			return "";
		}
		return tmp;
	}
	
	public JSONArray getArray(JSONObject topic, String attribute) {
		JSONArray values = new JSONArray();
		try {	
			JSONObject p = (JSONObject) topic.get("property");
			JSONObject attr = (JSONObject) p.get(attribute);
			values = (JSONArray) attr.get("values");
		} catch (PathNotFoundException ex){
			return null;
		} catch (NullPointerException ex) {
			return null;
		}
		return values;
	}
}
