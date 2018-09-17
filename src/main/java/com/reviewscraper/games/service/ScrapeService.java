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
	

	
	public void getScrapeService(String searchString) {
		
		List<Game> allGamesInDatabase =  this.igameDAO.findAll();
		Game foundGame = null;
		boolean isgameinDatabase = false;
		//String origineleZoekurl = "darksouls 3";
		
		String origineleZoekurl = searchString;
		
		origineleZoekurl = origineleZoekurl.trim();
			
		String zoekUrl = origineleZoekurl.replace(" ", "+");
		//System.out.println("de return was: " + this.getgoogleSearch(zoekUrl)); 
		
		
		for (Game gameInDatabase : allGamesInDatabase) {
			if (searchString.equals(gameInDatabase.getGameTitle())) {
				foundGame = gameInDatabase;
				isgameinDatabase = true;
				break;
				
			}
			
			
		}
		
		
		if (!isgameinDatabase) {
		//dit stuk creerd een nieuwe review die gescraped word van het internet
			
			
			List<Review> deReviews = new ArrayList<Review>();
			
			Review review = this.getgoogleSearch(zoekUrl, new Review());
			
			Game nieuweGame = new Game();
			review.setAuthor("Karel");
			review.setGame(nieuweGame);
			
			review.setWebsiteName("dikkeSwel");
			
			
	
			nieuweGame.setGameTitle(origineleZoekurl);
			nieuweGame.setGameStudio("nog niet implemented");
			
			nieuweGame.setReleaseDate("nog niet implemented");
			nieuweGame.setReviews(deReviews);
			
			
			igameService.create(nieuweGame);
			
			this.create(review);
			System.out.println("review word created!");
			
			
		
		
		} else { //end if game is in database
		//dit stuk zoekt in de database naar de al bekende reviews
			System.out.println("wollah hij staat er al in pik!!");
			
			
		} // end else
		
	} //end main
	
	
	public Review getgoogleSearch (String searchString, Review review) {
		 String output2 = new String();
		String deReviewSite = "gameinformer";
		 
		 String url2 = "https://www.google.nl/search?q=" + searchString + "+"  + "review+" + deReviewSite;
		 System.out.println("de google search url: " + url2);
		 
		 	System.out.println("vanaf hier doen we dat google ding!");
			 try {
	         Document doc2 = Jsoup.connect(url2).get();
	         String title = doc2.title();
	         System.out.println(title);
	         
	         
	         Element link = doc2.select("h3.r").first();
	         System.out.println("link = : " + link);
	         
	         Element linknummer2 = link.select("a").first();
	         String linkHref = linknummer2.attr("href"); // "http://example.com/"
	         System.out.println("linkHref: "+linkHref);
	        
	         //String output = this.getSiteReview(linkHref, review);
	         
	         Elements links = doc2.select("h3.r");
	         System.out.println("de links: \n" + links);
	         
	         //String linkHref2 = links.select("a").attr("href");
	         //System.out.println("linkhref2: " +linkHref2);
	         
	         List<String> delinks = links.select("a").eachAttr("href");
	         System.out.println("delinks: " +delinks);
	         
	         for (String meegeleverd : delinks) {
	        	 
	        	 output2 = this.getSiteReview(meegeleverd, review);
	        	 System.out.println("elke output2: " + output2);
	        	 
	        	 if ((output2.equals(null) == false) && output2.equals("") == false) {
	        		 try {
	        		 review.setReviewScore(Double.parseDouble(output2));
	        		 review.setUrl(meegeleverd);
	        		 } catch (Exception ex ) { 
	        			 System.out.println("de review was niet te parsen naar een double");
	        		 }
	        		 System.out.println("dit was de juiste site!");
	        		 break;
	        	 }
	        	 System.out.println("niet gevonden!");
	        	 
	         }
	         
	         
	         
	         
	         //return output2;
	         
	         /*
	         String destring = doc2.select("div.review-summary-score").first().text();
	         String destring2 = doc2.select("div.review-summary-score").text();
	         //destring2 = destring2.substring(0, 3);
	         
	         destring2 = destring2.replaceAll("[^0-9.]", "");
	         //System.out.println("de string 2 " + destring2);
	         
	         */
	      
	         
			 }
			catch (Exception ex) {
				
				System.out.println("helaaas");
			}
			
			
		
			 return review;
		
		
		
	}
	
	
	
	
	public String getSiteReview(String degameString, Review review) {
		String destring2 = new String();
		String url = "https://www.gameinformer.com/review/monster-hunter-generations-ultimate/touching-up-the-past";
		//String url = "https://www.gameinformer.com/review/nba-2k19/outworking-and-outplaying-the-competition";
		//String url = "https://www.gameinformer.com/review/spider-man/spinning-an-amazing-web";

		 try {
         Document doc = Jsoup.connect(degameString).get();
         String title = doc.title();
         System.out.println(title);
         
         Element link = doc.select("div.review-summary-score").first();

        // String destring = doc.select("div.review-summary-score").first().text();
         destring2 = doc.select("div.review-summary-score").text();
         //destring2 = destring2.substring(0, 3);
         
         destring2 = destring2.replaceAll("[^0-9.]", "");
         System.out.println(destring2);
         System.out.println("dannymessage: dit is de review! " +  destring2);
        
         
         
		 }
		catch (Exception ex) {
			
			System.out.println("helaaas");
		}
		
		 return destring2;
		
		
		
	}
	
	
	
	
	
	
	
	
}
