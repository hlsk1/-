package com.aizuche.controller;

import com.aizuche.entity.Review;
import com.aizuche.service.ReviewService;
import com.aizuche.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/review")
public class ReviewController {

    @Autowired
    private ReviewService reviewService;

    @GetMapping("/list")
    public Result list() {
        List<Review> list = reviewService.findAll();
        return Result.success(list);
    }

    @GetMapping("/popular")
    public Result popular() {
        return Result.success(reviewService.findPopular());
    }

    @GetMapping("/vehicle/{vehicleId}")
    public Result byVehicle(@PathVariable Long vehicleId) {
        return Result.success(reviewService.findByVehicleId(vehicleId));
    }

    @GetMapping("/customer/{customerId}")
    public Result byCustomer(@PathVariable Long customerId) {
        return Result.success(reviewService.findByCustomerId(customerId));
    }

    @GetMapping("/check/{orderId}")
    public Result checkReviewed(@PathVariable Long orderId) {
        boolean reviewed = reviewService.hasReviewed(orderId);
        return Result.success(reviewed);
    }

    @PostMapping("/add")
    public Result add(@RequestBody Review review) {
        if (review.getOrderId() == null) return Result.error("订单ID不能为空");
        if (reviewService.hasReviewed(review.getOrderId())) {
            return Result.error("该订单已评价过");
        }
        if (review.getRating() == null || review.getRating() < 1 || review.getRating() > 5) {
            return Result.error("评分需在1-5之间");
        }
        if (review.getContent() == null || review.getContent().trim().isEmpty()) {
            return Result.error("评价内容不能为空");
        }
        reviewService.save(review);
        return Result.success("评价成功");
    }

    @PostMapping("/like/{id}")
    public Result like(@PathVariable Long id) {
        reviewService.incrementLikes(id);
        return Result.success("点赞成功");
    }

    @PostMapping("/view/{id}")
    public Result view(@PathVariable Long id) {
        reviewService.incrementViews(id);
        Review review = reviewService.findById(id).orElse(null);
        return Result.success(review);
    }
}
