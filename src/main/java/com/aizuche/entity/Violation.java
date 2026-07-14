package com.aizuche.entity;
import java.math.BigDecimal;
import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "t_violation")
public class Violation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "order_no")
    private String orderNo;

    @Column(name = "customer_id")
    private Long customerId;

    @Column(name = "customer_name")
    private String customerName;

    @Column(name = "vehicle_plate")
    private String vehiclePlate;

    @Column(name = "violation_time")
    private LocalDateTime violationTime;

    private String location;
    private Integer points;

    @Column(name = "fine_amount")
    private BigDecimal fineAmount;

    private String status; // PENDING, RESOLVED

    @Column(name = "resolved_time")
    private LocalDateTime resolvedTime;

    private String resolver;

    @Column(columnDefinition = "TEXT")
    private String remark;

    @Column(name = "create_time")
    private LocalDateTime createTime;

    @Column(name = "update_time")
    private LocalDateTime updateTime;
}