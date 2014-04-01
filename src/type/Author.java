package type;

import java.util.ArrayList;

import operator.JsonOper;
import operator.Print;

import org.json.simple.JSONObject;



public class Author {
	private ArrayList<String> books = new ArrayList<String>(); //title
	private ArrayList<String> booksAbout = new ArrayList<String>(); //books about the author
	private ArrayList<String> influenced = new ArrayList<String>();
	private ArrayList<String> influencedBy = new ArrayList<String>();
	
	public Author (JSONObject topic) {
		JsonOper jo = new JsonOper();

		this.books = jo.getAttributes(topic, "/book/author/works_written", "text");
		this.booksAbout = jo.getAttributes(topic, "/book/book_subject/works", "text");
		this.influenced = jo.getAttributes(topic, "/influence/influence_node/influenced", "text");
		this.influencedBy = jo.getAttributes(topic, "/influence/influence_node/influenced_by", "text");
	}
	
	public void print() {
		Print.printLines("Books:", this.books);
		Print.printLines("Books about:", this.booksAbout);
		Print.printLines("Influenced:", this.influenced);
		Print.printLines("InfluencedBy", this.influencedBy);
	}
}
