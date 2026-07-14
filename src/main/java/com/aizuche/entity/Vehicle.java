
package com.aizuche.entity;
import java.math.BigDecimal;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
@Data
@Entity
@Table(name = "t_vehicle")
public class Vehicle {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String brand;
    private String model;

    @Column(name = "plate_number", unique = true)
    private String plateNumber;

    private String color;

    @Column(name = "seat_count")
    private Integer seatCount;

    @Column(name = "daily_price")
    private BigDecimal dailyPrice;

    private String status; // AVAILABLE, RENTED, REPAIR, OFFLINE

    @Column(name = "parking_spot")
    private String parkingSpot;

    @Column(name = "parking_time")
    private LocalDateTime parkingTime;

    @Column(name = "repair_reason")
    private String repairReason;

    @Column(name = "repair_expected_time")
    private LocalDateTime repairExpectedTime;

    @Column(name = "image_urls", columnDefinition = "TEXT")
    private String imageUrls;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "create_time")
    private LocalDateTime createTime;

    @Column(name = "update_time")
    private LocalDateTime updateTime;
}