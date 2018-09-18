package com.reviewscraper.games.service;

import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import com.reviewscraper.games.dao.IGameDAO;
import com.reviewscraper.games.dao.IReviewDAO;
import com.reviewscraper.games.models.Game;
import com.reviewscraper.games.models.Review;





@Service
public class ScrapeService implements IScrapeService {

	

	@Autowired 
	private IReviewDAO iReviewDAO;
	
	@Autowired
	private IGameDAO igameDAO;
	
	@Autowired IGameService igameService;
	
	
	public List<Review> findByGame(Game game) {
		
		return this.iReviewDAO.findByGame(game);
	}
	
	@Override
	public Review create(Review review) {
		Assert.notNull(review, "Address mag niet null zijn");

		return this.iReviewDAO.save(review);
	}
	

	
	public String getScrapeService(String searchString) {
		
		List<Game> allGamesInDatabase =  this.igameDAO.findAll();
		Game foundGame = null;
		boolean isgameinDatabase = false;
		
		String origineleZoekString = searchString;
		
		origineleZoekString = origineleZoekString.trim();
			
		String zoekGamename = origineleZoekString.replace(" ", "+");
		
		for (Game gameInDatabase : allGamesInDatabase) {
			if (searchString.equals(gameInDatabase.getGameTitle())) {
				foundGame = gameInDatabase;
				isgameinDatabase = true;
				break;
			}
		}
		
		
		if (isgameinDatabase == false) {
		//dit stuk creerd een nieuwe review die gescraped word van het internet
			
			List<Review> deReviews = new ArrayList<Review>();
			
			Game nieuweGame = new Game();
			
			Review review = this.getgoogleSearch(zoekGamename, new Review(), nieuweGame, "gameinformer");
			Review reviewIGN = this.getgoogleSearch(zoekGamename, new Review(), nieuweGame, "ign");
			
			
			
			
			
			
			review.setAuthor("Karel");
			review.setGame(nieuweGame);
			review.setWebsiteName("dikkeSwel");
			
			nieuweGame.setGameTitle(origineleZoekString);
			nieuweGame.setGameStudio("nog niet implemented");
			nieuweGame.setReleaseDate("nog niet implemented");
			nieuweGame.setReviews(deReviews);
			
			igameService.create(nieuweGame);     					//dit is de methodcall waarbij daadwerkelijk de game word toegevoegd aan de database
			
			this.create(review);									//hier word de review daadwerkelijk aan de database toegevoegd
			System.out.println("review word created!");
			
			
			
		
		
		} else { //end if game is in database
																	//dit stuk zoekt in de database naar de al bekende reviews
			System.out.println("wollah hij staat er al in pik!!");
			
			
		} // end else
		
		
		return zoekGamename;
		
	} //end main
	
	
	
	
	
	
	
	
	
	
	
	
	
	public Review getgoogleSearch (String searchString, Review review, Game game, String deReviewSite) {
		 String reviewScoreOutput = new String();
		 
		 
		 String url = "https://www.google.nl/search?q=" + searchString + "+"  + "review+" + deReviewSite;
		 System.out.println("de google search url: " + url);
		 
		 	System.out.println("vanaf hier doen we dat google ding!");
			 try {
	         Document docgoogleconnect = Jsoup.connect(url).get();
	         String title = docgoogleconnect.title();
	         System.out.println(title);
	         
	         Elements elementslinksfromGoogle = docgoogleconnect.select("h3.r");
	         System.out.println("de links: \n" + elementslinksfromGoogle);
	      
	         
	         List<String> delinksfromGoogle = elementslinksfromGoogle.select("a").eachAttr("href");
	         System.out.println("delinks: " +delinksfromGoogle);
	         
	         for (String meegeleverd : delinksfromGoogle) {
	        	 
	        	 reviewScoreOutput = this.getSiteReview(meegeleverd, review, "gameinformer");
	        	 System.out.println("elke output2: " + reviewScoreOutput);
	        	 
	        	 if ((reviewScoreOutput.equals(null) == false) && reviewScoreOutput.equals("") == false) {
	        		 try {
	        		 review.setReviewScore(Double.parseDouble(reviewScoreOutput));
	        		 review.setUrl(meegeleverd);
	        		 } catch (Exception ex ) { 
	        			 System.out.println("de review was niet te parsen naar een double");
	        		 }
	        		 System.out.println("dit was de juiste site!");
	        		 break;
	        	 } 					//end if reviewscoreoutout
	        	 System.out.println("niet gevonden!");
	        	 
	         } 					//for each loop meegeleverd
	      
	         
			 } 					//end outter try (docgoogleconnect)
			catch (Exception ex) {
				
				System.out.println("helaaas");
			} //end catch
			
			 return review;
		
		
		
	}
	
	
	
	
	public String getSiteReview(String degameString, Review review, String reviewSite) {
		String destring2 = new String();

		 try {
         Document doc = Jsoup.connect(degameString).get();
         String title = doc.title();
         System.out.println(title);
         
         Element link = doc.select("div.review-summary-score").first();

         destring2 = doc.select("div.review-summary-score").text();
    
         
         destring2 = destring2.replaceAll("[^0-9.]", "");
         System.out.println(destring2);
         System.out.println("dannymessage: dit is de review! " +  destring2);
        
         
         
		 }
		catch (Exception ex) {
			
			System.out.println("helaaas");
		}
		
		 return destring2;
		
		
		
	} //end siteReview
	
	/*
	public Review getGameinformerReview (String searchString, Review review, Game game, String deReviewSite) {
		
		String destring2 = new String();

		
		this.getgoogleSearch(zoekGamename, new Review(), nieuweGame, "gameinformer");
		
		
		
		
		 try {
        Document doc = Jsoup.connect(degameString).get();
        String title = doc.title();
        System.out.println(title);
        
        Element link = doc.select("div.review-summary-score").first();

        destring2 = doc.select("div.review-summary-score").text();
   
        
        destring2 = destring2.replaceAll("[^0-9.]", "");
        System.out.println(destring2);
        System.out.println("dannymessage: dit is de review! " +  destring2);
       
        
		 }
		catch (Exception ex) {
			
			System.out.println("helaaas");
		}
		
		 return destring2;
		
		
	} //end getgameinformerReview
	
	
	*/
	
	
	
	
	
	
}
