package type;

import java.util.ArrayList;

import operator.JsonOper;
import operator.Print;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.jayway.jsonpath.JsonPath;

public class Person {
	private String name;
	private String birthday;
	private String placeOfbirth;
	private String death; //(Date at Place, Cause:)
	private ArrayList<String> siblings = new ArrayList<String>();
	private ArrayList<String> spouses = new ArrayList<String>();
	private String description;
	//private JSONObject topic;
	
	public Person (JSONObject topic) {
		JsonOper jo = new JsonOper();
		
		this.name = jo.getAttribute(topic, "/type/object/name", "value");
		this.birthday = jo.getAttribute(topic, "/people/person/date_of_birth", "value");
		this.placeOfbirth = jo.getAttribute(topic, "/people/person/place_of_birth", "text");
		
		// death
		String deathDate = jo.getAttribute(topic, "/people/deceased_person/date_of_death", "value");
		String deathPlace = jo.getAttribute(topic, "/people/deceased_person/place_of_death", "text");
		String deathCause= jo.getAttribute(topic, "/people/deceased_person/cause_of_death", "text");
		if (deathDate.isEmpty() && deathPlace.isEmpty() && deathCause.isEmpty())
			this.death = "";
		else
			this.death = deathDate + " at " + deathPlace + ", cause: (" + deathCause +")";
		
		//siblings
		if((((JSONObject) topic.get("property")).get("/people/person/sibling_s")) != null){
			JSONArray sibs = (JSONArray) JsonPath.read(topic,"$.property['/people/person/sibling_s'].values");
			for (Object s:sibs) {
				this.siblings.add(JsonPath.read(s,"$.property['/people/sibling_relationship/sibling'].values[0].text").toString());
			}
		}

		//spouses
		if((((JSONObject) topic.get("property")).get("/people/person/spouse_s")) != null){
			JSONArray spousesArray = (JSONArray) JsonPath.read(topic,"$.property['/people/person/spouse_s'].values");
			for (Object s:spousesArray) {
				String spouseName = JsonPath.read(s,"$.property['/people/marriage/spouse'].values[0].text").toString();
				
				String spouseFrom = "";
				String spouseTo = "";
				if ((((JSONObject) ((JSONObject) s).get("property")).get("/people/marriage/from")) != null){
					JSONArray spouseFromObject = (JSONArray) JsonPath.read(s,"$.property['/people/marriage/from'].values");
					if (!spouseFromObject.isEmpty()) {
						spouseFrom = JsonPath.read(s,"$.property['/people/marriage/from'].values[0].text").toString();
						if ((((JSONObject) ((JSONObject) s).get("property")).get("/people/marriage/to")) != null){
							JSONArray spouseToObject = (JSONArray) JsonPath.read(s,"$.property['/people/marriage/to'].values");
							if (!spouseToObject.isEmpty())
								spouseTo = JsonPath.read(s,"$.property['/people/marriage/to'].values[0].text").toString();
							else
								spouseTo = "now";
						}
					}
				} 
				
				String ceremonyLoc = "";
				if ((((JSONObject) ((JSONObject) s).get("property")).get("/people/marriage/location_of_ceremony")) != null){
					JSONArray locObject = (JSONArray) JsonPath.read(s,"$.property['/people/marriage/location_of_ceremony'].values");
					ceremonyLoc = !locObject.isEmpty()?
							JsonPath.read(s,"$.property['/people/marriage/location_of_ceremony'].values[0].text").toString():"";
				}

				String spouse = spouseName + " (" + spouseFrom + " - " + spouseTo + ") @ " + ceremonyLoc;
				this.spouses.add(spouse);
			}
		}
		
		this.description = jo.getAttribute(topic, "/common/topic/description", "value");
	}
	
	public void print() {

		Print.printFormat("Name:", this.name);
		Print.printFormat("Birthday:", this.birthday);
		Print.printFormat("Place of birth:", this.placeOfbirth);
		
		Print.printFormat("Death:", this.death);
		
		Print.printLines("Description:", Print.splitToLines(this.description, 100));
		
		Print.printLines("Siblings:", this.siblings);
		Print.printLines("Spouses:", this.spouses);

	}
	
}
