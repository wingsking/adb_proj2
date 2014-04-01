package freebase;

import java.io.IOException;

import org.json.simple.parser.ParseException;
public class proj2 {

	public static void main(String[] args) throws IOException, ParseException {
		
		 String query = "who created zocdoc";
		 //Infobox box = new Infobox(query);
		 //box.printAll();
		 Question q = new Question(query);
		 q.printInfobox();
	 }
}
