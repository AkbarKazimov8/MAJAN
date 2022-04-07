package chc.problem;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.IntStream;

public class Utils {

	public static final String dateFormat1 = "d/M/yyyy";
	public static final String dateFormat2 = "MMMM d, yyyy";
	
	public static  final List<String> genderList = new ArrayList<String>(Arrays.asList("female", "male", "other"));
	public static  final List<String> familyList = new ArrayList<String>(Arrays.asList("single_man", "single_woman",
			"nuclear", "single_parent_mother", "single_parent_father", "extended"));
	public static  final List<String> nationalityList = new ArrayList<String>(Arrays.asList("nat1", "nat2",
			"nat3", "nat4", "nat5", "nat6", "nat7"));
	public static  final List<String> religionList = new ArrayList<String>(Arrays.asList("rel1", "rel2",
			"rel3", "rel4", "rel5", "rel6", "rel7"));
	public static  final List<String> ethnicList = new ArrayList<String>(Arrays.asList("eth1", "eth2",
			"eth3", "eth4", "eth5", "eth6", "eth7"));
	
	public static  final List<String> agePrefList = new ArrayList<String>(Arrays.asList("dont mind", "18-25", "26-33",
			"34-43", "44-50", "51-65", "65-120", "44-50/51-65", "51-65/65-120", "18-25/26-33/34-43",
			"18-25/26-33"));
	public static  final List<String> genderPrefList = new ArrayList<String>(Arrays.asList("dont mind", "female", "male", "other"));
	public static  final List<String> familyPrefList = new ArrayList<String>(Arrays.asList("dont mind", "single_man", "single_woman",
			"nuclear", "single_parent_mother", "single_parent_father", "extended", "single_parent_mother/single_parent_father/extended", 
			"single_woman/single_parent_mother", "single_parent_father/single_man", "extended/nuclear"));
	public static  final List<String> nationPrefList = new ArrayList<String>(Arrays.asList("dont mind", "mixed", "same"));
	public static  final List<String> religionPrefList = new ArrayList<String>(Arrays.asList("dont mind","mixed", "same"));
	public static  final List<String> ethnicPrefList = new ArrayList<String>(Arrays.asList("dont mind", "mixed", "same"));
	public static  final List<String> locationPrefList = new ArrayList<String>(Arrays.asList("dont mind", "L1", "L2",
			"L3", "L4", "L5", "L6", "L7", "L1/L2/L3", "L2/L3", "L3/L5", "L4/L2/L7", "L6/L2", "L7/L6/L5", "L1/L7", "L3/L6", 
			"L3/L6/L7"));
	public static  final List<String> accessPrefList = new ArrayList<String>(Arrays.asList("dont mind", "yes", "no"));
	public static  final List<String> rentalPrefList = new ArrayList<String>(Arrays.asList("dont mind", "1/1/2021-1/7/2021", 
			"1/5/2021-1/5/2022", "1/1/2021-1/7/2022", "1/3/2021-1/7/2022", " 1/1/2021-1/7/2022", "1/1/2021-1/4/2021",
			"15/3/2021-1/4/2022", "1/9/2021-1/12/2021", "3/7/2021-12/2/2022", "23/4/2020-17/5/2021"));
	public static  final List<String> shareWithPrefList = new ArrayList<String>(Arrays.asList("dont mind", "1", "2",
			"3", "4", "5", "1-3", "2-4", "1-5", "2-7","3-4", "1-4", "1-2", "2-3","3-5"));
	
	public static final String TwoDigitAfterDecimalFormat = "##.##";
	
	public static final String ThreeDigitAfterDecimalFormat = "##.###";

	public static void generateRandomTCNProfiles(int agentNumber, String path) throws IOException {
		int totalIdL = 4, totalAgeL = 5, totalGenderL = 8, totalFamilyL = 21, totalNationL = 7, totalReligL = 8, 
				totalEthnicL = 8, totalAgePL = 20, totalGenderPL = 13, totalFamilyPL = 51, totalNationPL = 13, 
				totalReligPL = 12, totalEthnicPL = 11, totalLocPL = 15, totalAccessPL = 11, totalAccPL = 10,
				totalRentPL = 21, totalShrWPL = 12;
		
		FileWriter fileWriter=new FileWriter(new File(path));
		PrintWriter printWriter=new PrintWriter(fileWriter);
		
		ThreadLocalRandom rand= ThreadLocalRandom.current();
		for (int i = 0; i < agentNumber; i++) {
			int id = (i+1) ; 
			String bSpace = generateBlankSpace(totalIdL - String.valueOf(id).length());
			printWriter.append(bSpace+id+"|");
			// age
			int age = rand.nextInt(18, 75);
			bSpace = generateBlankSpace(totalAgeL - String.valueOf(age).length());
			printWriter.append(bSpace+age+ "|");
			// gender
			String value = genderList.get(rand.nextInt(0, genderList.size()));
			bSpace = generateBlankSpace(totalGenderL-value.length());
			printWriter.append(bSpace+ value+"|");
			// family
			value = familyList.get(rand.nextInt(0, familyList.size()));
			bSpace = generateBlankSpace(totalFamilyL-value.length());
			printWriter.append(bSpace+value+"|");
			// nation
			value = nationalityList.get(rand.nextInt(0, nationalityList.size()));
			bSpace = generateBlankSpace(totalNationL-value.length());
			printWriter.append(bSpace+value+"|");
			// religion
			value = religionList.get(rand.nextInt(0, religionList.size()));
			bSpace = generateBlankSpace(totalReligL-value.length());
			printWriter.append(bSpace+value+"|");
			// ethnic
			value = ethnicList.get(rand.nextInt(0, ethnicList.size()));
			bSpace = generateBlankSpace(totalEthnicL-value.length());
			printWriter.append(bSpace+value+"|");
			// age pref
			value = agePrefList.get(rand.nextInt(0, agePrefList.size()));
			bSpace = generateBlankSpace(totalAgePL-value.length());
			printWriter.append(bSpace+value+"|");
			// gender pref
			value = genderPrefList.get(rand.nextInt(0, genderPrefList.size()));
			bSpace = generateBlankSpace(totalGenderPL-value.length());
			printWriter.append(bSpace+value+"|");
			// family pref
			value = familyPrefList.get(rand.nextInt(0, familyPrefList.size()));
			bSpace = generateBlankSpace(totalFamilyPL-value.length());
			printWriter.append(bSpace+value+"|");
			// nation pref
			value = nationPrefList.get(rand.nextInt(0, nationPrefList.size()));
			bSpace = generateBlankSpace(totalNationPL-value.length());
			printWriter.append(bSpace+value+"|");
			// religion pref
			value = religionPrefList.get(rand.nextInt(0, religionPrefList.size()));
			bSpace = generateBlankSpace(totalReligPL-value.length());
			printWriter.append(bSpace+value+"|");
			// ethnic pref
			value = ethnicPrefList.get(rand.nextInt(0, ethnicPrefList.size()));
			bSpace = generateBlankSpace(totalEthnicPL-value.length());
			printWriter.append(bSpace+value+"|");
			// location pref
			value = locationPrefList.get(rand.nextInt(0, locationPrefList.size()));
			bSpace = generateBlankSpace(totalLocPL-value.length());
			printWriter.append(bSpace+value+"|");
			// access pref
			value = accessPrefList.get(rand.nextInt(0, accessPrefList.size()));
			bSpace = generateBlankSpace(totalAccessPL-value.length());
			printWriter.append(bSpace+value+"|");
			// rental pref
			value = rentalPrefList.get(rand.nextInt(0, rentalPrefList.size()));
			bSpace = generateBlankSpace(totalRentPL-value.length());
			printWriter.append(bSpace+value+"|");
			// share with pref
			value = shareWithPrefList.get(rand.nextInt(0, shareWithPrefList.size()));
			bSpace = generateBlankSpace(totalShrWPL-value.length());
			printWriter.append(bSpace+value+"|");
			printWriter.println();
		}
		
		printWriter.close();
		
	}
	public static String generateBlankSpace(int amount) {
		String space  = "";
		for (int i = 0; i < amount; i++) {
			space+=" ";
		}
		
		return space;
	}
	public static void writeRandomCHCInputToFile(int agentNumber) throws IOException {
		String path = "C:\\Users\\akka02\\Desktop\\Master-Thesis\\Software\\EclipseWorkspace\\HDBSCANStar_FRAMEWORK_JAVA_Code_with_Visualization\\HDBSCAN_Star\\Input-Output\\input2.txt";
		
		FileWriter fileWriter=new FileWriter(new File(path));
		PrintWriter printWriter=new PrintWriter(fileWriter);
		
		ThreadLocalRandom rand= ThreadLocalRandom.current();
		for (int i = 0; i < agentNumber; i++) {
			printWriter.append((i+1)+" |");
			printWriter.append(rand.nextInt(18, 75) + "|");
			printWriter.append(genderList.get(rand.nextInt(0, genderList.size()))+ " |");
			printWriter.append(familyList.get(rand.nextInt(0, familyList.size()))+ " |");
			printWriter.append(nationalityList.get(rand.nextInt(0, nationalityList.size()))+ " |");
			printWriter.append(religionList.get(rand.nextInt(0, religionList.size()))+ " |");
			printWriter.append(ethnicList.get(rand.nextInt(0, ethnicList.size()))+ " |");
			printWriter.append(agePrefList.get(rand.nextInt(0, agePrefList.size()))+ " |");
			printWriter.append(genderPrefList.get(rand.nextInt(0, genderPrefList.size()))+ " |");
			printWriter.append(familyPrefList.get(rand.nextInt(0, familyPrefList.size()))+ " |");
			printWriter.append(nationPrefList.get(rand.nextInt(0, nationPrefList.size()))+ " |");
			printWriter.append(religionPrefList.get(rand.nextInt(0, religionPrefList.size()))+ " |");
			printWriter.append(ethnicPrefList.get(rand.nextInt(0, ethnicPrefList.size()))+ " |");
			printWriter.append(locationPrefList.get(rand.nextInt(0, locationPrefList.size()))+ " |");
			printWriter.append(accessPrefList.get(rand.nextInt(0, accessPrefList.size()))+ " |");
			printWriter.append(rentalPrefList.get(rand.nextInt(0, rentalPrefList.size()))+ " |");
			printWriter.append(shareWithPrefList.get(rand.nextInt(0, shareWithPrefList.size()))+ " |");
			printWriter.println();
		}
		
		printWriter.close();
		
	}
	
	public static int getRandomInt(int min, int max) {
		return 0;
	}
	
	public static LocalDate convertStringToDate(String input, String dateFormatPattern) {
		DateTimeFormatter dateTimeFormatter=DateTimeFormatter.ofPattern(dateFormatPattern, Locale.ENGLISH);
		return LocalDate.parse(input,dateTimeFormatter);
	}
	
	public static long convertDateToMillis(LocalDate date) {
		Instant instant = date.atStartOfDay(ZoneId.systemDefault()).toInstant();
		long timeInMls = instant.toEpochMilli();
	
		return timeInMls;
	}
	public static <K, V extends Comparable<? super V>> Map<K, V> sortByValue(Map<K, V> map) {
        List<Entry<K, V>> list = new ArrayList<>(map.entrySet());
        list.sort(Entry.comparingByValue());

        Map<K, V> result = new LinkedHashMap<>();
        for (Entry<K, V> entry : list) {
            result.put(entry.getKey(), entry.getValue());
        }

        return result;
    }
	
	public static void test() {
		int[][] rankArray = new int[][] {
			{3, 1, 2, 0}
		};
		
		double[][] valueArray = new double[][] {
			{1.2, 4.5, 0.5, 3.8}
		};

		
		List<Map<Integer, Double>> distValuesMap = new ArrayList<>();
		Map<Integer, Double> mp = new HashMap<>();
		mp.put(0, 0.5);
		mp.put(1, 1.5);
		mp.put(2, 1.0);
		mp.put(3, 5.0);
		mp.put(4, 2.0);
		mp = Utils.sortByValue(mp);
		
		Iterator<Integer> mapIt = mp.keySet().iterator();
		
		System.out.println("huhahuhahuha");
		while (mapIt.hasNext()) {
			int key = mapIt.next();
			double value = mp.get(key);
			//System.out.println("key:"+key+" val:"+value);
		}


		final Double[] vls = {1.5, 2.7, 0.3, 1.7};
		
		int[] sortedIndices = IntStream.range(0, vls.length)
				.boxed().sorted((i, j) -> vls[i].compareTo(vls[j]))
				.mapToInt(ele -> ele).toArray();
		
		for (int k = 0; k < sortedIndices.length; k++) {
			//System.out.println(sortedIndices[k]+": "+vls[sortedIndices[k]]);
			
		}
		
		Double[][] dst = {
			//   0  1      2      3       4      5      6      7      8      9 
				{0.0, 4.472, 7.071, 4.242, 7.211, 4.242, 4.472, 4.123, 6.082, 8.062},
				{4.472, 0.0, 4.242, 5.099, 2.828, 1.414, 6.324, 3.605, 2.236, 6.708},
				{7.071, 4.242, 0.0, 4.472, 5.099, 5.656, 5.830, 7.810, 2.236, 3.0},
				{4.242, 5.099, 4.472, 0.0, 7.615, 6.0, 1.414, 7.280, 5.0, 4.123 },
				{7.211, 2.828, 5.099, 7.615, 0.0, 3.162, 8.944, 5.0, 3.0, 8.062},
				{4.242, 1.414, 5.656, 6.0, 3.162, 0.0, 7.071, 2.236, 3.605, 8.062},
				{4.472, 6.324, 5.830, 1.414, 8.944, 7.071, 0.0, 8.062, 6.403, 5.0},
				{4.123, 3.605, 7.810, 7.280, 5.0, 2.236, 8.062, 0.0, 5.830, 10.0},
				{6.082, 2.236, 2.236, 5.0, 3.0, 3.605, 6.403, 5.830, 0.0, 5.099},
				{8.062, 6.708, 3.0, 4.123, 8.062, 8.062, 5.0, 10.0, 5.099, 0.0}
			};
		
		int[][] rankList = new int[dst.length][dst.length];
		
		for (int k = 0; k < rankList.length; k++) {
			rankList[k]=getSortedIndicesFromSmallerToHigher(dst[k]);
		}
		

		System.out.println("Rank List of Most Similar Agents");
		for (int k = 0; k < rankList.length; k++) {
			System.out.print("Agent"+(k+1)+"-> ");
			for (int k2 = 0; k2 < rankList[k].length; k2++) {
				System.out.print((rankList[k][k2]+1)+": "+dst[k][rankList[k][k2]]+", ");
			}
			System.out.println();
		}
		
	}
	
	public static int[] getSortedIndicesFromSmallerToHigher(Double[] inputArray) {
		int[] sortedIndices = IntStream.range(0, inputArray.length)
				.boxed().sorted((i, j) -> inputArray[i].compareTo(inputArray[j]))
				.mapToInt(ele -> ele).toArray();
		return sortedIndices;
	}
	
	//Reverse and get new Array 
    public static final int [] reverse(final int[] array) {
        final int len = array.length;
        final int[] reverse = (int[]) Array.newInstance(array.getClass().getComponentType(), len);
        for (int i = 0; i < len; i++) {
            reverse[i] = array[len-(i+1)];
        }
        return reverse;
    }
	
	public static String decimalFormat(String pattern, double value) {
		return new DecimalFormat(pattern).format(value);
	}
	
	public static double round(int amountOfNumbersAfterDecimal, double input) {
		double division = Math.pow(10, amountOfNumbersAfterDecimal);
		return Math.round(input*division)/division;
	}
}
