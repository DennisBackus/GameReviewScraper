package com.reviewscraper.games.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.reviewscraper.games.dao.IReviewDAO;
import com.reviewscraper.games.models.Game;
import com.reviewscraper.games.models.Review;

@Service
public interface IScrapeService {


	List<Review> findByGame(Game game);
	
	public Review create(Review address);
	
	public void getScrapeService(String searchString);
	
	
	
}
