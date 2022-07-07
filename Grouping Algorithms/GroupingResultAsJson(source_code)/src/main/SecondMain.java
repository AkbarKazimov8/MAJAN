package main;

import java.net.URI;
import java.net.URISyntaxException;

public class SecondMain {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String s = "/app/executionservice/welcome/services/request";
		String s2 = "file:/app/executionservice/welcome/services/request";

		try {
			URI uri = new URI(s);
			URI uri2 = new URI(s2);
			
			System.out.println(uri.toString());
			System.out.println(uri.getScheme());
			System.out.println(uri2.getScheme());
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}
