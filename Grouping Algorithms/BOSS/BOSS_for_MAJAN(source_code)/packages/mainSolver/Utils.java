package mainSolver;

public class Utils {
	public static final String TwoDigitAfterDecimalFormat = "##.##";
	
	public static final String ThreeDigitAfterDecimalFormat = "##.###";
	
	
	public static double round(int amountOfNumbersAfterDecimal, double input) {
		double division = Math.pow(10, amountOfNumbersAfterDecimal);
		return Math.round(input*division)/division;
	}
}
