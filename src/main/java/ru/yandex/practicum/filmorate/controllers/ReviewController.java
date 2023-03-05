package ru.yandex.practicum.filmorate.controllers;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.service.ReviewService;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/reviews")
@Slf4j
public class ReviewController {
    private final ReviewService reviewService;

    @PostMapping
    public Review createReview(@Valid @RequestBody Review review) {
        log.debug("Получен запрос " + RequestMethod.POST + " по адресу: /reviews");
        return reviewService.createReview(review);
    }

    @PutMapping
    public Review updateReview(@Valid @RequestBody Review review) {
        log.debug("Получен запрос " + RequestMethod.PUT + " по адресу: /reviews");
        return reviewService.updateReview(review);
    }

    @DeleteMapping("/{id}")
    public void deleteReview(@PathVariable("id") Long reviewId) {
        log.debug("Получен запрос " + RequestMethod.DELETE + " по адресу: /reviews/" + reviewId);
        reviewService.deleteReview(reviewId);
    }

    @GetMapping("{id}")
    public Review getReviewById(@PathVariable("id") Long reviewId) {
        log.debug("Получен запрос " + RequestMethod.GET + " по адресу: /reviews/" + reviewId);
        return reviewService.getReviewById(reviewId);
    }

    @GetMapping
    public List<Review> getAllReviews(@RequestParam(required = false, name = "filmId") Long filmId,
                                      @RequestParam(name = "count", defaultValue = "10") Long count) {
        log.debug("Получен запрос " + RequestMethod.GET + " по адресу: /reviews?filmId=" +
                filmId +"&count={count}" + count);

        List<Review> reviews = new ArrayList<>();

        if (filmId == null) {
            reviews = reviewService.getAllReviews();
        } else {
            reviews = reviewService.getAllReviewsFromFilm(filmId, count);
        }
        return reviews;
    }

    @PutMapping("/{id}/like/{userId}")
    public void addLike(@PathVariable("id") Long reviewId, @PathVariable("userId") Long userId) {
        log.debug("Получен запрос " + RequestMethod.PUT + " по адресу: /" + reviewId +
                "/like/" + userId);
        reviewService.addLike(reviewId, userId, true);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void deleteLike(@PathVariable("id") Long reviewId, @PathVariable("userId") Long userId) {
        log.debug("Получен запрос " + RequestMethod.DELETE + " по адресу: /" + reviewId +
                "/like/" + userId);
        reviewService.deleteLike(reviewId, userId, true);
    }

    @PutMapping("/{id}/dislike/{userId}")
    public void addDislike(@PathVariable("id") Long reviewId, @PathVariable("userId") Long userId) {
        log.debug("Получен запрос " + RequestMethod.PUT + " по адресу: /" + reviewId +
                "/dislike/" + userId);
        reviewService.addDislike(reviewId, userId, false);
    }

    @DeleteMapping("/{id}/dislike/{userId}")
    public void deleteDislike(@PathVariable("id") Long reviewId, @PathVariable("userId") Long userId) {
        log.debug("Получен запрос " + RequestMethod.DELETE + " по адресу: /" + reviewId +
                "/dislike/" + userId);
        reviewService.deleteDislike(reviewId, userId, false);
    }

}
