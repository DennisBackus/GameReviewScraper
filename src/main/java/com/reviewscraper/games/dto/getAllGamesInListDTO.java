	package com.reviewscraper.games.dto;

import java.util.List;

import javax.persistence.OneToMany;

import com.reviewscraper.games.models.Review;

public class getAllGamesInListDTO {

	private Long id;

	private String gameTitle;
	private String gameStudio;
	private String releaseDate;
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getGameTitle() {
		return gameTitle;
	}
	public void setGameTitle(String gameTitle) {
		this.gameTitle = gameTitle;
	}
	public String getGameStudio() {
		return gameStudio;
	}
	public void setGameStudio(String gameStudio) {
		this.gameStudio = gameStudio;
	}
	public String getReleaseDate() {
		return releaseDate;
	}
	public void setReleaseDate(String releaseDate) {
		this.releaseDate = releaseDate;
	}
	
	//private List<Review> reviews;
	
	
	
}
