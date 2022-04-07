package chc.problem;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import ca.ualberta.cs.hdbscanstar.Constraint;
import ca.ualberta.cs.hdbscanstar.Constraint.CONSTRAINT_TYPE;



public class CHCInput {
	private final static String[] dontMinds = new String[] {"don't mind", "dont mind", "dontmind", "don'tmind", "Dont mind"};
	
	public static double 
	ageWeight = 10,
	genderWeight = 9, 
	familyWeight = 5, 
	nationWeight = 8,
	religionWeight = 6, 
	ethnicWeight = 7, 
	locationWeight = 2, 
	accessWeight = 3, 
	rentPeriodWeight = 1,
	shareWithWeight = 4;
	
	public static final String 
	same = "same",
	mixed = "mixed";
	private List<Agent> agents = null;
	private ArrayList<Constraint> constraints;

	public CHCInput readChcInput(String inputPath, CHCInput chcInput) throws IOException {

		constraints = new ArrayList<Constraint>();
		
		BufferedReader reader = new BufferedReader(new FileReader(inputPath));
		Iterator<String> linesIter = reader.lines().iterator();
		while(linesIter.hasNext()) {
			String line=linesIter.next();

			if(line==null || line.trim().equals("") || line.trim().isEmpty() || line.trim().startsWith("//")) {
				continue;
			}else if (line.toLowerCase().startsWith("ml")) {
				constraints.addAll(createMLConstraints(line.substring(line.indexOf("{"))));
			}else if(line.toLowerCase().startsWith("cl")) {
				constraints.addAll(createCLConstraints(line.substring(line.indexOf("{"))));				
			}else if (line.toLowerCase().startsWith("weights")) {
				setWeights(line.substring(line.indexOf("{")));
			}else if(line.toLowerCase().startsWith("agent profiles")) {
				agents = createAgentsList(line.substring(line.indexOf("{")));
			}
		}

		chcInput.setAgents(agents);
		chcInput.setConstraints(constraints);
		return chcInput;
	}
	
	public CHCInput readInputFromFile(String inputPath, CHCInput chcInput) throws IOException {
		agents = new ArrayList<Agent>();
		constraints = new ArrayList<Constraint>();
		
		BufferedReader reader = new BufferedReader(new FileReader(inputPath));
		Iterator<String> linesIter = reader.lines().iterator();
		while(linesIter.hasNext()) {
			String line=linesIter.next();

			if(line==null || line.trim().equals("") || line.trim().isEmpty() || line.trim().startsWith("//")) {
				continue;
			}else if(Character.isDigit(line.trim().charAt(0))) {
				agents.add(createAgent(line));
			}else if (line.toLowerCase().startsWith("ml")) {
				constraints.addAll(createMLConstraints(line.substring(line.indexOf("{"))));
			}else if(line.toLowerCase().startsWith("cl")) {
				constraints.addAll(createCLConstraints(line.substring(line.indexOf("{"))));				
			}else if (line.toLowerCase().startsWith("weights")) {
				setWeights(line.substring(line.indexOf("{")));
			}
		}

		chcInput.setAgents(agents);
		chcInput.setConstraints(constraints);
		return chcInput;
	}
	
	private void setWeights(String inputLine) throws IOException  {
		String[] weights=inputLine.trim().replace("{", "").replace("}", "").split(",");
		if (weights.length!=10) {
			throw new IOException("For 10 Preference Attributes, there are more/less weights.");
		}
		ageWeight = Integer.valueOf(weights[0].trim());
		genderWeight = Integer.valueOf(weights[1].trim());
		familyWeight = Integer.valueOf(weights[2].trim());
		nationWeight = Integer.valueOf(weights[3].trim());
		religionWeight = Integer.valueOf(weights[4].trim());
		ethnicWeight = Integer.valueOf(weights[5].trim());
		locationWeight = Integer.valueOf(weights[6].trim());
		accessWeight = Integer.valueOf(weights[7].trim());
		rentPeriodWeight = Integer.valueOf(weights[8].trim());
		shareWithWeight = Integer.valueOf(weights[9].trim());

	}
	private ArrayList<Constraint> createMLConstraints(String inputLine){
		String[] mls = inputLine.trim().split("\\|");
		ArrayList<Constraint> constraints = new ArrayList<Constraint>();
		for (String ml: mls) {
			
			String[] pairs=ml.trim().replace("{", "").replace("}", "").split(",");
			Constraint constraint = new Constraint(Integer.valueOf(pairs[0])-1, Integer.valueOf(pairs[1])-1, CONSTRAINT_TYPE.MUST_LINK);
			constraints.add(constraint);
		}
		return constraints;
	}
	
	private ArrayList<Constraint> createCLConstraints(String inputLine){
		String[] cls = inputLine.trim().split("\\|");
		ArrayList<Constraint> constraints = new ArrayList<Constraint>();

		for (String cl: cls) {

			String[] pairs=cl.trim().replace("{", "").replace("}", "").split(",");
			Constraint constraint = new Constraint(Integer.valueOf(pairs[0])-1, Integer.valueOf(pairs[1])-1, CONSTRAINT_TYPE.CANNOT_LINK);
			constraints.add(constraint);
			}
		return constraints;
	}
	public List<Agent> readInputFromFile(String inputPath) throws FileNotFoundException {
		List<Agent> agentList = new ArrayList();
		
		BufferedReader reader = new BufferedReader(new FileReader(inputPath));
		Iterator<String> linesIter = reader.lines().iterator();
		while(linesIter.hasNext()) {
			String line=linesIter.next();

			if(line==null || line.trim().equals("") || line.trim().isEmpty() || line.trim().startsWith("//")) {
				continue;
			}else if(Character.isDigit(line.trim().charAt(0))) {
				agentList.add(createAgent(line));
			}
		}
		return agentList;
	}
	
	private Agent createAgent(String tcnInfo) {
		Agent agent=new Agent();
		String[] values = tcnInfo.trim().split("\\|");
		agent.setId(Integer.valueOf(values[0].trim()));
		agent.setAge(Integer.valueOf(values[1].trim()));
		agent.setGender(values[2].trim().toLowerCase());
		agent.setFamily(values[3].trim().toLowerCase());
		agent.setNationality(values[4].trim().toLowerCase());
		agent.setReligion(values[5].trim().toLowerCase());
		agent.setEthnicity(values[6].trim().toLowerCase());
		agent = setAgePreference(agent ,values[7].trim());
		agent = setGenderPreference(agent, values[8].trim());
		agent = setFamilyPreference(agent, values[9].trim());
		agent = setNationPreference(agent, values[10].trim());
		agent = setReligionPreference(agent, values[11].trim());
		agent = setEthnicPreference(agent, values[12].trim());
		agent = setLocationPreference(agent, values[13].trim());
		agent = setAccessibilityPreference(agent, values[14].trim());
		agent = setRentalPeriodPreference(agent, values[15].trim());
		agent = setShareWithPreference(agent, values[16].trim());
		
		return agent;
	}
	
	private List<Agent> createAgentsList(String agentProfilesLine){
		List<Agent> agents=new ArrayList<Agent>();
		String[] agProfilesArray = agentProfilesLine.trim().split("\\|");
		
		for(String agProfile: agProfilesArray) {
			String[] agentProfileAttributes = agProfile.replace("{", "").replace("}", "").trim().split(",");
			
			Agent agent=new Agent();

			agent.setId(Integer.valueOf(agentProfileAttributes[0].trim()));
			agent.setAge(Integer.valueOf(agentProfileAttributes[1].trim()));
			agent.setGender(agentProfileAttributes[2].trim().toLowerCase());
			agent.setFamily(agentProfileAttributes[3].trim().toLowerCase());
			agent.setNationality(agentProfileAttributes[4].trim().toLowerCase());
			agent.setReligion(agentProfileAttributes[5].trim().toLowerCase());
			agent.setEthnicity(agentProfileAttributes[6].trim().toLowerCase());
			agent = setAgePreference(agent ,agentProfileAttributes[7].trim());
			agent = setGenderPreference(agent, agentProfileAttributes[8].trim());
			agent = setFamilyPreference(agent, agentProfileAttributes[9].trim());
			agent = setNationPreference(agent, agentProfileAttributes[10].trim());
			agent = setReligionPreference(agent, agentProfileAttributes[11].trim());
			agent = setEthnicPreference(agent, agentProfileAttributes[12].trim());
			agent = setLocationPreference(agent, agentProfileAttributes[13].trim());
			agent = setAccessibilityPreference(agent, agentProfileAttributes[14].trim());
			agent = setRentalPeriodPreference(agent, agentProfileAttributes[15].trim());
			agent = setShareWithPreference(agent, agentProfileAttributes[16].trim());
			
			agents.add(agent);

			/*System.out.println("11--" + agent.getId());
			System.out.println(agent.getAge());
			System.out.println(agent.getGender());
			System.out.println(agent.getFamily());
			System.out.println(agent.getNationality());
			System.out.println(agent.getReligion());
			System.out.println(agent.getEthnicity());
			System.out.println(agent.getAgePrefList());
			System.out.println(agent.getGenderPref());
			System.out.println(agent.getFamilyPrefList());
			System.out.println(agent.getNationPref());
			System.out.println(agent.getReligionPref());
			System.out.println(agent.getEthnicPref());
			System.out.println(agent.getLocationPrefList());
			System.out.println(agent.getAccessibilityPref());
			System.out.println(agent.getRentalPeriodPref());
			System.out.println(agent.getShareWithPref());*/
		}
		return agents;
	}
	
	private Agent setAgePreference(Agent agent, String preference) {
		preference=preference.trim().toLowerCase();
		// Set true if it is don't mind
		for (String dontMind: dontMinds) {
			if(preference.equals(dontMind)) {
				agent.setAgePrefDontMind(true);
				return agent;
			}
		}
		
		// If not don't mind, then read the age range(s) and set them as the age preference
		String[] ageRanges = preference.split("/");
		List<Range> ageRangeList = new ArrayList();
		
		for (String ageRange : ageRanges) {
			Range rangeIns=new Range();
			String[] bounds = ageRange.split("-");
			rangeIns.setLowerBound(Integer.valueOf(bounds[0].trim()));
			rangeIns.setHigherBound(Integer.valueOf(bounds[1].trim()));
			ageRangeList.add(rangeIns);
		}
		
		agent.setAgePrefList(ageRangeList);
		
		return agent;
	}
	
	private Agent setGenderPreference(Agent agent, String preference) {
		preference=preference.trim().toLowerCase();
		// Set true if it is don't mind
		for (String dontMind: dontMinds) {
			if(preference.equals(dontMind)) {
				agent.setGenderPrefDontMind(true);
				return agent;
			}
		}
		
		agent.setGenderPref(preference);
		
		return agent;	
	}
	
	private Agent setFamilyPreference(Agent agent, String preference) {
		preference=preference.trim().toLowerCase();
		// Set true if it is don't mind
		for (String dontMind: dontMinds) {
			if(preference.equals(dontMind)) {
				agent.setFamilyPrefDontMind(true);
				return agent;
			}
		}
		
		String[] familyPreferences = preference.split("/");
		List<String> familyPreferenceList = new ArrayList();
		
		for (String familyPreference : familyPreferences) {
			familyPreferenceList.add(familyPreference.trim());
		}
		agent.setFamilyPrefList(familyPreferenceList);
		

		return agent;	
	}
	
	private Agent setNationPreference(Agent agent, String preference) {
		preference=preference.trim().toLowerCase();
		// Set true if it is don't mind
		for (String dontMind: dontMinds) {
			if(preference.equals(dontMind)) {
				agent.setNationPrefDontMind(true);
				return agent;
			}
		}
		agent.setNationPref(preference);
				
		return agent;	
	}
	
	private Agent setReligionPreference(Agent agent, String preference) {
		//System.out.println("////////*********-" + preference);
		preference=preference.trim().toLowerCase();
		// Set true if it is don't mind
		for (String dontMind: dontMinds) {
			if(preference.equals(dontMind)) {
				agent.setReligionPrefDontMind(true);
				return agent;
			}
		}
		agent.setReligionPref(preference);
				
		return agent;	
	}
	
	private Agent setEthnicPreference(Agent agent, String preference) {
		preference=preference.trim().toLowerCase();
		// Set true if it is don't mind
		for (String dontMind: dontMinds) {
			if(preference.equals(dontMind)) {
				agent.setEthnicPrefDontMind(true);
				return agent;
			}
		}
		agent.setEthnicPref(preference);
				
		return agent;	
	}
	
	private Agent setLocationPreference(Agent agent, String preference) {
		preference=preference.trim().toLowerCase();
		// Set true if it is don't mind
		for (String dontMind: dontMinds) {
			if(preference.equals(dontMind)) {
				agent.setLocationPrefDontMind(true);
				return agent;
			}
		}
		
		String[] locationPreferences = preference.split("/");
		List<String> locationPreferenceList = new ArrayList();
		
		for (String locationPreference : locationPreferences) {
			locationPreferenceList.add(locationPreference.trim());
		}
		agent.setLocationPrefList(locationPreferenceList);
		

		return agent;	
	}
	
	private Agent setAccessibilityPreference(Agent agent, String preference) {
		preference=preference.trim().toLowerCase();
		// Set true if it is don't mind
		for (String dontMind: dontMinds) {
			if(preference.equals(dontMind)) {
				agent.setAccessibilityPrefDontMind(true);
				return agent;
			}
		}
		agent.setAccessibilityPref(preference);
				
		return agent;	
	}
	
	private Agent setRentalPeriodPreference(Agent agent, String preference) {
		preference=preference.trim().toLowerCase();
		// Set true if it is don't mind
		for (String dontMind: dontMinds) {
			if(preference.equals(dontMind)) {
				agent.setRentalPeriodPrefDontMind(true);
				return agent;
			}
		}
		
		// If not "don't mind", then read the rental period range and set it as the preference
		String[] rentalPeriodBounds = preference.split("-");
		Range rentalPeriodRange = new Range();
		
		long lowerBoundInMillis = Utils.convertDateToMillis(Utils.convertStringToDate(rentalPeriodBounds[0].trim(), 
				Utils.dateFormat1));
		long higherBoundInMillis = Utils.convertDateToMillis(Utils.convertStringToDate(rentalPeriodBounds[1].trim(), 
				Utils.dateFormat1));

		rentalPeriodRange.setLowerBound(lowerBoundInMillis);
		rentalPeriodRange.setHigherBound(higherBoundInMillis);
		agent.setRentalPeriodPref(rentalPeriodRange);
		
		
		return agent;
	}

	private Agent setShareWithPreference(Agent agent, String preference) {
		preference=preference.trim().toLowerCase();
		// Set true if it is don't mind
		for (String dontMind: dontMinds) {
			if(preference.equals(dontMind)) {
				agent.setShareWithPrefDontMind(true);
				return agent;
			}
		}
		
		// If not "don't mind", then read the "share with" range and set it as the preference
		String[] shareWithBounds = preference.split("-");
		Range shareWithRange = new Range();

		if (shareWithBounds.length==1) {
			shareWithRange.setLowerBound(Integer.valueOf(shareWithBounds[0].trim()));
			shareWithRange.setHigherBound(Integer.valueOf(shareWithBounds[0].trim()));			
		}else {
			shareWithRange.setLowerBound(Integer.valueOf(shareWithBounds[0].trim()));
			shareWithRange.setHigherBound(Integer.valueOf(shareWithBounds[1].trim()));
		}
		
		agent.setShareWithPref(shareWithRange);
		
		
		return agent;
	}

	public void printAgents(List<Agent> agents) {
		for (Agent agent : agents) {
			System.out.println("id:"+agent.getId());
			System.out.println("age:"+agent.getAge());
			System.out.println("gender:"+agent.getGender());
			System.out.println("family:"+agent.getFamily());
			
			System.out.println("nation:"+agent.getNationality());
			
			System.out.println("religion:"+agent.getReligion());
			
			System.out.println("ethnic:"+agent.getEthnicity());
			
			System.out.println("age_pref_dm:"+agent.isAgePrefDontMind());
			if(null!=agent.getAgePrefList()) {
				List<Range> ageRangePref= agent.getAgePrefList();
				for (Range ageRange : ageRangePref) {
					System.out.println("lower:"+ageRange.getLowerBound());
					System.out.println("higher:"+ageRange.getHigherBound());
				}					
			}
			
			System.out.println("gender_pref_dm:"+agent.isGenderPrefDontMind());
			if(null!=agent.getGenderPref())
			System.out.println("gender_pref:"+agent.getGenderPref());
			
			System.out.println("family_pref_dm:"+agent.isFamilyPrefDontMind());
			if(null!=agent.getFamilyPrefList()) {
				List<String> familyPrefs = agent.getFamilyPrefList();
				for (String familyPref : familyPrefs) {
					System.out.println("family_pref:"+familyPref);
				}				
			}
			
			System.out.println("nation_pref_dm:"+agent.isNationPrefDontMind());
			if(null!=agent.getNationPref())
			System.out.println("nation_pref:"+agent.getNationPref());
			
			System.out.println("religion_pref_dm:"+agent.isReligionPrefDontMind());
			if(null!=agent.getReligionPref())
			System.out.println("religion_pref:"+agent.getReligionPref());
			
			System.out.println("ethnic_pref_dm:"+agent.isEthnicPrefDontMind());
			if(null!=agent.getEthnicPref())
			System.out.println("ethnic_pref:"+agent.getEthnicPref());
			
			System.out.println("location_pref_dm:"+agent.isLocationPrefDontMind());
			if(null!=agent.getLocationPrefList()) {
				List<String> locationPrefs = agent.getLocationPrefList();
				for (String locPref : locationPrefs) {
					System.out.println("location_pref:"+locPref);
					}
			}
			System.out.println("access_pref_dm:"+agent.isAccessibilityPrefDontMind());
			if(null!=agent.getAccessibilityPref())
			System.out.println("access_pref:"+agent.getAccessibilityPref());

			System.out.println("rentPeriod_pref_dm:"+agent.isRentalPeriodPrefDontMind());
			if(null!=agent.getRentalPeriodPref()) {
				Range rentPeriod = agent.getRentalPeriodPref();
				System.out.println("lower:"+rentPeriod.getLowerBound());
				System.out.println("higher:"+rentPeriod.getHigherBound());
			}
			System.out.println("shareWith_pref_dm:"+agent.isShareWithPrefDontMind());
			if(null!=agent.getShareWithPref()) {
				Range shareWithPeriod = agent.getShareWithPref();
				System.out.println("lower:"+shareWithPeriod.getLowerBound());
				System.out.println("higher:"+shareWithPeriod.getHigherBound());
			}
		}
	}

	public List<Agent> getAgents() {
		return agents;
	}

	public void setAgents(List<Agent> agents) {
		this.agents = agents;
	}

	public ArrayList<Constraint> getConstraints() {
		return constraints;
	}

	public void setConstraints(ArrayList<Constraint> constraints) {
		this.constraints = constraints;
	}
	
	
	
	
}
