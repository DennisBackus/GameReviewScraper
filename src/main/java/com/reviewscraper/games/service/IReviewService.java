package com.reviewscraper.games.service;

import java.util.List;

import com.reviewscraper.games.models.Game;
import com.reviewscraper.games.models.Review;

public interface IReviewService {

	List<Review> findByGame(Game game);
	
}
