package type;

import java.util.ArrayList;

import operator.JsonOper;
import operator.Print;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.jayway.jsonpath.JsonPath;

public class Actor {
	private ArrayList<String[]> filmsParticipated = new ArrayList<String[]>();//(Character, Film Name)
	private String[] header = {"Character", "Film Name"};
	
	public Actor(JSONObject topic) {
		JsonOper jo = new JsonOper();
		if (((JSONObject)topic.get("property")).get("/film/actor/film") != null){
			JSONArray films = (JSONArray) JsonPath.read(topic,"$.property['/film/actor/film'].values");
			for (Object f:films) {
				String[] tmp = new String[2];
				tmp[0] = jo.getAttribute((JSONObject)f, "/film/performance/character", "text");
				tmp[1] = jo.getAttribute((JSONObject)f, "/film/performance/film", "text");
				this.filmsParticipated.add(tmp);
			}
		}
	}
	
	private void printTable(String[] header, ArrayList<String[]> b) {
		if (b == null || b.isEmpty())
			return;
		
		Print.printHyphen();
		System.out.printf("| %-18s| %-48s| %-49s |\n", "Films:", header[0], header[1]);
		System.out.printf("| %-18s%-101s |\n", "", Print.getHyphens(101));
		for (String[] content:b) {
			if (content[0].length() > 48)
				content[0] = content[0].substring(0, 45) + "...";
			if (content[1].length() > 49)
				content[1] = content[1].substring(0, 46) + "...";
			System.out.printf("| %-18s| %-48s| %-49s |\n", "", content[0], content[1]);
		}
	}
	
	
	public void print() {
		printTable(this.header, this.filmsParticipated);
	}
}
