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
			
			
			System.out.println("hieronder de string van de cosine: " + input+ " en " +gameinDatabase );
			System.out.println("percentage1: "+cosine.apply(input, gameinDatabase));	
			percentage =  cosine.apply(input, gameinDatabase);
			
			
			if (gameinDatabase.contains(input)) {
				System.out.println("dannymessage: input contains gameindatabase!");
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
