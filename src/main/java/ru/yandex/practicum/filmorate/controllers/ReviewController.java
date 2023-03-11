package ru.yandex.practicum.filmorate.controllers;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.service.ReviewLikeService;
import ru.yandex.practicum.filmorate.service.ReviewService;
import ru.yandex.practicum.filmorate.utility.DefaultLogger;

import javax.validation.Valid;
import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/reviews")
public class ReviewController {
    private final ReviewService reviewService;
    private final ReviewLikeService reviewLikeService;
    private final DefaultLogger defaultLogger;

    @PostMapping
    public Review postReview(@Valid @RequestBody Review review) {
        defaultLogger.logRequestMethod(RequestMethod.POST, "/reviews");
        return reviewService.createReview(review);
    }

    @PutMapping
    public Review putReview(@Valid @RequestBody Review review) {
        defaultLogger.logRequestMethod(RequestMethod.PUT, "/reviews");
        return reviewService.updateReview(review);
    }

    @DeleteMapping("/{id}")
    public void deleteReviewById(@PathVariable("id") Long reviewId) {
        defaultLogger.logRequestMethod(RequestMethod.DELETE, "/reviews/" + reviewId);
        reviewService.deleteReview(reviewId);
    }

    @GetMapping("{id}")
    public Review getReviewById(@PathVariable("id") Long reviewId) {
        defaultLogger.logRequestMethod(RequestMethod.GET, "/reviews/" + reviewId);
        return reviewService.getReviewById(reviewId);
    }

    @GetMapping
    public List<Review> getAllReviews(@RequestParam(required = false) Long filmId,
                                      @RequestParam(name = "count", defaultValue = "10") Long count) {
        if (filmId == null) {
            defaultLogger.logRequestMethod(RequestMethod.GET, "/reviews?count=" + count);
            return reviewService.getAllReviews(count);
        } else {
            defaultLogger.logRequestMethod(RequestMethod.GET, "/reviews?filmId=" + filmId + "&count=" + count);
            return reviewService.getAllReviewsForFilm(filmId, count);
        }
    }

    @PutMapping("/{id}/{likeValue}/{userId}")
    public void addLikeOrDislike(@PathVariable("id") Long reviewId,
                                 @PathVariable("likeValue") String likeValue,
                                 @PathVariable("userId") Long userId) {
        defaultLogger.logRequestMethod(RequestMethod.PUT, "/reviews/" + reviewId + "/" + likeValue + "/" + userId);
        reviewLikeService.addLikeOrDislike(reviewId, userId, likeValue);
    }

    @DeleteMapping("/{id}/{likeValue}/{userId}")
    public void deleteLikeOrDislike(@PathVariable("id") Long reviewId,
                                    @PathVariable("likeValue") String likeValue,
                                    @PathVariable("userId") Long userId) {
        defaultLogger.logRequestMethod(RequestMethod.DELETE, "/reviews/" + reviewId + "/" + likeValue + "/" + userId);
        reviewLikeService.deleteLikeOrDislike(reviewId, userId, likeValue);
    }
}