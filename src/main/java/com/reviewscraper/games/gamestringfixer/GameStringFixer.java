package com.reviewscraper.games.gamestringfixer;

import org.apache.commons.lang3.*;
import org.apache.commons.text.similarity.CosineDistance;
import org.apache.commons.text.similarity.JaroWinklerDistance;


public class GameStringFixer {

	public boolean fixSearchString (String input, String gameinDatabase) {
		double percentage = 0;
		
	
		try {
		
			JaroWinklerDistance cosine = new JaroWinklerDistance();
			
			System.out.println("hieronder de string van de cosine: " + input+ " en " +gameinDatabase );
			System.out.println(cosine.apply(input, gameinDatabase));	
			percentage =  cosine.apply(input, gameinDatabase);
			
			
	// double distance = //StringUtils.getJaroWinklerDistance(input, gameinDatabase);
		
		//System.out.println("de score is " + score);
		//System.out.println("int i = " + i);
		
		} catch (NullPointerException ex) {
			System.out.println("de laatste en die is nul lollollol");
		}
		
		
		if (percentage > 0.8) {
			return true;
		} else {
		
		return false;
		}
	}
	
	
	
}
