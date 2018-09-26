package com.reviewscraper.games.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.reviewscraper.games.dto.gameTitleDTO;
import com.reviewscraper.games.dto.getAllGamesInListDTO;
import com.reviewscraper.games.dto.suggestTitleDTO;
import com.reviewscraper.games.gamestringfixer.GameStringFixer;
import com.reviewscraper.games.models.Game;
import com.reviewscraper.games.service.IGameService;
import com.reviewscraper.games.service.IScrapeService;



@RestController
public class GameController {
	@Autowired
	private IGameService iGameService;
	
	@Autowired
	private IScrapeService iscrapeService;

	@GetMapping("/game/{gameTitle}")
	public gameTitleDTO findByGameTitle(@PathVariable String gameTitle){
		gameTitleDTO dummy = new gameTitleDTO();
		List<Game> foundGames = new ArrayList<Game>();
		for(Game game : iGameService.findAll()) {
			if(game.getGameTitle().contains(gameTitle)) {
				
				
				
				foundGames.add(game);
			}

		}
		if(foundGames.isEmpty()) {
			dummy.setMessage("Found no matches!");
			dummy.setSuccess(false);
		}
		else {
			dummy.setFoundGames(foundGames);
			
			dummy.setMessage("Match found");
			dummy.setSuccess(true);
		}
		return dummy;}
	
	@PostMapping("/game")
	public Game create(@RequestBody Game game) {
		return this.iGameService.create(game);
	}
	
	@GetMapping("/game/all")
	public gameTitleDTO findAll() {
		
		gameTitleDTO dummy = new gameTitleDTO();  // Heb dit even anders gedaan, zodat we met dezelfde tabel in frontend de games uit gameTitleDTIO.foundGames kunnen lezen
		List<Game> foundGames = new ArrayList<Game>();
		
		for (Game game : this.iGameService.findAll()) { 
			foundGames.add(game);			
		}
		if(foundGames.isEmpty()) {
			dummy.setMessage("Found no matches!");
			dummy.setSuccess(false);
		}
		else {
			dummy.setFoundGames(foundGames);
			
			dummy.setMessage("Match found");
			dummy.setSuccess(true);
		}
		return dummy;
		
		//return this.iPersonService.findAll();
	}
	
	
	@GetMapping("/game/titlesuggest/{input}")
	public suggestTitleDTO titleDTO(@PathVariable String input){
		suggestTitleDTO dummy = new suggestTitleDTO();
		GameStringFixer fixer = new GameStringFixer();
		List<String> foundTitles = new ArrayList<String>();
		for(Game game : this.iGameService.findAll()) {
			if(fixer.fixSearchString(input, game.getGameTitle())) {
				
				foundTitles.add(game.getGameTitle());
			}
			
		}
		dummy.setTitles(foundTitles);
		return dummy;
	}
	
	
	@GetMapping("/game/scrape/{gameTitle}")
	public gameTitleDTO findGameReview(@PathVariable String gameTitle) {
		
		String fixedTitle = iscrapeService.getScrapeService(gameTitle);
		String zoekStringZonderPlusjes = iscrapeService.getInputOfUser();
		
		GameStringFixer deGameStringFixer = new GameStringFixer();
		
		gameTitleDTO gametitleDTO = new gameTitleDTO();
		
		//List<Game> foundGames = new ArrayList<Game>();
		
		
		
		List<Game> foundGames = iscrapeService.searchGamesInDatabase(iGameService.findAll(), gamepie -> deGameStringFixer.getAdvancedStringFixer(zoekStringZonderPlusjes, gamepie.getGameTitle()));
		System.out.println("lambda met de gevonden games: " + foundGames);
		//foundGames.addAll(gevondenGames);
		
		
		/*
		for(Game game : iGameService.findAll()) {
			if(game.getGameTitle().contains(fixedTitle)) {
				System.out.println("op het moment dat het spel nog niet in de database stond: " + game.getGameTitle());
				System.out.println("op het moment dat het spel nog niet in de database stond: " + game.getReviews());
				foundGames.add(game);
			}

		}
		*/
		if(foundGames.isEmpty()) {
			gametitleDTO.setMessage("Found no matches!");
			gametitleDTO.setSuccess(false);
		}
		else {
			
			
			
			
			
			
			
			gametitleDTO.setFoundGames(foundGames);
			gametitleDTO.setMessage("Match found");
			gametitleDTO.setSuccess(true);
		}
		
		return gametitleDTO;
		
		
	} //end find game review
	
	
	
	
	

}
