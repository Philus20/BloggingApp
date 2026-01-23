package org.example.bloggingapp.Services;

import org.example.bloggingapp.Database.DbInterfaces.IService;
import org.example.bloggingapp.Database.Repositories.ReviewRepository;
import org.example.bloggingapp.Models.ReviewEntity;

import java.util.List;

public class ReviewService implements IService<ReviewEntity> {
    
    private final ReviewRepository reviewRepository;
    
    public ReviewService(ReviewRepository reviewRepository) {
        this.reviewRepository = reviewRepository;
    }
    
    @Override
    public ReviewEntity create(ReviewEntity review) {
        reviewRepository.create(review);
        return review;
    }
    
    @Override
    public ReviewEntity findById(int id) {
        return reviewRepository.findByInteger(id);
    }
    
    @Override
    public ReviewEntity findByString(String identifier) {
        return reviewRepository.findByString(identifier);
    }
    
    @Override
    public List<ReviewEntity> findAll() {
        return reviewRepository.findAll();
    }
    
    @Override
    public ReviewEntity update(int id, ReviewEntity review) {
        ReviewEntity existingReview = findById(id);
        if (existingReview != null) {
            review.setReviewId(id);
            reviewRepository.updateById(id);
            return review;
        }
        return null;
    }
    
    @Override
    public boolean delete(int id) {
        ReviewEntity existingReview = findById(id);
        if (existingReview != null) {
            reviewRepository.delete(id);
            return true;
        }
        return false;
    }
    
    public List<ReviewEntity> findByPostId(int postId) {
        return findAll().stream()
                .filter(review -> review.getPostId() == postId)
                .toList();
    }
    
    public List<ReviewEntity> findByUserId(int userId) {
        return findAll().stream()
                .filter(review -> review.getUserId() == userId)
                .toList();
    }
    
    public List<ReviewEntity> findByRating(int rating) {
        return findAll().stream()
                .filter(review -> review.getRating() == rating)
                .toList();
    }
}
