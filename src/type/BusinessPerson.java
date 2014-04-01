package type;

import java.util.ArrayList;

import operator.JsonOper;
import operator.Print;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.jayway.jsonpath.JsonPath;

public class BusinessPerson {
	private ArrayList<String[]> leadership = new ArrayList<String[]>();//(Organization, Role, Title, From, To)
	private ArrayList<String[]> boardMember = new ArrayList<String[]>();//(Organization, Role, Title, From, To)
	private ArrayList<String> founded = new ArrayList<String>(); //OrganizationName
	private String[] header = {"Organization", "Role", "Title", "From / To"};
	
	public BusinessPerson(JSONObject topic) {
		JsonOper jo = new JsonOper();
		
		this.founded = jo.getAttributes(topic, "/organization/organization_founder/organizations_founded", "text");
		
		//leadership
		if((((JSONObject) topic.get("property")).get("/business/board_member/leader_of")) != null){
			JSONArray leaderOf = (JSONArray) JsonPath.read(topic,"$.property['/business/board_member/leader_of'].values");
			for (Object l:leaderOf) {
				String[] tmp = new String[5];
				tmp[0] = jo.getAttribute((JSONObject)l, "/organization/leadership/organization", "text");
				tmp[1] = jo.getAttribute((JSONObject)l, "/organization/leadership/role", "text");				
				tmp[2] = jo.getAttribute((JSONObject)l, "/organization/leadership/title", "text");	
				tmp[3] = jo.getAttribute((JSONObject)l, "/organization/leadership/from", "text");					
				tmp[4] = jo.getAttribute((JSONObject)l, "/organization/leadership/to", "text");	
				
				this.leadership.add(tmp);
			}
		}
		
		//boardmember
		if((((JSONObject) topic.get("property")).get("/business/board_member/organization_board_memberships")) != null){
			JSONArray boardMemberOf = (JSONArray) JsonPath.read(topic,"$.property['/business/board_member/organization_board_memberships'].values");
			for (Object b:boardMemberOf) {
				String[] tmp = new String[5];
				tmp[0] = jo.getAttribute((JSONObject)b, "/organization/organization_board_membership/organization", "text");
				tmp[1] = jo.getAttribute((JSONObject)b, "/organization/organization_board_membership/role", "text");				
				tmp[2] = jo.getAttribute((JSONObject)b, "/organization/organization_board_membership/title", "text");	
				tmp[3] = jo.getAttribute((JSONObject)b, "/organization/organization_board_membership/from", "text");					
				tmp[4] = jo.getAttribute((JSONObject)b, "/organization/organization_board_membership/to", "text");	
									
				this.boardMember.add(tmp);
			}
		}
	}
	
	private void printTable(String[] header, ArrayList<String[]> b) {
		if (b.isEmpty())
			return;
		
		String columnName = "";
		if (b == this.leadership)
			columnName = "Leadership:";
		else
			columnName = "Board Member:";
		
		Print.printHyphen();
		System.out.printf("| %-18s| %-30s| %-20s| %-20s| %-24s|\n", columnName, header[0], header[1], header[2], header[3]);
		System.out.printf("| %-18s%-103s\n", "", Print.getHyphens(103));
		for (String[] content:b) {
			
			if (content[0].length() > 30)
				content[0] = content[0].substring(0, 27) + "...";
			if (content[1].length() > 20)
				content[1] = content[1].substring(0, 17) + "...";
			if (content[2].length() > 20)
				content[2] = content[2].substring(0, 17) + "...";
			
			String fromTo="";
			if (!content[3].isEmpty() && !content[4].isEmpty())
				fromTo = content[3] + " / " + content[4];
			else if (content[3].isEmpty() && content[4].isEmpty())
				fromTo = "";
			else if (!content[3].isEmpty())
				fromTo = content[3] + " / " + "now";
			else if (!content[4].isEmpty())
				fromTo = "N/A" + " / " + content[4];
			
			if (fromTo.length() > 22)
				fromTo = fromTo.substring(0, 19) + "...";
			System.out.printf("| %-18s| %-30s| %-20s| %-20s| %-24s|\n", "", content[0], content[1], content[2], fromTo);
		}
	}
	
	public void print() {
		Print.printLines("Founded:", this.founded);
		printTable(this.header, this.leadership);
		printTable(this.header, this.boardMember);
	}
}
