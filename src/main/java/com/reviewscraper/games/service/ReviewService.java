package com.reviewscraper.games.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.reviewscraper.games.dao.IReviewDAO;
import com.reviewscraper.games.models.Game;
import com.reviewscraper.games.models.Review;

@Service 
public class ReviewService implements IReviewService {

	@Autowired 
	private IReviewDAO iReviewDAO;
	
	@Override
	public List<Review> findByGame(Game game) {
		
		return this.iReviewDAO.findByGame(game);
	}

}
