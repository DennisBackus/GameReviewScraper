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
			
			String gameversioninput = input.replaceAll("[^\\.0123456789]","");
			String gameversiongameinDatabase = gameinDatabase.replaceAll("[^\\.0123456789]","");
			
			if ((gameversioninput.equals(gameversiongameinDatabase) || gameversioninput.isEmpty() ) == false) {
				System.out.println("andere versie van game!");
				return false;
			}
			
			

			String inputZonderSpatieofStreep = input.replaceAll("[- ]", "");
			String gameinDatabaseZonderSpatieofStreep = gameinDatabase.replaceAll("[- ]", "");
			
			
			System.out.println("hieronder de string van de cosine: " + inputZonderSpatieofStreep+ " en " +gameinDatabaseZonderSpatieofStreep );
			System.out.println("percentage1: "+cosine.apply(inputZonderSpatieofStreep, gameinDatabaseZonderSpatieofStreep));	
			percentage =  cosine.apply(inputZonderSpatieofStreep, gameinDatabaseZonderSpatieofStreep);
			
			
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
	}
	
	
	
}
