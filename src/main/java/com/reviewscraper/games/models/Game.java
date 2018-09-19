package com.reviewscraper.games.models;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;

@Entity
public class Game {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY) 
		private Long id;

	private String gameTitle;
	private String gameStudio;
	private String releaseDate;
	
	private double avgScore;
	private double topScore;
	private double lowestScore;
	
	
	
	@OneToMany(mappedBy="game")
	@JsonManagedReference
	private List<Review> reviews;
	
	
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
	public List<Review> getReviews() {
		return reviews;
	}
	public void setReviews(List<Review> reviews) {
		this.reviews = reviews;
	}
	public double getAvgScore() {
		return avgScore;
	}
	public void setAvgScore(double avgScore) {
		this.avgScore = avgScore;
	}
	public double getTopScore() {
		return topScore;
	}
	public void setTopScore(double topScore) {
		this.topScore = topScore;
	}
	public double getLowestScore() {
		return lowestScore;
	}
	public void setLowestScore(double lowestScore) {
		this.lowestScore = lowestScore;
	}
	

}
