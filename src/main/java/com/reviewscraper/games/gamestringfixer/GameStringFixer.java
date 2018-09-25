package com.reviewscraper.games.gamestringfixer;

import org.apache.commons.lang3.*;
import org.apache.commons.text.similarity.CosineDistance;
import org.apache.commons.text.similarity.JaroWinklerDistance;
import org.apache.commons.text.similarity.LevenshteinDistance;


public class GameStringFixer {

	public boolean fixSearchString (String input, String gameinDatabase) {
		double percentage = 0;
		double percentage2 = 0;
	
		try {
		
			JaroWinklerDistance cosine = new JaroWinklerDistance();
			
			
			String gameversioninputlowecase = input.toLowerCase();
			String gameversiongameinDatabaselowecase = gameinDatabase.toLowerCase();
			
			
			String gameversioninputfixiiior3 = gameversioninputlowecase.replace("iii", "3");
			String gameversiongameinDatabasefixiiior3 = gameversiongameinDatabaselowecase.replace("iii", "3");
			
			gameversioninputfixiiior3 = gameversioninputfixiiior3.replace("ii", "2");
			gameversiongameinDatabasefixiiior3 = gameversiongameinDatabasefixiiior3.replace("ii", "2");
			
			gameversioninputfixiiior3 = gameversioninputfixiiior3.replace(" i", "1");
			gameversiongameinDatabasefixiiior3 = gameversiongameinDatabasefixiiior3.replace(" i", "1");
			
			
			String gameversioninput = gameversioninputfixiiior3.replaceAll("[^\\.0123456789]","");
			String gameversiongameinDatabase = gameversiongameinDatabasefixiiior3.replaceAll("[^\\.0123456789]","");
			
			if ((gameversioninput.equals(gameversiongameinDatabase) || gameversioninput.isEmpty() ) == false) {
				if (gameversioninput.equals("1") && ((gameversiongameinDatabase.isEmpty() || gameversiongameinDatabase.equals("") ) || gameversiongameinDatabasefixiiior3.contains("remastered")  )  ) {
					System.out.println("eerste versie van de game!");
					
				} else {
				System.out.println("andere versie van game!");
				return false;
				}
			}
			
			

			String inputZonderSpatieofStreep = input.replaceAll("[- +]", "").toLowerCase();
			String gameinDatabaseZonderSpatieofStreep = gameinDatabase.replaceAll("[- +]", "").toLowerCase();
			gameinDatabaseZonderSpatieofStreep = gameinDatabaseZonderSpatieofStreep.replace('é', 'e');
			inputZonderSpatieofStreep = inputZonderSpatieofStreep.replace('é', 'e');
			
			StringBuilder convertdatabaseGameName = new StringBuilder(gameinDatabaseZonderSpatieofStreep);
			String inputSubstring = new StringBuilder(inputZonderSpatieofStreep).substring(0, 3);
			System.out.println("inputsubstring :  " + inputSubstring);
			
			int substringLocation = gameinDatabaseZonderSpatieofStreep.indexOf(inputSubstring);
			System.out.println("substringLocation: " + substringLocation);
			String convertdatabaseGameNameString = convertdatabaseGameName.substring(substringLocation , substringLocation + (inputZonderSpatieofStreep.length()));
			
			System.out.println("converteddatabaseGameName " + convertdatabaseGameNameString);
			gameinDatabaseZonderSpatieofStreep = convertdatabaseGameNameString;
			
				
		
			
			
			System.out.println("hieronder de string van de cosine: " + inputZonderSpatieofStreep+ " en " +gameinDatabaseZonderSpatieofStreep );
			percentage =  cosine.apply(inputZonderSpatieofStreep, gameinDatabaseZonderSpatieofStreep);
			System.out.println("percentage1: "+percentage);
			
			System.out.println("dit is de test om te kijken of de input in de gamedatabase voorkomt!!!");
			System.out.println("input: " + inputZonderSpatieofStreep + " gamedatabse: " + gameinDatabaseZonderSpatieofStreep);
			if (gameinDatabaseZonderSpatieofStreep.contains(inputZonderSpatieofStreep)) {
				System.out.println("dannymessage: input contains gameindatabase!");
				System.out.println("in de test of hij uberhaupt erin voor komt!!!");
				return true;
			}
			
			
			
			
			//we gaan kijken naar de docs voor apachi commons om bij de jarowinkler te kijken of iets matches
			//https://commons.apache.org/proper/commons-text/apidocs/index.html
			
			
			// double distance = //StringUtils.getJaroWinklerDistance(input, gameinDatabase);
		
		//System.out.println("de score is " + score);
		//System.out.println("int i = " + i);
		
		} catch (NullPointerException ex) {
			System.out.println("de laatste en die is nul lollollol");
		} catch (Exception ex) {
			ex.getMessage();
		}
		
		
		if (percentage > 0.8) {
			return true;
		} else {
		
		return false;
		}
	} //end fix searchstring
	
	public boolean getgameinformerStringFixer (String input, String gameinDatabase) {
		
		
		System.out.println("getgaminformerStringFixer");
		if (this.fixSearchString(input, gameinDatabase)) {				//dit stuk moet uiteindelijk wel flase zijn!
			
			System.out.println("if this.fixersearchstring is true!");
			
			
			StringBuilder inputBuilder = new StringBuilder(inputOrOutputZonderSpatieOfStreep(input).toLowerCase());
			StringBuilder gameinDatabaseBuilder = new StringBuilder(inputOrOutputZonderSpatieOfStreep(gameinDatabase).toLowerCase());
			
			char [] gameInDatabaseChars = new char [gameinDatabaseBuilder.length() + 1];
			
			gameinDatabaseBuilder.getChars(0, gameinDatabaseBuilder.length(), gameInDatabaseChars, 0);
			String tijdelijkeString = "";
			double percentageNu = 0.0;
			for (char x :gameInDatabaseChars) {
				tijdelijkeString = tijdelijkeString + x;
				
				//je moet in elke loop kijken hoeveel procent de string gelijk is met elkaar!
				try {
				System.out.println("this inputzonderspatie of streep input: " + this.inputOrOutputZonderSpatieOfStreep(input));
				System.out.println("this inputzonderspatie of streep tijdelijkeString: " + this.inputOrOutputZonderSpatieOfStreep(tijdelijkeString));
				System.out.println("getsubstringofdatabase: " + this.getSubStringofDataBase(this.inputOrOutputZonderSpatieOfStreep(input), this.inputOrOutputZonderSpatieOfStreep(tijdelijkeString)));
				
				
				double percentagenieuw = this.percentageCalculator(this.inputOrOutputZonderSpatieOfStreep(input.toLowerCase()), this.getSubStringofDataBase(this.inputOrOutputZonderSpatieOfStreep(input.toLowerCase()), this.inputOrOutputZonderSpatieOfStreep(tijdelijkeString.toLowerCase())));
				if (percentagenieuw > percentageNu) {
					percentageNu = percentagenieuw;
				} else if (percentagenieuw < percentageNu) {
					System.out.println("neee hij is kleiner geworden!");
					return false;
				}
				
				System.out.println("dannymessage: percentage nu = " +percentageNu);
				
				} catch (Exception ex) {
					System.out.println("deze string is niet zo goed!!");
					System.out.println("dannymessage: percentage nu = " +percentageNu);
				}
				
				
				
			} //end forloop
			System.out.println("dit zijn de chars: " + tijdelijkeString );
			
			
			
			
			
			return true;
			
			
		} //end if 
		
		
		return false;
	} //end get game informer string
	
	
	 private double percentageCalculator (String input, String datbaseString) {
		 JaroWinklerDistance cosine = new JaroWinklerDistance();
		double percentage = 0.0;
		 
		 System.out.println("hieronder de string van de cosine: " + input+ " en " +datbaseString );
		percentage =  cosine.apply(input, datbaseString);
		System.out.println("percentage1: "+percentage);
		
		
		return percentage;
	} //end percentage calculator
	 
	 
	 private String inputOrOutputZonderSpatieOfStreep (String theString) {
		 
		 	String theStringZonderSpatieofStreep = theString.replaceAll("[- +]", "").toLowerCase();
		 	theStringZonderSpatieofStreep = theString.replace('é', 'e');
		 
		 System.out.println("inputOrOutputZonderSpatieOfStreep: " + theStringZonderSpatieofStreep);
		 return theStringZonderSpatieofStreep;
	 } //end input or output zonderspatie of streep
	 
	
	 private String getSubStringofDataBase (String left, String right) {
		 
		 	System.out.println("getSubStringofDataBase");
			StringBuilder convertdatabaseGameName = new StringBuilder(right.toLowerCase());
			System.out.println("getSubStringofDataBase right: " + right.toLowerCase() );
			String inputSubstring = new StringBuilder(left.toLowerCase()).substring(0, 3);
			System.out.println("inputsubstring :  " + inputSubstring);
			
			
			int substringLocation = right.toLowerCase().indexOf(inputSubstring);
			System.out.println("substringLocation: " + substringLocation);
			String convertdatabaseGameNameString = convertdatabaseGameName.substring(substringLocation , (convertdatabaseGameName.length()));
			
		 System.out.println("getSubStringofDataBase: " + convertdatabaseGameNameString );
		 
		 return convertdatabaseGameNameString;
		 
	 }
	 
	 
	 
	 
	
	
} //end class
