package com.reviewscraper.games.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.reviewscraper.games.dto.gameTitleDTO;
import com.reviewscraper.games.dto.getAllGamesInListDTO;
import com.reviewscraper.games.models.Game;
import com.reviewscraper.games.service.IGameService;



@RestController
public class GameController {
	@Autowired
	private IGameService iGameService;

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
	public List<getAllGamesInListDTO> findAll() {
		
		List<getAllGamesInListDTO> getAllGamesInListDTO = new ArrayList<getAllGamesInListDTO>();
		
		for (Game game : this.iGameService.findAll()) {
			getAllGamesInListDTO gamelistDTO = new getAllGamesInListDTO();
			gamelistDTO.setId(game.getId());
			gamelistDTO.setGameTitle(game.getGameTitle());
			gamelistDTO.setId(game.getId());
			gamelistDTO.setGameStudio(game.getGameStudio());
			gamelistDTO.setReleaseDate(game.getReleaseDate());
			
			getAllGamesInListDTO.add(gamelistDTO);
		}
		
		return getAllGamesInListDTO;
		
		//return this.iPersonService.findAll();
	}
	
	
	
	
	

}
