package type;

import java.util.ArrayList;

import operator.JsonOper;
import operator.Print;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.jayway.jsonpath.JsonPath;

public class League {
	private String name;
	private String sport;
	private String slogan;
	private String website;
	private String championship;
	private ArrayList<String> teams = new ArrayList<String>();
	private String description;
	
	public League(JSONObject topic) {
		JsonOper jo = new JsonOper();
		this.name = jo.getAttribute(topic, "/type/object/name", "value");
		this.sport = jo.getAttribute(topic, "/sports/sports_league/sport", "text");
		this.slogan = jo.getAttribute(topic, "/organization/organization/slogan", "value");
		this.website = jo.getAttribute(topic, "/common/topic/official_website", "value");
		this.championship = jo.getAttribute(topic, "/sports/sports_league/championship", "text");
		this.description = jo.getAttribute(topic, "/common/topic/description", "value");

		if ((((JSONObject) topic.get("property")).get("/sports/sports_league/teams")) != null){
			JSONArray teamsArray = (JSONArray) JsonPath.read(topic, "$.property['/sports/sports_league/teams'].values");
			for (Object t:teamsArray) {
				this.teams.add(JsonPath.read(t, "$.property['/sports/sports_league_participation/team'].values[0].text").toString());
			}
		}
	}
	
	public void print() {
		Print.printFormat("Name:", this.name);
		Print.printFormat("Sport:", this.sport);
		Print.printFormat("Slogan:", this.slogan);
		Print.printFormat("Official Website:", this.website);
		Print.printFormat("Championship:", this.championship);		
		Print.printLines("Teams:", this.teams);
		Print.printLines("Description:", Print.splitToLines(this.description, 100));
	}
}
