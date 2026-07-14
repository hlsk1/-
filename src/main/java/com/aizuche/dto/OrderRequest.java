package com.aizuche.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class OrderRequest {
    private Long customerId;
    private Long vehicleId;
    private LocalDateTime pickupTime;
    private LocalDateTime returnTime;
    private String remark;
}