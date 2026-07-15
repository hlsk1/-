package com.aizuche.repository;

import com.aizuche.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

    List<Review> findByOrderId(Long orderId);

    List<Review> findByCustomerId(Long customerId);

    List<Review> findByVehicleId(Long vehicleId);

    boolean existsByOrderId(Long orderId);

    @Query("SELECT r FROM Review r ORDER BY r.likes DESC, r.createTime DESC")
    List<Review> findAllOrderByPopular();
}
