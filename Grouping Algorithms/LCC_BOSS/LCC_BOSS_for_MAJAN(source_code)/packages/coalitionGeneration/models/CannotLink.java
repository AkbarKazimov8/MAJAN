package coalitionGeneration.models;

import java.util.List;

public class CannotLink {
	private int x,y,k,b;
	private int[] pair;
	private List<int[]> f;
	
	public CannotLink(int x, int y) {
		super();
		this.x = x;
		this.y = y;
		}


	public int getX() {
		return x;
	}


	public void setX(int x) {
		this.x = x;
	}


	public int getY() {
		return y;
	}


	public void setY(int y) {
		this.y = y;
	}


	public int[] getPair() {
		return pair;
	}


	public void setPair(int[] pair) {
		this.pair = pair;
	}
	
	
}
