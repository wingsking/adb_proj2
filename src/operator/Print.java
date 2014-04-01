package operator;

import java.util.ArrayList;

public class Print {
	private static int hyphenLength = 123;
	
	public static String getHyphens(int num) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i<num; i++) {
			sb.append("-");
		}
		return sb.toString();
	}
	
	public static void printHyphen() {
		for (int i = 0; i < hyphenLength; i++)
			System.out.print("-");
		System.out.println();
	}
	
	public static void printHyphenByLength(int length) {
		
		for (int i = 0; i < length; i++)
			System.out.print("-");
		System.out.println();
	}
	
	public static void printFormat(String a, String b) {
		if (b == null || b.length() == 0)
			return;
		printHyphen();
		System.out.printf("| %-20s%-100s|\n", a, b.length()>100?(b.substring(0, 97)+"..."):b);
	}
	
	public static void printLines(String a, ArrayList<String> b) {
		if (b == null || b.isEmpty())
			return;
		printHyphen();
		for (int i=0;i<b.size();i++) {
			if (i==0)
				System.out.printf("| %-20s%-100s|\n", a, b.get(i).length()>100?(b.get(i).substring(0, 97)+"..."):b.get(i));
			else
				System.out.printf("| %-20s%-100s|\n", "", b.get(i).length()>100?(b.get(i).substring(0, 97)+"..."):b.get(i));
		}
	}
	
	public static void publicTable(String[] header, ArrayList<String[]> b) {

	}
	
	public static ArrayList<String> splitToLines(String s, int width) {
		String ss = s.replaceAll("\n", "");
		ArrayList<String> lines = new ArrayList<String>();
		int length = ss.length();
		for (int i = 0; i < length; i += width) {
			lines.add(ss.substring(i, Math.min(length, i+width)));
		}
		
		return lines;
	}

	private static void printSpaces(int num) {
		//StringBuilder sb = new StringBuilder();
		for (int i = 0; i < num; i++) {
			System.out.print(" ");
		}
		//return sb.toString();
	}
	
	public static void printMeta(String meta) {
		printHyphen();
		System.out.print("|");
		int padding = (hyphenLength - meta.length())/2;
		printSpaces(padding);
		System.out.print(meta);
		printSpaces(hyphenLength - meta.length() - padding - 2);
		System.out.print("|\n");
	}

}
