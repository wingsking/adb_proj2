package type;

import java.util.ArrayList;

import operator.JsonOper;
import operator.Print;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.jayway.jsonpath.JsonPath;

public class SportsTeam {
	private ArrayList<String> name = new ArrayList<String>();
	private ArrayList<String> sport = new ArrayList<String>();
	private ArrayList<String> arena = new ArrayList<String>();
	private ArrayList<String> championships = new ArrayList<String>();
	private String founded;
	private String league;
	private ArrayList<String> location = new ArrayList<String>();
	private ArrayList<String[]> coaches = new ArrayList<String[]>(); //Coaches(Name, Position, From/To)
	private String[] header1 = {"Name", "Position", "From / To"};
	private ArrayList<String[]> playersRoster = new ArrayList<String[]>(); //playersRoster(Name, Position, Number, From/To)
	private String[] header2 = {"Name", "Position", "Number", "From / To"};
	private String description;
	
	public SportsTeam(JSONObject topic) {
		JsonOper jo = new JsonOper();
		this.name = jo.getAttributes(topic, "/type/object/name", "value");
		this.sport = jo.getAttributes(topic, "/sports/sports_team/sport", "text");
		this.arena = jo.getAttributes(topic, "/sports/sports_team/arena_stadium", "text");
		this.championships = jo.getAttributes(topic, "/sports/sports_team/championships", "text");
		this.founded = jo.getAttribute(topic, "/sports/sports_team/founded", "text");
		
		this.league = (((JSONObject) topic.get("property")).get("/sports/sports_team/league")) != null?
				JsonPath.read(topic, "$.property['/sports/sports_team/league'].values[0].property['/sports/sports_league_participation/league'].values[0].text").toString():"";
		
		this.location = jo.getAttributes(topic, "/sports/sports_team/location", "text");
			
		//coaches
		if ((JSONObject) ((JSONObject) topic.get("property")).get("/sports/sports_team/coaches") != null) {
			JSONArray coachesArray = (JSONArray) JsonPath.read(topic, "$.property['/sports/sports_team/coaches'].values");
			for (Object c:coachesArray) {
				String[] tmp = new String[4];
				tmp[0] = jo.getAttribute((JSONObject)c, "/sports/sports_team_coach_tenure/coach", "text");
				
				tmp[1] = "N/A";
				if ((((JSONObject) ((JSONObject) c).get("property")).get("/sports/sports_team_coach_tenure/position")) != null) {
					JSONArray toArray= (JSONArray)JsonPath.read(c, "$.property['/sports/sports_team_coach_tenure/position'].values");
					tmp[1] = !toArray.isEmpty()?
						JsonPath.read(c, "$.property['/sports/sports_team_coach_tenure/position'].values[0].text").toString():"";
				}	
				
				tmp[2] = "N/A";
				if ((((JSONObject) ((JSONObject) c).get("property")).get("/sports/sports_team_coach_tenure/from")) != null) {
					JSONArray toArray= (JSONArray)JsonPath.read(c, "$.property['/sports/sports_team_coach_tenure/from'].values");
					tmp[2] = !toArray.isEmpty()?
						JsonPath.read(c, "$.property['/sports/sports_team_coach_tenure/from'].values[0].text").toString():"N/A";
				}
				
				//should be "now" if not found
				tmp[3] = "now";
				if ((((JSONObject) ((JSONObject) c).get("property")).get("/sports/sports_team_coach_tenure/to")) != null) {
					JSONArray toArray= (JSONArray)JsonPath.read(c, "$.property['/sports/sports_team_coach_tenure/to'].values");
					tmp[3] = !toArray.isEmpty()?
						JsonPath.read(c, "$.property['/sports/sports_team_coach_tenure/to'].values[0].text").toString():"now";
				}
				
				this.coaches.add(tmp);
			}
		}
		
		//playersRoster
		if ((JSONObject) ((JSONObject) topic.get("property")).get("/sports/sports_team/roster") != null) {
			JSONArray playersArray = (JSONArray) JsonPath.read(topic, "$.property['/sports/sports_team/roster'].values");
			for (Object p:playersArray) {
				String[] tmp = new String[5];
				tmp[0] = jo.getAttribute((JSONObject)p, "/sports/sports_team_roster/player", "text");
				
				//positions
				tmp[1] = "";
				if ((((JSONObject) ((JSONObject) p).get("property")).get("/sports/sports_team_roster/position")) != null) {
					JSONArray positions = JsonPath.read(p, "$.property['/sports/sports_team_roster/position'].values");
					for (Object pos:positions) {
						tmp[1] += JsonPath.read(pos, "$.text").toString() + ",";
					}
					if(tmp[1].length()>0)
						tmp[1] = tmp[1].substring(0, tmp[1].length()-1);
				}
				tmp[2] = "";
				if ((((JSONObject) ((JSONObject) p).get("property")).get("/sports/sports_team_roster/number")) != null) {
					JSONArray positions = JsonPath.read(p, "$.property['/sports/sports_team_roster/number'].values");
					for (Object pos:positions) {
						tmp[2] += JsonPath.read(pos, "$.text").toString() + ",";
					}
					if (tmp[2].length()>0)
						tmp[2] = tmp[2].substring(0, tmp[2].length()-1);
				}
				
				tmp[3] = jo.getAttribute((JSONObject)p, "/sports/sports_team_roster/from", "value");
				tmp[4] = jo.getAttribute((JSONObject)p, "/sports/sports_team_roster/to", "value");
				this.playersRoster.add(tmp);
			}
		}
		
		this.description = JsonPath.read(topic, "$.property['/common/topic/description'].values[0].value").toString();
	}
	
	public void print() {
		Print.printLines("Name:", this.name);
		Print.printLines("Sport:", this.sport);
		Print.printLines("Arena:", this.arena);
		Print.printLines("Championships:", this.championships);
		Print.printFormat("Founded:", this.founded);
		Print.printFormat("Leagues:", this.league);
		Print.printLines("Location:", this.location);
		
		printTable(header1, this.coaches);
		printTable(header2, this.playersRoster);
		
		Print.printLines("Description:", Print.splitToLines(this.description, 100));
	}
	
	private void printTable(String[] header, ArrayList<String[]> b) {
		if (b.isEmpty())
			return;
		Print.printHyphen();
		if (b == this.coaches) {
			System.out.printf("| %-18s| %-30s| %-30s| %-36s|\n", "Coaches:", header[0], header[1], header[2]);
			System.out.printf("| %-18s%-103s\n", "", Print.getHyphens(103));
			for (String[] content:b) {
				if (content[0].length() > 30)
					content[0] = content[0].substring(0, 27) + "...";
				if (content[1].length() > 30)
					content[1] = content[1].substring(0, 27) + "...";
				String fromTo = content[2] + " / " + content[3];
				if (fromTo.length() > 36)
					fromTo = fromTo.substring(0, 33) + "...";
				
				System.out.printf("| %-18s| %-30s| %-30s| %-36s|\n", "", content[0], content[1], fromTo);
			}
		} else if ( b == this.playersRoster) {		
		
			System.out.printf("| %-18s| %-30s| %-20s| %-20s| %-24s|\n", "PlayersRoster:", header[0], header[1], header[2], header[3]);
			System.out.printf("| %-18s%-103s\n", "", Print.getHyphens(103));
			for (String[] content:b) {
				
				if (content[0].length() > 30)
					content[0] = content[0].substring(0, 27) + "...";
				if (content[1].length() > 20)
					content[1] = content[1].substring(0, 17) + "...";
				if (content[2].length() > 20)
					content[2] = content[2].substring(0, 17) + "...";
				String fromTo = content[3] + " / " + content[4];
				if (fromTo.length() > 22)
					fromTo = fromTo.substring(0, 19) + "...";
				System.out.printf("| %-18s| %-30s| %-20s| %-20s| %-24s|\n", "", content[0], content[1], content[2], fromTo);
			}
		}
	}
	
}
