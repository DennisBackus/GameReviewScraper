package com.reviewscraper.games.dto;

import java.util.List;

import com.reviewscraper.games.models.Game;

public class gameTitleDTO {

	private boolean success;
	private List<Game> foundGames;
	private String message;
	
	
	
	public boolean isSuccess() {
		return success;
	}
	public void setSuccess(boolean success) {
		this.success = success;
	}
	public List<Game> getFoundGames() {
		return foundGames;
	}
	public void setFoundGames(List<Game> foundGames) {
		this.foundGames = foundGames;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
}
