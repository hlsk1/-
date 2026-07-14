package com.aizuche.entity;
import java.math.BigDecimal;
import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "t_order")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "order_no", unique = true)
    private String orderNo;

    @Column(name = "customer_id")
    private Long customerId;

    @Column(name = "vehicle_id")
    private Long vehicleId;

    @Column(name = "customer_name")
    private String customerName;

    @Column(name = "customer_phone")
    private String customerPhone;

    @Column(name = "vehicle_brand")
    private String vehicleBrand;

    @Column(name = "vehicle_model")
    private String vehicleModel;

    @Column(name = "vehicle_plate")
    private String vehiclePlate;

    @Column(name = "pickup_time")
    private LocalDateTime pickupTime;

    @Column(name = "return_time")
    private LocalDateTime returnTime;

    @Column(name = "actual_return_time")
    private LocalDateTime actualReturnTime;

    @Column(name = "rent_days")
    private Integer rentDays;

    @Column(name = "daily_price")
    private BigDecimal dailyPrice;

    @Column(name = "total_amount")
    private BigDecimal totalAmount;

    @Column(name = "deposit_amount")
    private BigDecimal depositAmount;

    @Column(name = "final_amount")
    private BigDecimal finalAmount;

    private String status; // PENDING, PICKED_UP, RETURNED, CANCELLED, COMPLETED

    @Column(name = "car_condition", columnDefinition = "TEXT")
    private String carCondition;

    @Column(name = "car_condition_images", columnDefinition = "TEXT")
    private String carConditionImages;

    @Column(name = "cancel_reason")
    private String cancelReason;

    @Column(columnDefinition = "TEXT")
    private String remark;

    @Column(name = "create_time")
    private LocalDateTime createTime;

    @Column(name = "update_time")
    private LocalDateTime updateTime;
}