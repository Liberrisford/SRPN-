/**
 * Contains the main method for the SRPN calculator, a program that aims to represent the functionality of the SRPN program found on the legacy system.
 * It is a Reverse Polish Notation calculator with the feature that all arithmetic is saturated, i.e. it stays at the Integer maximum or minuimum value
 * as opposed to wrapping around. 
 * 
 * @author Liam Berrisford
 * @version 1.0
 * @release 01/12/2015
 * @See SRPNStack.java
 */



import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class SRPN {
	public static void main(String[] args) throws IOException {
		
		//Calling the constructor for the SRPNStack class. 
		SRPNStack stack = new SRPNStack();
		String input;
		
		//Defining that the input for the Buffered Reader with be an input stream from the user (user keyboard input).
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		
		//A while loop is used that will keep inputting data into the SRPN as long as the user keeps inputting data into the program.
		while((input=br.readLine()) != null) {
			stack.input(input);
		}
		
		
	}
}
