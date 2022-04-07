package ca.ualberta.cs.distance;

/**
 * Computes the euclidean distance between two points, d = sqrt((x1-y1)^2 + (x2-y2)^2 + ... + (xn-yn)^2).
 * @author zjullion
 */
public class EuclideanDistance implements DistanceCalculator {

	// ------------------------------ PRIVATE VARIABLES ------------------------------

	// ------------------------------ CONSTANTS ------------------------------

	// ------------------------------ CONSTRUCTORS ------------------------------
	
	public EuclideanDistance() {
	}

	// ------------------------------ PUBLIC METHODS ------------------------------
	
	public double computeDistance(double[] attributesOne, double[] attributesTwo) {
		
		double distance = 0;
		
		for (int i = 0; i < attributesOne.length && i < attributesTwo.length; i++) {
			distance+= ((attributesOne[i] - attributesTwo[i]) * (attributesOne[i] - attributesTwo[i]));
		}
		
		return Math.sqrt(distance);
	}
	
	
	public String getName() {
		return "euclidean";
	}

	
	double[][] values = new double[][] {
		{0, 3.063, 1.806, 1.851, 2.834, 1.940, 1.510, 2.834, 4.131, 1.445},
		{3.063, 0, 5.483, 1.805, 2.624, 1.076, 2.690, 6.483, 5.781, 1.670},
		{1.806, 5.483, 0, 2.405, 1.363, 0.822, 5.848, 3.322, 6.955, 5.728},
		{1.851, 1.805, 2.405, 0, 2.468, 0.522, 0.559, 2.255, 2.255, 1.851},
		{2.834, 2.624, 1.363, 2.468, 0, 0.972, 5.086, 3.862, 3.194, 4.729},
		{1.940, 1.076, 0.822, 0.522, 0.972, 0, 0.736, 0.833, 1.634, 2.367},
		{1.510, 2.690, 5.848, 0.559, 5.086, 0.736, 0, 6.973, 9.343, 0.562},
		{2.834, 6.483, 3.322, 2.255, 3.862, 0.833, 6.973, 0, 6.471, 3.881},
		{4.131, 5.781, 6.955, 2.255, 3.194, 1.634, 9.343, 6.471, 0, 5.816},
		{1.445, 1.670, 5.728, 1.851, 4.729, 2.367, 0.562, 3.881, 5.816, 0}
	};
	@Override
	public double getDistance(int object1, int object2) {

		
		return values[object1][object2];
	}

	// ------------------------------ PRIVATE METHODS ------------------------------

	// ------------------------------ GETTERS & SETTERS ------------------------------

}