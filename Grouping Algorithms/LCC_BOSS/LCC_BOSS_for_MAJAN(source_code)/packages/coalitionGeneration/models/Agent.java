package coalitionGeneration.models;

public class Agent {

	final private int ID;
	final private int[] personalInfo; // index 0:gender, 1:nationality. value at index 0 0:male, 1:female. value at index 1 n:index of nationality
	//private int gender; // 0:male, 1:female
	//private String nationality;
	private double CPL;
	final private int attendance; // 0:attended, 1:missed such that summation of attendance values of agents in a coalition returns the amount who missed
	final private int[] preferences; // index 0:genderPref, 1:nationPref. value at index 0 and 1 -1:mixed, 0:same
	//private int gPref, nPref; // 0:same, 1:mixed
	private double[] preferenceWeights;
	
	public Agent(int ID, double CPL, int attendance, int gender, int nationality, int genderPref, int nationPref, double genderWeight, double nationWeight) {
		this.ID=ID;
		this.CPL=CPL;
		this.attendance=attendance;
		this.preferenceWeights=new double[] {genderWeight,nationWeight};
		this.personalInfo=new int[] {gender,nationality};
		this.preferences=new int[] {genderPref,nationPref};
	}

	public void setPreferenceWeights(double genderWeight, double nationWeight) {
		this.preferenceWeights[0] = genderWeight;
		this.preferenceWeights[1] = nationWeight;
	}
	
	public int getID() {
		return ID;
	}

	public double getCPL() {
		return CPL;
	}

	public void setCPL(double cPL) {
		CPL = cPL;
	}

	public int getAttendance() {
		return attendance;
	}


	public double[] getPreferenceWeights() {
		return preferenceWeights;
	}

	
	public int getGender() {
		return personalInfo[0];
	}
	
	public int getNationality() {
		return personalInfo[1];
	}
	
	public int getGenderPreference() {
		return preferences[0];
	}
	
	public int getNationalityPreference() {
		return preferences[1];
	}

	public int[] getPersonalInfo() {
		return personalInfo;
	}

	public int[] getPreferences() {
		return preferences;
	}
	
	
	
}
