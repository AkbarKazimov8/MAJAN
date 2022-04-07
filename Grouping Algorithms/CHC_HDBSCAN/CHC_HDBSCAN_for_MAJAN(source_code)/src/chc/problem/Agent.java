package chc.problem;

import java.util.List;

public class Agent {

	// + <agent_id:{int}> | 
	// + <age:{int}> | 
	// + <gender:{male, female, other}> | 
	// + <family:{single_man, single_woman, nuclear, single_parent_mother, single_parent_father, extended}> |
	// + <nationality: {String}> | 
	// + <religion: {String}> | 
	// + <ethinicity: {String}> |
	// + <age_pref:{don't mind, (1 or multiple of 18-25, 26-33, 34-43, 44-50, 51-65, 65-120)}> |
	// + <gender_pref:{don't mind, male, female, other}> |
	// + <family_pref:{don't mind, single_man, single_woman, nuclear, single_parent_mother, single_parent_father, extended}> |
	// + <nation_pref:{don't mind, mixed, same}> |
	// + <religion_pref:{don't mind, mixed, same}> |
	// + <ethnic_pref:{don't mind, mixed, same}> |
	// + <location_pref:{don't mind, (String)}> |
	// + <access_pref:{dont'mind, yes}> |
	// + <rental_pref:{dont'mind, (range from-to: dd/mm/yyyy, dd/mm/yyyy)} | 
	// + <shareWith_pref:{don't mind, (range from-to: int, int}>

	private int id;
	private int age;
	private String gender;
	private String family;
	private String nationality;
	private String religion;
	private String ethnicity;
	private boolean agePrefDontMind = false;
	private List<Range> agePrefList;
	private boolean genderPrefDontMind = false;
	private String genderPref;
	private boolean familyPrefDontMind = false;
	private List<String> familyPrefList;
	private boolean nationPrefDontMind = false;
	private String nationPref;
	private boolean religionPrefDontMind = false;
	private String religionPref;
	private boolean ethnicPrefDontMind = false;
	private String ethnicPref;
	private boolean locationPrefDontMind = false;
	private List<String> locationPrefList;
	private boolean accessibilityPrefDontMind = false;
	private String accessibilityPref;
	private boolean rentalPeriodPrefDontMind = false;
	private Range rentalPeriodPref;
	private boolean shareWithPrefDontMind = false;
	private Range shareWithPref;
	
	
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getAge() {
		return age;
	}
	public void setAge(int age) {
		this.age = age;
	}
	public String getGender() {
		return gender;
	}
	public void setGender(String gender) {
		this.gender = gender.trim();
	}
	public String getFamily() {
		return family;
	}
	public void setFamily(String family) {
		this.family = family.trim();
	}
	public String getNationality() {
		return nationality;
	}
	public void setNationality(String nationality) {
		this.nationality = nationality.trim();
	}
	public String getReligion() {
		return religion;
	}
	public void setReligion(String religion) {
		this.religion = religion.trim();
	}
	public String getEthnicity() {
		return ethnicity;
	}
	public void setEthnicity(String ethnicity) {
		this.ethnicity = ethnicity.trim();
	}
	public boolean isAgePrefDontMind() {
		return agePrefDontMind;
	}
	public void setAgePrefDontMind(boolean agePrefDontMind) {
		this.agePrefDontMind = agePrefDontMind;
	}
	public List<Range> getAgePrefList() {
		return agePrefList;
	}
	public void setAgePrefList(List<Range> agePrefList) {
		this.agePrefList = agePrefList;
	}
	public boolean isGenderPrefDontMind() {
		return genderPrefDontMind;
	}
	public void setGenderPrefDontMind(boolean genderPrefDontMind) {
		this.genderPrefDontMind = genderPrefDontMind;
	}
	public String getGenderPref() {
		return genderPref;
	}
	public void setGenderPref(String genderPref) {
		this.genderPref = genderPref.trim();
	}
	public boolean isFamilyPrefDontMind() {
		return familyPrefDontMind;
	}
	public void setFamilyPrefDontMind(boolean familyPrefDontMind) {
		this.familyPrefDontMind = familyPrefDontMind;
	}
	public List<String> getFamilyPrefList() {
		return familyPrefList;
	}
	public void setFamilyPrefList(List<String> familyPrefList) {
		this.familyPrefList = familyPrefList;
	}
	public boolean isNationPrefDontMind() {
		return nationPrefDontMind;
	}
	public void setNationPrefDontMind(boolean nationPrefDontMind) {
		this.nationPrefDontMind = nationPrefDontMind;
	}
	public String getNationPref() {
		return nationPref;
	}
	public void setNationPref(String nationPref) {
		this.nationPref = nationPref;
	}
	public boolean isReligionPrefDontMind() {
		return religionPrefDontMind;
	}
	public void setReligionPrefDontMind(boolean religionPrefDontMind) {
		this.religionPrefDontMind = religionPrefDontMind;
	}
	public String getReligionPref() {
		return religionPref;
	}
	public void setReligionPref(String religionPref) {
		this.religionPref = religionPref;
	}
	public boolean isEthnicPrefDontMind() {
		return ethnicPrefDontMind;
	}
	public void setEthnicPrefDontMind(boolean ethnicPrefDontMind) {
		this.ethnicPrefDontMind = ethnicPrefDontMind;
	}
	public String getEthnicPref() {
		return ethnicPref;
	}
	public void setEthnicPref(String ethnicPref) {
		this.ethnicPref = ethnicPref;
	}
	public boolean isLocationPrefDontMind() {
		return locationPrefDontMind;
	}
	public void setLocationPrefDontMind(boolean locationPrefDontMind) {
		this.locationPrefDontMind = locationPrefDontMind;
	}
	public List<String> getLocationPrefList() {
		return locationPrefList;
	}
	public void setLocationPrefList(List<String> locationPrefList) {
		this.locationPrefList = locationPrefList;
	}
	public boolean isAccessibilityPrefDontMind() {
		return accessibilityPrefDontMind;
	}
	public void setAccessibilityPrefDontMind(boolean accessibilityPrefDontMind) {
		this.accessibilityPrefDontMind = accessibilityPrefDontMind;
	}
	public String getAccessibilityPref() {
		return accessibilityPref;
	}
	public void setAccessibilityPref(String accessibilityPref) {
		this.accessibilityPref = accessibilityPref;
	}
	public boolean isRentalPeriodPrefDontMind() {
		return rentalPeriodPrefDontMind;
	}
	public void setRentalPeriodPrefDontMind(boolean rentalPeriodPrefDontMind) {
		this.rentalPeriodPrefDontMind = rentalPeriodPrefDontMind;
	}
	public Range getRentalPeriodPref() {
		return rentalPeriodPref;
	}
	public void setRentalPeriodPref(Range rentalPeriodPref) {
		this.rentalPeriodPref = rentalPeriodPref;
	}
	public boolean isShareWithPrefDontMind() {
		return shareWithPrefDontMind;
	}
	public void setShareWithPrefDontMind(boolean shareWithPrefDontMind) {
		this.shareWithPrefDontMind = shareWithPrefDontMind;
	}
	public Range getShareWithPref() {
		return shareWithPref;
	}
	public void setShareWithPref(Range shareWithPref) {
		this.shareWithPref = shareWithPref;
	}
	
}
