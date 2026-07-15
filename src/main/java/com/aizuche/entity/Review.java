package com.aizuche.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "t_review")
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "customer_id")
    private Long customerId;

    @Column(name = "customer_name")
    private String customerName;

    @Column(name = "order_id")
    private Long orderId;

    @Column(name = "order_no")
    private String orderNo;

    @Column(name = "vehicle_id")
    private Long vehicleId;

    @Column(name = "vehicle_brand")
    private String vehicleBrand;

    @Column(name = "vehicle_model")
    private String vehicleModel;

    private Integer rating; // 1-5 星

    @Column(columnDefinition = "TEXT")
    private String content;

    private Integer likes;  // 点赞数

    private Integer views;  // 浏览数

    @Column(name = "create_time")
    private LocalDateTime createTime;

    @Column(name = "update_time")
    private LocalDateTime updateTime;
}
