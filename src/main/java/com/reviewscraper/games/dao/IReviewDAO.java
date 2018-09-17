package com.reviewscraper.games.dao;

import org.springframework.data.repository.CrudRepository;

import com.reviewscraper.games.models.Review;
import com.reviewscraper.games.models.Game;
import java.util.List;

public interface IReviewDAO extends CrudRepository<Review, Long>{

	List<Review> findByGame(Game game);
}
