package com.reviewscraper.games.service;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import com.reviewscraper.exceptionhandelers.GameNotTheSameException;
import com.reviewscraper.games.dao.IGameDAO;
import com.reviewscraper.games.dao.IReviewDAO;
import com.reviewscraper.games.gamestringfixer.GameStringFixer;
import com.reviewscraper.games.models.Game;
import com.reviewscraper.games.models.Review;





@Service
public class ScrapeService implements IScrapeService , Runnable {



	//thread specific variables
	private static List<Review> deReviews = new ArrayList<Review>();

	private String treadSearchString; 
	private String treadName;
	private Game treadGame;
	private String treadDeReviewSite;
	//end trhead specific variables



	private int maxTries = 0;

	private String zoekStringZonderPlusjes;

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


	public ScrapeService () {
		super();
	}

	public ScrapeService (String treadSearchString, Review treadReview, Game treadGame,	String treadDeReviewSite, String treadName) {
		this.treadSearchString = treadSearchString;
		this.treadGame = treadGame;
		this.treadDeReviewSite = treadDeReviewSite;
		this.treadName = treadName;
	}



	public String getScrapeService(String searchString) {
		deReviews.clear();
		
		System.out.println("getScrapeservice wordt gestart!!!");


		List<Game> allGamesInDatabase =  this.igameDAO.findAll();
		String foundGame = new String();;
		boolean isgameinDatabase = false;

		String origineleZoekString = searchString;
		zoekStringZonderPlusjes = origineleZoekString.replace("+", " ");

		origineleZoekString = origineleZoekString.trim();

		String zoekGamename = origineleZoekString.replace(" ", "+");

		GameStringFixer gamestringfixer = new GameStringFixer();

		//List<Game> gevondenGames = searchGamesInDatabase(allGamesInDatabase, gamepie -> gamestringfixer.fixSearchString(zoekStringZonderPlusjes, gamepie.getGameTitle()));
		List<Game> gevondenGames = searchGamesInDatabase(allGamesInDatabase, gamepie -> gamestringfixer.getAdvancedStringFixer(zoekStringZonderPlusjes, gamepie.getGameTitle()));
		System.out.println("lambda met de gevonden games: " + gevondenGames);


		for (Game gameInDatabase : allGamesInDatabase) {
			System.out.println("voor elke game word geprint:" + gameInDatabase.getGameTitle());
			//gameInDatabase.getGameTitle().contains(searchString)
			try {
				if (gamestringfixer.fixSearchString(zoekStringZonderPlusjes, gameInDatabase.getGameTitle())) {
					System.out.println("gameInDatabase: de gametitel = " + gameInDatabase.getGameTitle());
					foundGame = gameInDatabase.getGameTitle();
					isgameinDatabase = true;
					break;
				}
			} catch (Exception ex) {

			}

		} //end for loop


		if (isgameinDatabase == false) {
			//dit stuk creerd een nieuwe review die gescraped word van het internet

			//List<Review> deReviews = new ArrayList<Review>();

			Game nieuweGame = new Game();
			//nieuweGame.setGameTitle(origineleZoekString);
			//nieuweGame.setGameStudio("nog niet implemented");
			//nieuweGame.setReleaseDate("nog niet implemented");

			try {
				
			
				Review reviewGameinformer = this.getgoogleSearch(zoekGamename, new Review(), nieuweGame, "gameinformer");
				deReviews.add(reviewGameinformer);


				//ScrapeService service = new ScrapeService();
				String [] reviewSites = {"ign", "gamespot", "gamesradar", "insidegamer", "powerunlimited", "xgn", "gamer.nl", "levelup.com",  "gameplanet", "destructoid", "impulsegamer" };

				long start = System.currentTimeMillis();

				int naamTread = 0;
				List<Thread> treads = new ArrayList<Thread>();
				for (String site : reviewSites) {
					Thread service1 = new Thread(new ScrapeService(nieuweGame.getGameTitle(), new Review(), nieuweGame, site, "naamtread: " + naamTread) , ("naamtread = " + naamTread));
					service1.setName(("naamtread = " + naamTread));
					naamTread++;
					treads.add(service1);
					System.out.println("op creatie word deze tread gemaakt met de naam:" + service1.getName());
				}
	
			
			for (Thread treddie : treads) {
				treddie.run();
			}
			
			for (Thread treddie : treads) {
				try {
					treddie.join();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					
				}
			}

	
				long end = System.currentTimeMillis();
				System.out.println("Took : " + ((end - start) / 1000));

				//deReviews.add(this.getgoogleSearch(zoekGamename, new Review(), nieuweGame, "ign"));
				System.out.println("nieuwegame.gametitle is: " + nieuweGame.getGameTitle());

				System.out.println("de reviews static: " + deReviews);

				nieuweGame.setAvgScore(getScoreTypeFromReviews(deReviews, nieuweGame));

				nieuweGame.setReviews(deReviews);

				igameService.create(nieuweGame);     					//dit is de methodcall waarbij daadwerkelijk de game word toegevoegd aan de database

				for (Review reviewinList : deReviews) {
					this.create(reviewinList);					
				}

				//hier word de review daadwerkelijk aan de database toegevoegd
				System.out.println("review word created!");


				return nieuweGame.getGameTitle();

			}  catch (GameNotTheSameException ex) {
				ex.printStackTrace();
				return "no Game found";

			}


		} else { //end if game is in database
			//dit stuk zoekt in de database naar de al bekende reviews
			System.out.println("wollah hij staat er al in pik!!");
			return foundGame;

		} // end else

		


	} //end main



	public List<Game> searchGamesInDatabase (List<Game> deSpellen, Predicate<Game> checker) {
		List<Game> deSpelleninDatabasegevonden = new ArrayList<Game>();
		for (Game eenSpel : deSpellen) {
			if (checker.test(eenSpel))
				deSpelleninDatabasegevonden.add(eenSpel);
		}
		return deSpelleninDatabasegevonden;
	}

	public double getScoreTypeFromReviews (List<Review> deReviews, Game game) {
		List<Double> deScores = new ArrayList<Double>();
		double deScore = 0;
		double topScore =0;
		double lowestScore=10;

		for (Review eenReview : deReviews) {
			if (eenReview.getReviewScore() != 0) {
				deScores.add(eenReview.getReviewScore());
				if(eenReview.getReviewScore() > topScore) {
					topScore = eenReview.getReviewScore();
				}
				if(eenReview.getReviewScore() < lowestScore) {
					lowestScore = eenReview.getReviewScore();
				}
			}
		} //end for loop

		for (double eenScore : deScores) {
			deScore += eenScore;

		}
		DecimalFormat df2 = new DecimalFormat(".##");
		String deScoretijdelijk =  df2.format((deScore/deScores.size()));
		deScore = Double.parseDouble(deScoretijdelijk);
		game.setTopScore(topScore);
		game.setLowestScore(lowestScore);
		return deScore;
	}


	public String getInputOfUser () {

		return zoekStringZonderPlusjes;
	}







	public Review getgoogleSearch (String searchString, Review review, Game game, String deReviewSite) throws GameNotTheSameException {
		String reviewScoreOutput = new String();


		String url = "https://www.google.nl/search?q=" + searchString + "+"  + "review+" + deReviewSite + "+game";
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


			if (delinksfromGoogle.isEmpty() && maxTries < 6) {
				maxTries++;
				getgoogleSearch(searchString, review, game, deReviewSite);

			} else {
				maxTries = 0;
			}

			for (String meegeleverd : delinksfromGoogle) {

				try {
					reviewScoreOutput = this.getSiteReview(meegeleverd, review, deReviewSite, game, searchString); //en voor de andere sites? 

					if (reviewScoreOutput.equals("null")) {
						System.out.println("de GameNotTheSameException is trowns!!!");
						throw new GameNotTheSameException();

					}
				} catch (GameNotTheSameException ex) {
					throw new GameNotTheSameException();
				}
				catch (Exception ex) {

				}


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
		catch (GameNotTheSameException ex) {
			throw new GameNotTheSameException();
		}

		catch (NullPointerException ex) {
			ex.getMessage();
		}


		catch (Exception ex) {

			System.out.println("helaaas getgoogleSearch is vastgelopen!");
			ex.printStackTrace();


		} //end catch


		review.setGame(game);




		return review;



	}




	public String getSiteReview(String degameString, Review review, String reviewSite, Game game, String origineleZoekTerm) {
		String dereturnScore = new String();



		if (reviewSite.equals("gameinformer")) {
			dereturnScore = this.getGameinformerReview(degameString, review, game, origineleZoekTerm);

		} else if (reviewSite.equals("ign")) {
			dereturnScore = this.getIGNReview(degameString, review, game);

		} else if (reviewSite.equals("gamespot")) {
			dereturnScore = this.getGamespotReview(degameString, review, game);

		} else if (reviewSite.equals("gamesradar")) {
			dereturnScore = this.getGamesradarReview(degameString, review, game);

		} else if (reviewSite.equals("insidegamer")) {
			dereturnScore = this.getInsidegamerReview(degameString, review, game);

		} 
		else if (reviewSite.equals("powerunlimited")) {
			dereturnScore = this.getPowerUnlimitedReview(degameString, review, game);

		} 
		else if (reviewSite.equals("xgn")) {
			dereturnScore = this.getXGNReview(degameString, review, game);

		} 
		else if (reviewSite.equals("gamer.nl")) {
			dereturnScore = this.getGamerNL(degameString, review, game);

		} 
		else if (reviewSite.equals("levelup.com")) {
			dereturnScore = this.getlevelUP(degameString, review, game);

		} else if (reviewSite.equals("gameplanet")) {
			dereturnScore = this.getGameplanetReview(degameString, review, game);

		} else if (reviewSite.equals("destructoid")) {
			dereturnScore = this.getDestructoidReview(degameString, review, game);

		} else if (reviewSite.equals("impulsegamer")) {
			dereturnScore = this.getImpulsegamer(degameString, review, game);

		} 





		/* 

		 else if (reviewSite.equals("gameinformer")) {
			 this.getGameinformerReview();

		 }

		 */





		return dereturnScore;



	} //end siteReview



	public String getGameinformerReview (String searchString, Review review, Game game, String origineleZoekTerm) {

		String dereturnStringGameinformer = new String();

		review.setWebsiteName("Game Informer");

		if (!searchString.contains("gameinformer")) {
			return null;
		}

		try {
			Document doc = Jsoup.connect(searchString).get();
			String title = doc.title();
			System.out.println(title);

			//Element linkReviewSite = doc.select("div.review-summary-score").first();
			System.out.println("dereturnString gameinformer gaat nu beginnen!");
			dereturnStringGameinformer = doc.select("div.review-summary-score").text();
			System.out.println("Uhm hier zou de review score geparsed moeten worden: " + dereturnStringGameinformer);

			dereturnStringGameinformer = dereturnStringGameinformer.replaceAll("[^0-9.]", "");
			System.out.println(dereturnStringGameinformer);
			System.out.println("dannymessage: dit is de review! " +  dereturnStringGameinformer);

			System.out.println("dannymessage net foor de set vanaf dennis zn stuk (first().OwnText()!!");
			String gameStudio = new String();
			if(doc.select("div.game-details-developer").first().ownText() != null) {
				gameStudio = doc.select("div.game-details-developer").first().ownText();}
			else {
				gameStudio = doc.select("div.game-details-publisher").first().ownText();	
			}
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
			String gameTitle = doc.select("h1.page-title").first().text();
			System.out.println("Titel doet ie nog");


			GameStringFixer controle = new GameStringFixer();
			System.out.println("dannymessage: net voor de controle isgamehetzelfde");
			
			//nieuwe code:
			try {
			controle.getAdvancedStringFixer(origineleZoekTerm, gameTitle);
			} catch (Exception ex) {
				System.out.println("boeie je code heeft een minifout!");
			}
			//end nieuwe code
			
			
			
			boolean isGameWelEchtHetZelfde = controle.fixSearchString(origineleZoekTerm, gameTitle);
			System.out.println("vanaf hier doet hij de controle of het niet gewoon bullshit is: " + isGameWelEchtHetZelfde);
			if (!isGameWelEchtHetZelfde) {
				System.out.println("dannymessage: JA HIJ IS ECHT FALSE HOOR");
				return "null";
			}


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

		if (!searchString.contains("ign")) {
			return null;
		}


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

		review.setWebsiteName("GameSpot");

		if (!searchString.contains("gamespot")) {
			return null;
		}

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
			if (!searchString.contains("GamesRadar+")) {
				return null;
			}
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

		review.setWebsiteName("InsideGamer");

		if (!searchString.contains("insidegamer")) {
			return null;
		}


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

	}//end getignReview

	public String getPowerUnlimitedReview (String searchString, Review review, Game game) {

		String dereturnStringPowerUnlimited = new String();

		review.setWebsiteName("PowerUnlimited");

		if (!searchString.contains("pu")) {
			return null;
		}

		try {
			Document puDoc = Jsoup.connect(searchString).get();
			String puScoreString = puDoc.select("div.score").first().text();
			puScoreString = puScoreString.replaceAll("SCORE: ", "");
			Double puScore = Double.parseDouble(puScoreString)/10;
			dereturnStringPowerUnlimited = puScore.toString();
			System.out.println("PowerUnlimited review score is " + puScore);

		} catch (IOException e) {
			System.out.println("could not find review from Power Unlimited");

		}


		return dereturnStringPowerUnlimited; 

	}//end gettingReviewPU

	public String getXGNReview (String searchString, Review review, Game game) {

		String dereturnStringXGN = new String();

		review.setWebsiteName("XGN");
		if (!searchString.contains("xgn")) {
			return null;
		}
		try {
			Document xgnDoc = Jsoup.connect(searchString).get();
			String xgnscoreS = xgnDoc.select("input.knob-rating").val();
			Double xgnScore = Double.parseDouble(xgnscoreS);
			dereturnStringXGN = xgnScore.toString();
			System.out.println("xgn review score is " + xgnScore);

		} catch (IOException e) {
			System.out.println("could not find review from XGN");

		}


		return dereturnStringXGN; 

	} //end of xgnReview

	public String getGamerNL(String searchString, Review review, Game game) {

		String dereturnStringGNL = new String();

		review.setWebsiteName("Gamer NL");

		if (!searchString.contains("gamer.nl")) {
			return null;
		}



		try {
			Document gNLDoc = Jsoup.connect(searchString).get();
			String gNLScoreString = gNLDoc.select("div.rs-review--score").first().child(0).attr("alt");
			gNLScoreString = gNLScoreString.replaceAll("Score: ", "");
			Double gNLScore = Double.parseDouble(gNLScoreString)/10;	
			dereturnStringGNL = gNLScore.toString();
			System.out.println("GamerNL review score is " + gNLScore);

		} catch (IOException e) {
			System.out.println("could not find review from Gamer NL");

		}


		return dereturnStringGNL; 

	} //end of GamerNLReview

	public String getlevelUP(String searchString, Review review, Game game) {

		String dereturnStringLUP = new String();

		review.setWebsiteName("LevelUp");
		if (!searchString.contains("levelup")) {
			return null;
		}

		try {
			Document levelUpDoc = Jsoup.connect(searchString).get();
			String levelUpscoreS = levelUpDoc.select("canvas#canvas.rating_container.rating").first().attr("rating");
			Double levelUpScore = Double.parseDouble(levelUpscoreS);
			System.out.println("LevelUP review score is " + levelUpScore);
			dereturnStringLUP = levelUpScore.toString();



		} catch (IOException e) {
			System.out.println("could not find review from Gamer NL");

		} catch (Exception ex) {

		}


		return dereturnStringLUP; 

	} //end of GamerNLReview



	public String getGameplanetReview (String searchString, Review review, Game game) {

		String dereturnGameplanet = new String();

		review.setWebsiteName("Gameplanet");

		if (!searchString.contains("gameplanet")) {
			return null;
		}
		try {
			Document gPlanetDoc = Jsoup.connect(searchString).get();
			String gPlanetscoreS = gPlanetDoc.select("span.numerator").first().text();
			Double gPlanetScore = Double.parseDouble(gPlanetscoreS);
			System.out.println("GamePlanet review score is " + gPlanetScore);
			dereturnGameplanet =  gPlanetScore.toString();
		} catch (IOException e) {
			System.out.println("could not find review from gameplanet");

		} 

		return dereturnGameplanet;

	} //end getgameplanetReview


	public String getDestructoidReview (String searchString, Review review, Game game) {

		String destructoidToString = new String();

		review.setWebsiteName("Destructoid");
		if (!searchString.contains("destructoid")) {
			return null;
		}

		try {
			Document destructoidDoc = Jsoup.connect(searchString).get();
			String destructoidscoreS = destructoidDoc.select("div.gscore").first().text();
			Double destructoidScore = Double.parseDouble(destructoidscoreS);
			destructoidToString = destructoidScore.toString();

		} catch (IOException e) {
			System.out.println("could not find review from destructoid");

		} 

		return destructoidToString;

	} //end getDestructoidReview


	public String getImpulsegamer (String searchString, Review review, Game game) {

		String impulseToString = new String();

		review.setWebsiteName("Impulse Gamer");
		if (!searchString.contains("impulsegamer")) {
			return null;
		}

		try {
			Document impulseDoc = Jsoup.connect(searchString).get();
			String impulsescoreS = impulseDoc.select("div#omc-criteria-final-score").first().text().replaceAll("[^\\.0123456789]","");
			Double impulseScore = Double.parseDouble(impulsescoreS)*2;
			impulseToString = impulseScore.toString();


		} catch (IOException e) {
			System.out.println("could not find review from Impulsegamer");

		} 

		return impulseToString;

	} //end getgameplanetReview

	@Override
	public void run() {
		// TODO Auto-generated method stub

		System.out.println("test1: nu runt tread: " + treadName);

		try {

			System.out.println("test2: nu runt tread: " + treadName);

			Review review = this.getgoogleSearch(treadSearchString, new Review(), treadGame, treadDeReviewSite);
			deReviews.add(review);

			System.out.println("test3: nu runt tread: " + treadName);

		} catch (GameNotTheSameException ex) {
			ex.printStackTrace();
			System.out.println("game not found");
		}

		System.out.println("test4: nu runt tread en is klaar!: " + treadName);


	}








} //end class
