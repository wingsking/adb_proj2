package freebase;

import java.io.IOException;
import java.util.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Scanner;

import org.json.simple.parser.ParseException;


public class proj2 {
	public static String API_KEY;	//	
	
	public static void main(String[] args) throws IOException, ParseException {
		API_KEY = args[0]; 
		
		//if args includes more than just API_KEY
		if (args.length > 2) {
			String query = args[1]; //"who created microsoft";
			System.out.println("Query-Question: " + query);
			String method = args[2];
			
			if (method.equals("infobox")) {
				Infobox box = new Infobox(query);
				box.printAll();
			} else if (method.equals("question")) {
				Question q = new Question(query);
				q.printInfobox();
			}
		} else {
			System.out.println("Welcome to infobox creator using Freebase knowledge graph.\n"
							+ "Feel curious? Start exploring...\n\n" );
			Scanner scan = new Scanner(System.in);
			while (true) {
				DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
				System.out.print("[" + dateFormat.format(new Date())
								+ "] " + System.getProperty("user.name") + "@fb_ibox> ");
				String query = scan.nextLine();
				if (query.isEmpty())
					continue;
				if (query.toLowerCase().startsWith("who created")) {
					Question q = new Question(query.substring(11).trim(), true);
					q.printInfobox();
				} else {
					Infobox box = new Infobox(query);
					box.printAll();
				}
			}
		}
	 }
}
