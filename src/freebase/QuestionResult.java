package freebase;

import java.util.ArrayList;
import java.util.Comparator;

import operator.Print;

public class QuestionResult {
	private String name;
	private String entity;
	private ArrayList<String> created = new ArrayList<String>();
	
	public String getName() { return this.name;}
	public void setName(String name) {this.name = name;};
	
	public String getEntityType() { return this.entity;}
	public void setEntityType(String entity) {this.entity = entity;}	
	
	public ArrayList<String> getCreated() { return this.created;}
	public void setCreated(ArrayList<String> created) {this.created = created;}
	
	//this is for command line output
	public String printString() {
		StringBuilder sb = new StringBuilder();
		if (this.created.size() > 1) {
			for (int i=0;i<this.created.size()-1;i++) {
				sb.append("<" + this.created.get(i) + ">, ");
			}
			sb.append("and <" + this.created.get(this.created.size()-1) + ">");
		}else
			sb.append("<" + this.created.get(0) + ">");
			
		return this.name + " (as " + this.entity + ") created " + sb.toString();
	}

	
	public static Comparator<QuestionResult> sortByName = new Comparator<QuestionResult>() {
        public int compare(QuestionResult o1, QuestionResult o2) {
            return o1.name.compareTo(o2.name);
        }
    };
    
    //print as infobox-like format
	public void print() {
		Print.printHyphen();
		System.out.printf("| %-35s| %-31s| %-49s |\n", this.name + ":", "As", "Creation");
		System.out.printf("| %-35s%-84s |\n", "", Print.getHyphens(84));
		
		for (int i=0;i<this.created.size();i++) {
			String content = this.created.get(i);
			if (content.length() > 49)
				content = content.substring(0, 46) + "...";
			if (i == 0)
				System.out.printf("| %-35s| %-31s| %-49s |\n", "", this.entity, content);
			else
				System.out.printf("| %-35s| %-31s| %-49s |\n", "", "", content);
		}
	}

}
