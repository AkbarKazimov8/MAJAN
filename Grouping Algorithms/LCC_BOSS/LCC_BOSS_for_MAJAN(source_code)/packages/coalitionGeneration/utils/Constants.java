package coalitionGeneration.utils;

import java.util.ArrayList;
import java.util.List;

public class Constants {

	private static List<String> nationalities =new ArrayList<>();
	{
		nationalities.add("A");
		nationalities.add("B");
		nationalities.add("C");
		nationalities.add("D");
		nationalities.add("E");
		nationalities.add("F");
		nationalities.add("G");
		nationalities.add("H");
	}		
	private static void addNationalitiy(String nationality) {
		nationalities.add(nationality);	
	}
	
	public static int getNationalityIndex(String nationality) {
		if(!nationalities.contains(nationality)) {
			addNationalitiy(nationality);
		}
		return nationalities.indexOf(nationality);
	}
}
