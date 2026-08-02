package com.example.inventory_service.service;

import com.example.inventory_service.dto.ReviewRequest;
import com.example.inventory_service.entity.Review;
import com.example.inventory_service.repository.ProductRepository;
import com.example.inventory_service.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final ProductRepository productRepository;

    public Review addReview(Long productId, String userEmail, ReviewRequest request) {
        if (!productRepository.existsById(productId)) {
            throw new RuntimeException("Product not found");
        }

        if (request.getRating() == null || request.getRating() < 1 || request.getRating() > 5) {
            throw new RuntimeException("Rating must be between 1 and 5");
        }

        Review review = new Review();
        review.setProductId(productId);
        review.setUserEmail(userEmail);
        review.setRating(request.getRating());
        review.setComment(request.getComment());
        review.setCreatedAt(LocalDateTime.now());

        return reviewRepository.save(review);
    }

    public List<Review> getReviewsForProduct(Long productId) {
        return reviewRepository.findByProductIdOrderByCreatedAtDesc(productId);
    }
}