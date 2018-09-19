package com.reviewscraper.games.service;

import java.io.IOException;
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
import com.reviewscraper.games.gamestringfixer.GameStringFixer;
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
		System.out.println("getScrapeservice wordt gestart!!!");


		List<Game> allGamesInDatabase =  this.igameDAO.findAll();
		String foundGame = new String();;
		boolean isgameinDatabase = false;

		String origineleZoekString = searchString;
		String zoekStringZonderPlusjes = origineleZoekString.replace("+", " ");

		origineleZoekString = origineleZoekString.trim();

		String zoekGamename = origineleZoekString.replace(" ", "+");

		for (Game gameInDatabase : allGamesInDatabase) {
			System.out.println("voor elke game word geprint:" + gameInDatabase.getGameTitle());
			//gameInDatabase.getGameTitle().contains(searchString)

			GameStringFixer gamestringfixer = new GameStringFixer();
			try {
				if (gamestringfixer.fixSearchString(zoekStringZonderPlusjes, gameInDatabase.getGameTitle())) {
					System.out.println("gameInDatabase: de gametitel = " + gameInDatabase.getGameTitle());
					foundGame = gameInDatabase.getGameTitle();
					isgameinDatabase = true;
					break;
				}
			} catch (Exception ex) {

			}

		}


		if (isgameinDatabase == false) {
			//dit stuk creerd een nieuwe review die gescraped word van het internet

			List<Review> deReviews = new ArrayList<Review>();

			Game nieuweGame = new Game();
			//nieuweGame.setGameTitle(origineleZoekString);
			//nieuweGame.setGameStudio("nog niet implemented");
			//nieuweGame.setReleaseDate("nog niet implemented");


			//deReviews.add
			Review reviewGameinformer = this.getgoogleSearch(zoekGamename, new Review(), nieuweGame, "gameinformer");
			deReviews.add(reviewGameinformer);

			Review reviewIGN = this.getgoogleSearch(zoekGamename, new Review(), nieuweGame, "ign");
			deReviews.add(reviewIGN);

			Review reviewGamespot = this.getgoogleSearch(zoekGamename, new Review(), nieuweGame, "gamespot");
			deReviews.add(reviewGamespot);

			Review reviewGamesradar = this.getgoogleSearch(zoekGamename, new Review(), nieuweGame, "gamesradar");
			deReviews.add(reviewGamesradar);
			
			Review reviewInsidegamer = this.getgoogleSearch(zoekGamename, new Review(), nieuweGame, "insidegamer");
			deReviews.add(reviewInsidegamer);
			
			

			//deReviews.add(this.getgoogleSearch(zoekGamename, new Review(), nieuweGame, "ign"));
			System.out.println("nieuwegame.gametitle is: " + nieuweGame.getGameTitle());


			nieuweGame.setReviews(deReviews);

			igameService.create(nieuweGame);     					//dit is de methodcall waarbij daadwerkelijk de game word toegevoegd aan de database

			for (Review reviewinList : deReviews) {
				this.create(reviewinList);					
			}

			//hier word de review daadwerkelijk aan de database toegevoegd
			System.out.println("review word created!");


			return nieuweGame.getGameTitle();


		} else { //end if game is in database
			//dit stuk zoekt in de database naar de al bekende reviews
			System.out.println("wollah hij staat er al in pik!!");
			return foundGame;

		} // end else




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
			
			if (delinksfromGoogle.isEmpty()) {
				getgoogleSearch(searchString, review, game, deReviewSite);
			}

			for (String meegeleverd : delinksfromGoogle) {

				reviewScoreOutput = this.getSiteReview(meegeleverd, review, deReviewSite, game); //en voor de andere sites? 
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

			System.out.println("helaaas getgoogleSearch is vastgelopen!");
			
			
		} //end catch


		review.setGame(game);




		return review;



	}




	public String getSiteReview(String degameString, Review review, String reviewSite, Game game) {
		String dereturnScore = new String();


		if (reviewSite.equals("gameinformer")) {
			dereturnScore = this.getGameinformerReview(degameString, review, game);

		} else if (reviewSite.equals("ign")) {
			dereturnScore = this.getIGNReview(degameString, review, game);

		} else if (reviewSite.equals("gamespot")) {
			dereturnScore = this.getGamespotReview(degameString, review, game);

		} else if (reviewSite.equals("gamesradar")) {
			dereturnScore = this.getGamesradarReview(degameString, review, game);

		} else if (reviewSite.equals("insidegamer")) {
			dereturnScore = this.getInsidegamerReview(degameString, review, game);

		} 

		



		/* 

		 else if (reviewSite.equals("gameinformer")) {
			 this.getGameinformerReview();

		 }

		 */





		return dereturnScore;



	} //end siteReview



	public String getGameinformerReview (String searchString, Review review, Game game) {

		String dereturnStringGameinformer = new String();

		review.setWebsiteName("Gameinformer");

		try {
			Document doc = Jsoup.connect(searchString).get();
			String title = doc.title();
			System.out.println(title);

			//Element linkReviewSite = doc.select("div.review-summary-score").first();

			dereturnStringGameinformer = doc.select("div.review-summary-score").text();


			dereturnStringGameinformer = dereturnStringGameinformer.replaceAll("[^0-9.]", "");
			System.out.println(dereturnStringGameinformer);
			System.out.println("dannymessage: dit is de review! " +  dereturnStringGameinformer);

			System.out.println("dannymessage net foor de set vanaf dennis zn stuk (first().OwnText()!!");

			String gameStudio = doc.select("div.game-details-publisher").first().ownText();
			System.out.println("Studio doet ie nog");
			// String gameReleaseDatet = doc.select("div.game-details-release").first().select("time").first().text();
			//    System.out.println(gameReleaseDatet);
			String gameReleaseDate = new String();
			try {
				gameReleaseDate = doc.select("div.game-details-release").first().select("time").first().ownText();
			} catch (NullPointerException ex) {
				gameReleaseDate = "not found";
			}
			System.out.println("Date doet ie nog");
			String gameTitle = doc.select("h1.page-title").first().text().toLowerCase();
			System.out.println("Titel doet ie nog");

			System.out.println("dannymessage net foor de set gametitle!!");
			game.setGameTitle(gameTitle);
			game.setGameStudio(gameStudio);
			game.setReleaseDate(gameReleaseDate);

			System.out.println("danny message: gamtitle = " + game.getGameTitle());
			System.out.println("danny message: gamStudio = " + game.getGameStudio());



		}
		catch (Exception ex) {

			System.out.println("helaaas getGameinformerReview is ergens vastgelopen!");
		}

		return dereturnStringGameinformer;


	} //end getgameinformerReview


	public String getIGNReview (String searchString, Review review, Game game) {

		String dereturnStringIGN = new String();

		review.setWebsiteName("IGN");

		Document ignDoc;
		try {
			ignDoc = Jsoup.connect(searchString).get();
			dereturnStringIGN = ignDoc.select("span.score").first().text();
			//Double ignScore = Double.parseDouble(ignScoreS);	
			System.out.println("ign review score is " + dereturnStringIGN);
		} catch (IOException e) {
			System.out.println("could not find review from IGN");

		}

		return dereturnStringIGN;

	} //end getignReview



	public String getGamespotReview (String searchString, Review review, Game game) {

		String dereturnStringIGN = new String();

		review.setWebsiteName("Gamespot");


		try {
			Document gsDoc = Jsoup.connect(searchString).get();
			String gsScoreString = gsDoc.select("div.gs-score__cell").first().text();
			Double gsScore = Double.parseDouble(gsScoreString);	
			System.out.println("Gamespot review score is " + gsScore);
			dereturnStringIGN =  gsScore.toString();
		} catch (IOException e) {
			System.out.println("could not find review from Gamespot");

		} 

		return dereturnStringIGN;

	} //end getignReview

	public String getGamesradarReview (String searchString, Review review, Game game) {

		String dereturnStringGamesradar = new String();

		review.setWebsiteName("Gamesradar");


		try {
		Document grDoc = Jsoup.connect(searchString).get();
		String grScoreString = grDoc.select("span.score.score-long").first().text();
		Double grScore = Double.parseDouble(grScoreString) * 2;
		dereturnStringGamesradar =  grScore.toString();
		System.out.println("GamesRader review score is " + grScore);
		} catch (IOException e) {
			System.out.println("could not find review from Gamesradar");

		} 
		

		return dereturnStringGamesradar;

	} //end getignReview


	public String getInsidegamerReview (String searchString, Review review, Game game) {

		String dereturnStringInsidegamer = new String();

		review.setWebsiteName("insidegamer");

		try {
		Document igDoc = Jsoup.connect(searchString).get();
		String igScoreString = igDoc.select("div.rating__value").first().text();
		igScoreString = igScoreString.replaceAll(",", ".");
		Double igScore = Double.parseDouble(igScoreString);	
		dereturnStringInsidegamer =  igScore.toString();
		System.out.println("InsideGamer review score is " + igScore);
		
		} catch (IOException e) {
			System.out.println("could not find review from Insidegamer");

		}
		

		return dereturnStringInsidegamer;

	} //end getignReview






} //end class
