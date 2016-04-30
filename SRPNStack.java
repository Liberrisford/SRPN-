/**
 * The class that contains all of the SRPN methods and constructors relevant for the SRPN program to be operational.
 * In the documentation of this program #method is used with the format #method(methodName, parameterDataType).
 * 
 * @author Liam Berrisford
 * @version 1.0
 * @release 01/12/2015
 * @See SRPN.java
 */



import java.lang.Math;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Stack;

public class SRPNStack {
	private Stack<Integer> SRPN;
	private ArrayList<Integer> Random = new ArrayList<Integer>();
	private int randomNumbers = 0;
	
	/**
	 * No parameter Constructor that sets up an empty stack and populates an Array List used for random number generation.
	 * 
	 * @See SRPN.java
	 */
	public SRPNStack() {
		SRPN = new Stack<Integer>();
		randomListPopulate();
	}
	
	/**Method used for comments. This will split the input string into the comments and desired input, and then input the
	 * non-comment into the SRPN calculator.
	 * 
	 * @param userInput - This input has not been modified at all yet and is exactly equal to the user input.
	 * @See SRPN.java
	 */
	public void input(String userInput) {
		if(truthChecker(userInput)) {
			
			//The string is split into sections based on hash tags (the hash tags are not kept).
			String[] commentSplit = userInput.split("#");
			
			//Inputs only the part of the string before a hash tag, if there was one.
			stringSplit(commentSplit[0]);
		}
	}

	/**
	 * We were told to replicate every part of SRPN...
	 * 
	 * @param userInput - The input has not been modified at all yet and is exactly equal to the user input.
	 * @return A boolean as to whether or not the input string was equal to "rachid".
	 * @See #method(input, String)
	 */
	public boolean truthChecker(String userInput) {
		if(userInput.equals("rachid")) {
			System.out.println("Rachid is the best unit lecturer.");
			return false;
		}
		return true;
	}
	
	/**
	 * This method is to split the user input into separate strings, either a single operator (e.g. +) or an operand (e.g. 1234).
	 * 
	 * @param commentedInput - At this stage the users input has been checked for and has had any comments removed.
	 * @See #method(input, String)
	 */
	public void stringSplit(String commentedInput) {
		/**If there is a space in the string then it is split with the space as the delimiter, if it is another operator then 
		*a look around is used as this means that the operator can be kept, and then also input into the SRPN calculator, whereas
		*we do not want to input a space into the SRPN calculator. The string is then also split if there is a - (signifying negative operand),
		*and -- (signifying a negative operand and subtraction operator).
		*/
		String[] result = commentedInput.split("(\\s+)|((?<=\\+)|(?=\\+))|((?<=\\*)|(?=\\*))|((?<=/)|(?=/))|((?<=%)|(?=%))|((?<=\\^)|(?=\\^))|((?<=\\.)|(?=\\.))|((?<=[a-z])|(?=[a-z]))|(?=--)|(?=-)");
		
		ArrayList<String> splitStrings = new ArrayList<String>();
		
		for(int i = 0; i < result.length; i++) {
			//If it is an operator then the exception will be caught on line 76, then the string is added to the tokens Array List
			//otherwise the String (not the Integer.parsedInt value ) is added to the ArrayList.
			try {
				if(saturationInputCheck(result[i])) {
					Integer.parseInt(result[i]); 
					splitStrings.add(result[i]);
				}
			} catch(NumberFormatException nfe) {
				splitStrings.add(result[i]);
			}
		}
		
		//Each individual String in the Array List is then passed to the next method. 
		for(int i = 0; i < splitStrings.size(); i++) {
			//Validation to make sure no strings that are empty are input into the SRPN calculator.
			if (splitStrings.get(i).trim().length() > 0) {
				calculateInt(splitStrings.get(i));
			} 
		}
	}
	
	/**
	 * Method used to check if the initial input value is over the Integer Max/Min value.
	 * 
	 * @param testInput - Testing whether the initial string that is input is over the integer Max/Min.
	 * @return A boolean. If false is returned the value is not input into the stack but just the Max/Min value is instead. 
	 * Otherwise true is returned and the string is not over the Integer Max/Min. 
	 * @See #method(stringSplit, String)
	 */
	public boolean saturationInputCheck(String testInput) {
		//If the string has over 18 character then it may not be able to be stored in a long, and so the Integer Max/Min value
		//is pushed to the stack instead.
		if(testInput.length() > 18) { 
			
			if(testInput.contains("-")) {
				SRPN.push(-2147483648);
			} else {
				SRPN.push(2147483647);
			}
			return false;
		} else {
			
			//If it is below 18 characters then it is parsed to a long and checked whether it is over the Integer Min/Max.
			long input = Long.parseLong(testInput);
			
			if(input > Integer.MAX_VALUE ) {
				SRPN.push(2147483647);
				return false;
			} else if(input < Integer.MIN_VALUE) {
				SRPN.push(-2147483648);
				return false;
			} else {
				return true;
			}
		}
	}
	
	/**
	 * Method used to calculate whether the String is an Integer. It works by trying to parse the String an an
	 * integer if it can then it will check if the stack is full, if not it will be pushed to the stack. 
	 * 
	 * @param splitInput - The last manipulation to the input was for it to be to be split into separate strings.
	 * @See #method(stringSplit, String)
	 */
	public void calculateInt(String splitInput) {
			try {
				int input = Integer.parseInt(splitInput);
				//If it can be parsed to an integer then it may be an octal value and so then a method is called to check this.
				if(octalCheck(splitInput)) {
					if(SRPN.size() >= 23) {
						System.err.println("Stack overflow.");
					} else {
						SRPN.push(input);
					}
				}
			} catch (NumberFormatException nfe) {
				//If the string is not an integer then it may be an one of the non-numeric inputs for the calculator.
				calculateNonNumericString(splitInput);
			}
	}
	
	/**
	 * Method used to check whether the input was an octal value (has a leading zero). if it does then it checks if it has an
	 * 8 or 9, then if it does it splits the string based on the 8 and 9, then only uses the prior part for calculating the decimal
	 * equivalent (the same as srpn.lcpu).
	 * 
	 * @param numericInput - The string passed to this method is known to be numeric, but not whether it is decimal or octal.
	 * @return A boolean. True if the integer is decimal (not octal), and so the string is then input into the stack as 
	 * an integer. False if it was an octal, and the octal decimal value is passed to the stack instead. 
	 * @See #method(calculateInt, String)
	 */
	public boolean octalCheck(String numericInput) {
		if(numericInput.startsWith("0")) {
				if(numericInput.contains("8") || numericInput.contains("9")) {
					String[] inputWithout8 = numericInput.split("8");
					String[] inputWithout9 = inputWithout8[0].split("9");
					octalValueCalculator(inputWithout9[0]);
					return false;
				} else {
					octalValueCalculator(numericInput);
					return false;
				}
		}
		return true;
	}
				

	/**
	 * Method used for calculating the decimal value of an octal input. Done by Multiplying each decimal value (0-7) by 8 
	 * to the exponent equal the positions moved from the right (starting at 0), then finding the total of each individual multiplication. 
	 * 
	 * @param octalInput - The input has been identified as an octal value if passed to this method.
	 * @See #method(octalCheck, String)
	 */
	public void octalValueCalculator(String octalInput) {
		//The string is split into individual chars, and then stored in an array.
		char[] individualIntegers = octalInput.toCharArray();
		
		//The total for the octalValue and the counter for the exponent are declared.
		int octalValue = 0;
		int exponent = 0;
		
		//For loop thats counts back from the last char in the array and multiplies by 8 to the relevant exponent.
		for(int i=((individualIntegers.length)-1); i >= 0; i--) {
			octalValue += (Character.getNumericValue(individualIntegers[i]) * (Math.pow(8, exponent)));
			exponent += 1;
		}
		//The octal value is then checked to make sure it is not over the Integer Max/Min value.
		long octalSaturationValue = Long.parseLong(octalInput);
		if(octalSaturationValue > Integer.MAX_VALUE ) {
			SRPN.push(2147483647);
		} else if(octalSaturationValue < Integer.MIN_VALUE) {
			SRPN.push(-2147483648);
		} else {
			SRPN.push(octalValue);
		}
	}
	
	
	/**
	 * Method used to determine and execute the feature that any of the non operator strings represent (e.g. d), otherwise
	 * pass on a recognized or pass on an unrecognized input.
	 * 
	 * @param nonNumericInput - If the string has been passed to this method then it is known it is a non numeric string (e.g. no 0-9).
	 * @See #method(CalculateInt, String)
	 */
	public void calculateNonNumericString(String nonNumericInput) {
		//If statement to check if string is a recognized input.
		if(nonNumericInput.contains("=") || nonNumericInput.contains("d") || nonNumericInput.contains("r") || nonNumericInput.contains("+") || nonNumericInput.contains("-") || nonNumericInput.contains("/") || nonNumericInput.contains("*") || nonNumericInput.contains("%") || nonNumericInput.contains("^")) {
			
			//It is is recognized then it is checked if it was one of the non operator inputs. 
			if(nonNumericInput.equals("=")) {
				System.out.println("" + SRPN.peek());
			} else if(nonNumericInput.equals("d")) {
				
				//An iterator is used to look at the contents of the stack without removing them. It will individual print the element
				//in the stack as long as there is another element available.
				Iterator<Integer> iterator = SRPN.iterator();
				while((iterator.hasNext())) {
					System.out.println(iterator.next());
				} 
			}else if(nonNumericInput.equals("r")) {
				
					//As the random number is an integer it is checked that there are only a maximum of 23 elements in the stack.
					if(SRPN.size() >= 23) {
						System.err.println("Stack overflow.");
					} else {
						SRPN.push(Random.get(randomNumbers));
						//As there are only 100 numbers in the random  array, 99 is the last element in the Array and so once it is pushed 
						//to the stack then the position in the array is reset to the first. (index 0)
						if(randomNumbers > 98) {
							randomNumbers = 0;
						} else {
							randomNumbers += 1;
						}
					}
			} else {
				//If the string is not one of the non operator input, then it must be an operator input.
				calculateOperators(nonNumericInput);
			}
		} else {
			//Otherwise the unrecognized string is passed to another method.
			unrecognizedInput(nonNumericInput);
		}
	}
	
	/**
	 * Method to split up any unrecognized string and give each individual character back in an error message. 
	 * 
	 * @param illegalInput - If it is is passed to this method then it is an illegal input and should not have been input into the SRPN calculator.
	 * @See #method(calculateNonNumericString, String)
	 */
	public void unrecognizedInput(String illegalInput) {
		//String is put into an array of chars.
		char[] individualCharacters = illegalInput.toCharArray();
		
		//Then each individual char in the array is printed out in an error message.
		for(int i = 0; i < individualCharacters.length; i++) {
			System.err.println("Unrecognised operator or operand \"" + individualCharacters[i] + "\"." );
		}
	}
		
	/**
	 * Method used to calculate which operator has been input. Only inputs that would remove values from the stack are contained 
	 * in this method.
	 * 
	 * @param operatorInput - If the string has been passed to this method then it is known that it a recognized operator.
	 * @See #method(calculateNonNumericString, String)
	 */
	public void calculateOperators(String operatorInput) {
		//Having all of the operators in the same method means that they can be put into a single if statement that will check whether or not
		//there are enough operands in the stack for the operation to be carried out.
		if(underflowCheck()) {
			
		//If there are enough operands in the stack then the operand is calculated and the relevant operation done.
			int operand1 = SRPN.pop();
			int operand2 = SRPN.pop();
			if(operatorInput.equals("+")) {
				
				//When the operation is performed it is changed to a long as if the result of the operation is bigger than the
				//integer Max/Min then the long will be able to store it.
				long saturationCheckValue = (long)operand1 + (long)operand2;
				
				//A method is called where the result is then checked against the integer Max/Min Values, if it is over then Max/Min value then the 
				//Max/Min is assigned instead.
				if(arithmeticSaturationCheck(saturationCheckValue)) {
					
					//Otherwise the result is added to the stack. The same process is used for all the operators.
					SRPN.push((operand1 + operand2));
				}
			} else if(operatorInput.equals("-")) {
				long saturationCheckValue = (long)operand2 - (long)operand1;
				if(arithmeticSaturationCheck(saturationCheckValue)) {
					SRPN.push(operand2 - operand1);
				}
			} else if (operatorInput.equals("/")) {
				//If Statement to stop the program dividing by zero.
				if(operand1 == 0 ) {
					System.err.println("Divide by 0.");
					SRPN.push((int) operand2);
					SRPN.push((int) operand1);
				} else {
					long saturationCheckValue = (long)operand2 / (long)operand1;
					if(arithmeticSaturationCheck(saturationCheckValue)) {
						SRPN.push(operand2 / operand1);
					}
				}
			} else if(operatorInput.equals("*")) {
				long saturationCheckValue = (long)operand1 * (long)operand2;
				if(arithmeticSaturationCheck(saturationCheckValue)){
					SRPN.push(operand1 * operand2);
				}
			} else if(operatorInput.equals("%")) {
				long saturationCheckValue = (long)operand2 % (long)operand1;
				if(arithmeticSaturationCheck(saturationCheckValue)) {
					SRPN.push(operand2 % operand1);
				}
			} else if(operatorInput.equals("^")) {
				long saturationCheckValue = (long) Math.pow(operand2,operand1);
				//If statement to stop the program raising anything to a negative power.
				if(operand1 < 0) {
					System.err.println("Negative power.");
					SRPN.push((int) operand2);
					SRPN.push((int) operand1);
				} else {
					if(arithmeticSaturationCheck(saturationCheckValue)) {
						//As Math.pow uses double the result needs to be changed to an integer before it can be input into the Stack.
						SRPN.push((int) Math.pow(operand2, operand1));
					}
				}
			}
		}
	}
	
	/**
	 * Method that is called to check whether there are enough operands in the stack for the operation to be carried out.
	 * 
	 * @return A boolean. True if the stack has enough operands in for an operation to be carried out. False if there arn't 
	 * enough operands for a operation to be carried out.
	 * @See #method(calculateOperators, String)
	 * 
	 */
	public boolean underflowCheck() {
		if(SRPN.size() < 2) {
			System.err.println("Stack underflow.");
			return false;
		} else {
			return true;
		}
	}
	
	/**
	 * Method that is called to check whether the result of an operation is over the Integer Min/Max value.
	 * 
	 * @param arithmaticOperationValue - Value that is the result of the relevant operation of the two popped integers (e.g. addition).
	 * @return A boolean. True if the result is not over the integer Max/Min value. False if it is over the Max/Min.
	 * @See #method(calculateOperators, String)
	 */
	public boolean arithmeticSaturationCheck(long arithmeticOperationValue) {
		if(arithmeticOperationValue > Integer.MAX_VALUE) {
			SRPN.push(2147483647);
			return false;
		} else if(arithmeticOperationValue < Integer.MIN_VALUE) {
			SRPN.push(-2147483648);
			return false;
		} else {
			return true;
		}
	}
	
	/**
	 * Method used to populate an array with all possible values for the random value in the SRPN calculator (100 possible values).
	 * 
	 * @See #Constructor(SRPNStack)
	 */
	public void randomListPopulate() {
		Random.add(1804289383);
		Random.add(846930886);
		Random.add(1681692777);
		Random.add(1714636915);
		Random.add(1957747793);
		Random.add(424238335);
		Random.add(719885386);
		Random.add(1649760492);
		Random.add(596516649);
		Random.add(1189641421);
		Random.add(1025202362);
		Random.add(1350490027);
		Random.add(783368690);
		Random.add(1102520059);
		Random.add(2044897763);
		Random.add(1967513926);
		Random.add(1365180540);
		Random.add(1540383426);
		Random.add(304089172);
		Random.add(1303455736);
		Random.add(35005211);
		Random.add(521595368);
		Random.add(294702567);
		Random.add(1726956429);
		Random.add(336465782);
		Random.add(861021530);
		Random.add(278722862);
		Random.add(233665123);
		Random.add(2145174067);
		Random.add(468703135);
		Random.add(1101513929);
		Random.add(1801979802);
		Random.add(1315634022);
		Random.add(635723058);
		Random.add(1369133069);
		Random.add(1125898167);
		Random.add(1059961393);
		Random.add(2089018456);
		Random.add(628175011);
		Random.add(1656478042);
		Random.add(1131176229);
		Random.add(1653377373);
		Random.add(859484421);
		Random.add(1914544919);
		Random.add(608413784);
		Random.add(756898537);
		Random.add(1734575198);
		Random.add(1973594324);
		Random.add(149798315);
		Random.add(2038664370);
		Random.add(1129566413);
		Random.add(184803526);
		Random.add(412776091);
		Random.add(1424268980);
		Random.add(1911759956);
		Random.add(749241873);
		Random.add(137806862);
		Random.add(42999170);
		Random.add(982906996);
		Random.add(135497281);
		Random.add(511702305);
		Random.add(2084420925);
		Random.add(1937477084);
		Random.add(1827336327);
		Random.add(572660336);
		Random.add(1159126505);
		Random.add(805750846);
		Random.add(1632621729);
		Random.add(1100661313);
		Random.add(1433925857);
		Random.add(1141616124);
		Random.add(84353895);
		Random.add(939819582);
		Random.add(2001100545);
		Random.add(1998898814);
		Random.add(1548233367);
		Random.add(610515434);
		Random.add(1585990364);
		Random.add(1374344043);
		Random.add(760313750);
		Random.add(1477171087);
		Random.add(356426808);
		Random.add(945117276);
		Random.add(1889947178);
		Random.add(1780695788);
		Random.add(709393584);
		Random.add(491705403);
		Random.add(1918502651);
		Random.add(752392754);
		Random.add(1474612399);
		Random.add(2053999932);
		Random.add(1264095060);
		Random.add(1411549676);
		Random.add(1843993368);
		Random.add(943947739);
		Random.add(1984210012);
		Random.add(855636226);
		Random.add(1749698586);
		Random.add(1469348094);
		Random.add(1956297539);
	}
}

