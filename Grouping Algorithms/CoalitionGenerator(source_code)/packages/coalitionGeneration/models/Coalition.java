package coalitionGeneration.models;

import java.util.List;


public class Coalition {
		private int ID; // representation of the coalition in bit format
		private int[] commonValues; // index 0:common Gender, index 1:common Nationality
		private int gender; // common gender of members -> {-2:unknown, -1:mixed, 0:male, 1:female} 
		private int nationality; // common nationality of members -> {-2:unknown, -1:mixed, n:nationID}
		private double sd=0; // standard deviation
		private double totalCpl; // sum of Course Progress Level of members
		private double value; // coalition value
		private List<Agent> memberAgents;

		public Coalition(int iD, List<Agent> memberAgents) {
			super();
			ID = iD;
			this.memberAgents = memberAgents;
			this.commonValues=new int[2];
			computeGenderAndNationality();
			computeSD();
		} 
		
		public Coalition(int iD, double value) {
			super();
			this.ID = iD;
			this.value=value;
		} 
	
		private void computeGenderAndNationality() {
			gender=-2; 
			nationality=-2;

			for (Agent agent : memberAgents) {
				totalCpl+=agent.getCPL();
				if(gender==-2) {
					gender=agent.getGender();		
				}else if (gender != agent.getGender() && gender!=-1) {
					gender=-1;
				}
				
				if(nationality==-2) {
					nationality=agent.getNationality();		
				}else if (nationality!= agent.getNationality() && nationality!=-1) {
					nationality=-1;
				}
			}
			
			commonValues[0]=this.gender;
			commonValues[1]=this.nationality;
		}
		
		private void computeSD() {
			double mean = 0;
			
			mean=totalCpl/memberAgents.size();
			
			for (Agent agent : memberAgents) {
				sd+=Math.pow((agent.getCPL()-mean),2);
			}
			this.sd=Math.sqrt(sd/memberAgents.size());			
		}

		
		public int getID() {
			return ID;
		}

		public void setID(int iD) {
			ID = iD;
		}

		public int[] getCommonValues() {
			return commonValues;
		}

		public void setCommonValues(int[] commonValues) {
			this.commonValues = commonValues;
		}

		public int getGender() {
			return gender;
		}

		public void setGender(int gender) {
			this.gender = gender;
		}

		public int getNationality() {
			return nationality;
		}

		public void setNationality(int nationality) {
			this.nationality = nationality;
		}

		public double getSd() {
			return sd;
		}

		public void setSd(double sd) {
			this.sd = sd;
		}

		public double getTotalCpl() {
			return totalCpl;
		}

		public void setTotalCpl(double totalCpl) {
			this.totalCpl = totalCpl;
		}

		public double getValue() {
			return value;
		}

		public void setValue(double value) {
			this.value = value;
		}
		public void addValue(double value) {
			this.value+=value;
		}

		public List<Agent> getMemberAgents() {
			return memberAgents;
		}

		public void setMemberAgents(List<Agent> memberAgents) {
			this.memberAgents = memberAgents;
		}
		
		
}
