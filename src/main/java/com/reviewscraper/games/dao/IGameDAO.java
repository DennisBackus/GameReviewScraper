package com.reviewscraper.games.dao;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.reviewscraper.games.models.Game;

public interface IGameDAO extends CrudRepository<Game, Long> {

	List<Game> findByGameTitle(String gametitle);
	List<Game> findByGameStudio(String gamestudio);
	List<Game> findAll();
	
	
}
