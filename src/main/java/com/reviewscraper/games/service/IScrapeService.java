package com.reviewscraper.games.service;

import java.util.List;
import java.util.function.Predicate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.reviewscraper.games.dao.IReviewDAO;
import com.reviewscraper.games.models.Game;
import com.reviewscraper.games.models.Review;

@Service
public interface IScrapeService {


	List<Review> findByGame(Game game);
	
	public Review create(Review address);
	
	public String getScrapeService(String searchString);
	
	public List<Game> searchGamesInDatabase (List<Game> deSpellen, Predicate<Game> checker);
	
	public String getInputOfUser ();
}
