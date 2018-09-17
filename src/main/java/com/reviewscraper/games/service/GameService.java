package com.reviewscraper.games.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import com.reviewscraper.games.dao.IGameDAO;
import com.reviewscraper.games.models.Game;

@Service
public class GameService implements IGameService {

	@Autowired
	private IGameDAO iGameDAO;
	
	@Override
	public List<Game> findByGameTitle(String gametitle) {
		return this.iGameDAO.findByGameTitle(gametitle); 
	}

	@Override
	public List<Game> findByGameStudio(String gamestudio) {
		return this.iGameDAO.findByGameStudio(gamestudio);
	}

	@Override
	public List<Game> findAll() {
		return this.iGameDAO.findAll();
	}

	@Override
	public Game create(Game game) {
		Assert.notNull(game, "Game may not be null");
		return this.iGameDAO.save(game);
	}

	@Override
	public void update(Game game) {
		Assert.notNull(game, "Game may not be null");
		 this.iGameDAO.save(game);
		 
	}

	@Override
	public void delete(Game game) {
		Assert.notNull(game, "Game may not be null");
		 this.iGameDAO.delete(game);
	}

}
