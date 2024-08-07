package com.mymart.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mymart.model.Product;
import com.mymart.model.Rating;
import com.mymart.model.User;

import com.mymart.repository.RatingRepository;

@Service
public class RatingService {

    private final RatingRepository ratingRepository;
    private final ProductService productService;

    @Autowired
    public RatingService(RatingRepository ratingRepository, ProductService productService) {
        this.ratingRepository = ratingRepository;
        this.productService = productService;
       
    }

    @Transactional
    public void rateProduct(User user, Product product, double rating) {
        if (rating < 1 || rating > 5) {
            throw new IllegalArgumentException("Invalid rating value");
        }
        } 
        
    @Cacheable("averageRatings")
    public double calculateAverageRating(Product product) {
        if (product == null) {
            return 0.0;
        }
        List<Rating> ratings = product.getRatings();
        if (ratings.isEmpty()) {
            return 0.0;
        }
        double sum = 0;
        for (Rating rating : ratings) {
            sum += rating.getRating();
        }
        double averageRating = sum / ratings.size();
        averageRating = Math.round(averageRating * 10.0) / 10.0; // Round to one decimal place
        return averageRating;
    }

   
    public String determineRatingColor(double rating, boolean isUserRating) {
        if (isUserRating && rating == 0) {
            return "#A9A9A9"; 
        } else if (rating > 0 && rating <= 1.0) {
            return "#C70039";
        } else if (rating > 1.0 && rating < 2.0) {
            return "#C9CC3F";
        } else if (rating >= 2.0 && rating < 3.0) {
            return "#93C572";
        } else if (rating >= 3.0 && rating < 4.0) {
            return "#008000";
        } else {
            return "#355E3B";
        }
    }


    public Sort getSortSpecification(String sortField) {
        switch (sortField) {
            case "oldestReviews":
                return Sort.by(Direction.ASC, "dateTime");
            case "highRatings":
                return Sort.by(Direction.DESC, "rating");
            case "lowRatings":
                return Sort.by(Direction.ASC, "rating");
            
            case "latestReviews":
            default:
                return Sort.by(Direction.DESC, "dateTime");
        }
    }
    
   
}
