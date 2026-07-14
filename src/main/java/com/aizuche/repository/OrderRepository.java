package com.aizuche.repository;

import com.aizuche.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    @Query("SELECT o FROM Order o WHERE " +
            "(:startTime IS NULL OR o.pickupTime >= :startTime) AND " +
            "(:endTime IS NULL OR o.pickupTime <= :endTime) AND " +
            "(:status IS NULL OR o.status = :status) AND " +
            "(:customerName IS NULL OR o.customerName LIKE %:customerName%) AND " +
            "(:vehiclePlate IS NULL OR o.vehiclePlate LIKE %:vehiclePlate%)")
    List<Order> findByConditions(@Param("startTime") LocalDateTime startTime,
                                 @Param("endTime") LocalDateTime endTime,
                                 @Param("status") String status,
                                 @Param("customerName") String customerName,
                                 @Param("vehiclePlate") String vehiclePlate);

    @Query("SELECT o FROM Order o WHERE o.status = 'PENDING' AND DATE(o.pickupTime) = CURRENT_DATE")
    List<Order> findTodayPickupOrders();

    @Query("SELECT o FROM Order o WHERE o.status = 'PICKED_UP' AND DATE(o.returnTime) = CURRENT_DATE")
    List<Order> findTodayReturnOrders();

    List<Order> findByCustomerId(Long customerId);

    @Query("SELECT COUNT(o) FROM Order o WHERE o.status = 'COMPLETED' AND o.createTime BETWEEN :start AND :end")
    Long countCompletedOrdersBetween(@Param("start") LocalDateTime start,
                                     @Param("end") LocalDateTime end);
}