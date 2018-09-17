package com.reviewscraper.games.service;

import java.util.List;

import com.reviewscraper.games.models.Game;

public interface IGameService {
	
	
	List<Game> findByGameTitle(String gametitle);
	List<Game> findByGameStudio(String gamestudio);
	List<Game> findAll();

	public Game create(Game game);

	public void update(Game game);

	public void delete(Game game);
}
